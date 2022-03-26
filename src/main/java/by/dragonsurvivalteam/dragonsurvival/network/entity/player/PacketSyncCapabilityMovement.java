package by.dragonsurvivalteam.dragonsurvival.network.entity.player;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketSyncCapabilityMovement implements IMessage<PacketSyncCapabilityMovement>{

	public int playerId;
	public double bodyYaw;
	public double headYaw;
	public double headPitch;
	public boolean bite;

	public PacketSyncCapabilityMovement(){
	}

	public PacketSyncCapabilityMovement(int playerId, double bodyYaw, double headYaw, double headPitch, boolean bite){
		this.bodyYaw = bodyYaw;
		this.headYaw = headYaw;
		this.headPitch = headPitch;
		this.playerId = playerId;
		this.bite = bite;
	}

	@Override
	public void encode(PacketSyncCapabilityMovement m, FriendlyByteBuf b){
		b.writeInt(m.playerId);
		b.writeDouble(m.bodyYaw);
		b.writeDouble(m.headYaw);
		b.writeDouble(m.headPitch);
		b.writeBoolean(m.bite);
	}

	@Override
	public PacketSyncCapabilityMovement decode(FriendlyByteBuf b){
		return new PacketSyncCapabilityMovement(b.readInt(), b.readDouble(), b.readDouble(), b.readDouble(), b.readBoolean());
	}

	@Override
	public void handle(PacketSyncCapabilityMovement syncCapabilityMovement, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		ServerPlayer player = context.getSender();
		if(player == null){
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(syncCapabilityMovement, supplier));
			return;
		}
		Entity entity = player.level.getEntity(syncCapabilityMovement.playerId);
		if(entity == null || entity.level == null){
			context.setPacketHandled(true);
			return;
		}
		if(entity instanceof Player){
			DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
				dragonStateHandler.setMovementData(syncCapabilityMovement.bodyYaw, syncCapabilityMovement.headYaw, syncCapabilityMovement.headPitch, syncCapabilityMovement.bite);
			});
		}
		if(!entity.level.isClientSide){
			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), syncCapabilityMovement);
		}
		context.setPacketHandled(true);
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(PacketSyncCapabilityMovement message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.setMovementData(message.bodyYaw, message.headYaw, message.headPitch, message.bite);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}