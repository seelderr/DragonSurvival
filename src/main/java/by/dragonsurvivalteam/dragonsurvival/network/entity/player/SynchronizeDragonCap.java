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
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

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
	public void encode(SynchronizeDragonCap message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeByte(message.dragonType.ordinal());
		buffer.writeBoolean(message.hiding);
		buffer.writeDouble(message.size);
		buffer.writeBoolean(message.hasWings);
		buffer.writeInt(message.lavaAirSupply);
		buffer.writeInt(message.passengerId);
	}

	@Override
	public SynchronizeDragonCap decode(PacketBuffer buffer){
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
	public void handle(SynchronizeDragonCap message, Supplier<Context> supplier){
		if(supplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER){
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), message);
			ServerPlayerEntity serverPlayerEntity = supplier.get().getSender();
			DragonStateProvider.getCap(serverPlayerEntity).ifPresent(dragonStateHandler -> {

				if(message.dragonType == DragonType.NONE && dragonStateHandler.getType() != DragonType.NONE){
					DragonCommand.reInsertClawTools(serverPlayerEntity, dragonStateHandler);
				}

				dragonStateHandler.setIsHiding(message.hiding);
				dragonStateHandler.setType(message.dragonType);
				dragonStateHandler.setSize(message.size, serverPlayerEntity);
				dragonStateHandler.setHasWings(message.hasWings);
				dragonStateHandler.setLavaAirSupply(message.lavaAirSupply);
				dragonStateHandler.setPassengerId(message.passengerId);
				serverPlayerEntity.setForcedPose(null);
				serverPlayerEntity.refreshDimensions();
			});
		}else{
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SynchronizeDragonCap message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			ClientPlayerEntity myPlayer = Minecraft.getInstance().player;
			if(myPlayer != null){
				World world = myPlayer.level;

				if(ClientDragonRender.dragonArmor != null){
					ClientDragonRender.dragonArmor.player = myPlayer.getId();
				}
				PlayerEntity thatPlayer = (PlayerEntity)world.getEntity(message.playerId);

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
						DragonEntity dragonEntity = DSEntities.DRAGON.create(world);
						dragonEntity.player = thatPlayer.getId();
						ClientDragonRender.playerDragonHashMap.computeIfAbsent(thatPlayer.getId(), integer -> new AtomicReference<>(dragonEntity)).getAndSet(dragonEntity);
					}
					thatPlayer.setForcedPose(null);
					thatPlayer.refreshDimensions();
				}
			}
			context.setPacketHandled(true);
		});
	}
}