package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;

import java.util.function.Supplier;

public class SyncFlightSpeed implements IMessage<SyncFlightSpeed> {
	public int playerId;
	public Vec3 flightSpeed;

	public SyncFlightSpeed() { /* Nothing to do */ }

	public SyncFlightSpeed(int playerId, final Vec3 flightSpeed) {
		this.playerId = playerId;
		this.flightSpeed = flightSpeed;
	}

	@Override
	public void encode(final SyncFlightSpeed message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeDouble(message.flightSpeed.x);
		buffer.writeDouble(message.flightSpeed.y);
		buffer.writeDouble(message.flightSpeed.z);
	}

	@Override
	public SyncFlightSpeed decode(final FriendlyByteBuf buffer) {
		return new SyncFlightSpeed(buffer.readInt(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
	}

	@Override
	public void handle(final SyncFlightSpeed message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncFlightSpeed(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				sender.setDeltaMovement(message.flightSpeed);
				TargetPoint point = new TargetPoint(sender, sender.position().x, sender.position().y, sender.position().z, 32, sender.level.dimension());
				NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncFlightSpeed(sender.getId(), message.flightSpeed));
			}
		}

		context.setPacketHandled(true);
	}
}