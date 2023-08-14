package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGrowthState implements IMessage<SyncGrowthState> {
	public boolean growing;

	public SyncGrowthState(boolean growing) {
		this.growing = growing;
	}

	public SyncGrowthState() { /* Nothing to do */ }

	@Override
	public void encode(final SyncGrowthState message, final FriendlyByteBuf buffer) {
		buffer.writeBoolean(message.growing);
	}

	@Override
	public SyncGrowthState decode(final FriendlyByteBuf buffer) {
		return new SyncGrowthState(buffer.readBoolean());
	}

	@Override
	public void handle(final SyncGrowthState message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncGrowthState(message));
		}

		context.setPacketHandled(true);
	}
}