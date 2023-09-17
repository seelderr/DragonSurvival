package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketSyncCapabilityMovement implements IMessage<PacketSyncCapabilityMovement> {
	public int playerId;
	public double bodyYaw;
	public double headYaw;
	public double headPitch;
	public boolean bite;

	public PacketSyncCapabilityMovement() { /* Nothing to do */ }

	public PacketSyncCapabilityMovement(int playerId, double bodyYaw, double headYaw, double headPitch, boolean bite) {
		this.bodyYaw = bodyYaw;
		this.headYaw = headYaw;
		this.headPitch = headPitch;
		this.playerId = playerId;
		this.bite = bite;
	}

	@Override
	public void encode(final PacketSyncCapabilityMovement message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeDouble(message.bodyYaw);
		buffer.writeDouble(message.headYaw);
		buffer.writeDouble(message.headPitch);
		buffer.writeBoolean(message.bite);
	}

	@Override
	public PacketSyncCapabilityMovement decode(final FriendlyByteBuf buffer) {
		return new PacketSyncCapabilityMovement(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean());
	}

	@Override
	public void handle(final PacketSyncCapabilityMovement message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handlePacketSyncCapabilityMovement(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				Entity entity = sender.level().getEntity(message.playerId);

				if (entity instanceof Player player) {
					context.enqueueWork(() -> DragonStateProvider.getCap(player).ifPresent(handler -> handler.setMovementData(message.bodyYaw, message.headYaw, message.headPitch, message.bite)));
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), message);
				}
			}
		}

		context.setPacketHandled(true);
	}
}