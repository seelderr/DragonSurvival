package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Synchronizes dragon level and size */
public class SyncSize implements IMessage<SyncSize> {
	public int playerId;
	public double size;

	public SyncSize(int playerId, double size) {
		this.playerId = playerId;
		this.size = size;
	}

	public SyncSize() { /* Nothing to do */ }

	@Override
	public void encode(final SyncSize message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeDouble(message.size);
	}

	@Override
	public SyncSize decode(final FriendlyByteBuf buffer) {
		return new SyncSize(buffer.readInt(), buffer.readDouble());
	}

	@Override
	public void handle(final SyncSize message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncSize(message));
		}

		context.setPacketHandled(true);
	}
}