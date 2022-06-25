package by.dragonsurvivalteam.dragonsurvival.network.entity.player;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SynchronizeDragonCap implements IMessage<SynchronizeDragonCap>{

	public int playerId;
	public boolean hiding;
	public DragonType dragonType;
	public double size;
	public boolean hasWings;
	public int lavaAirSupply;
	public int passengerId;

	public SynchronizeDragonCap(){
	}

	public SynchronizeDragonCap(int playerId, boolean hiding, DragonType dragonType, double size, boolean hasWings, int lavaAirSupply, int passengerId){
		this.playerId = playerId;
		this.hiding = hiding;
		this.dragonType = dragonType;
		this.size = size;
		this.hasWings = hasWings;
		this.lavaAirSupply = lavaAirSupply;
		this.passengerId = passengerId;
	}

	@Override
	public void encode(SynchronizeDragonCap message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeByte(message.dragonType.ordinal());
		buffer.writeBoolean(message.hiding);
		buffer.writeDouble(message.size);
		buffer.writeBoolean(message.hasWings);
		buffer.writeInt(message.lavaAirSupply);
		buffer.writeInt(message.passengerId);
	}

	@Override
	public SynchronizeDragonCap decode(FriendlyByteBuf buffer){
		int id = buffer.readInt();
		DragonType type = DragonType.values()[buffer.readByte()];
		boolean hiding = buffer.readBoolean();
		double size = buffer.readDouble();
		boolean hasWings = buffer.readBoolean();
		int lavaAirSupply = buffer.readInt();
		int passengerId = buffer.readInt();
		return new SynchronizeDragonCap(id, hiding, type, size, hasWings, lavaAirSupply, passengerId);
	}

	@Override
	public void handle(SynchronizeDragonCap message, Supplier<NetworkEvent.Context> supplier){
		if(supplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER){
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), message);
			ServerPlayer serverPlayer = supplier.get().getSender();
			DragonStateProvider.getCap(serverPlayer).ifPresent(dragonStateHandler -> {

				if(message.dragonType == DragonType.NONE && dragonStateHandler.getType() != DragonType.NONE){
					DragonCommand.reInsertClawTools(serverPlayer, dragonStateHandler);
				}

				dragonStateHandler.setIsHiding(message.hiding);
				dragonStateHandler.setType(message.dragonType);
				dragonStateHandler.setSize(message.size, serverPlayer);
				dragonStateHandler.setHasWings(message.hasWings);
				dragonStateHandler.setLavaAirSupply(message.lavaAirSupply);
				dragonStateHandler.setPassengerId(message.passengerId);
				serverPlayer.setForcedPose(null);
				//serverPlayer.refreshDimensions();
			});
		}else{
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SynchronizeDragonCap message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			LocalPlayer myPlayer = Minecraft.getInstance().player;
			if(myPlayer != null){
				Level world = myPlayer.level;

				if(ClientDragonRender.dragonArmor != null){
					ClientDragonRender.dragonArmor.player = myPlayer.getId();
				}
				Player thatPlayer = (Player)world.getEntity(message.playerId);

				if(thatPlayer != null){
					DragonStateProvider.getCap(thatPlayer).ifPresent(dragonStateHandler -> {
						dragonStateHandler.setType(message.dragonType);
						dragonStateHandler.setIsHiding(message.hiding);
						dragonStateHandler.setHasWings(message.hasWings);
						dragonStateHandler.setSize(message.size);
						dragonStateHandler.setLavaAirSupply(message.lavaAirSupply);
						dragonStateHandler.setPassengerId(message.passengerId);
					});
					//refresh instances
					if(thatPlayer != myPlayer){
						DragonEntity dragon = DSEntities.DRAGON.create(world);
						dragon.player = thatPlayer.getId();
						ClientDragonRender.playerDragonHashMap.computeIfAbsent(thatPlayer.getId(), integer -> new AtomicReference<>(dragon)).getAndSet(dragon);
					}
					thatPlayer.setForcedPose(null);
					//thatPlayer.refreshDimensions();
				}
			}
			context.setPacketHandled(true);
		});
	}
}