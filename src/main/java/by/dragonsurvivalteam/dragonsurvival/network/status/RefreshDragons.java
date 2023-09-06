package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RefreshDragons implements IMessage<RefreshDragons> {
	public int playerId;

	public RefreshDragons(int playerId) {
		this.playerId = playerId;
	}

	public RefreshDragons() { /* Nothing to do */ }

	@Override
	public void encode(final RefreshDragons message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
	}

	@Override
	public RefreshDragons decode(final FriendlyByteBuf buffer) {
		return new RefreshDragons(buffer.readInt());
	}

	@Override
	public void handle(final RefreshDragons message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleRefreshDragons(message));
		}

		context.setPacketHandled(true);
	}
}