package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPotionRemovedEffect implements IMessage<SyncPotionRemovedEffect> {
	public int playerId;
	public int effectId;

	public SyncPotionRemovedEffect() { /* Nothing to do */ }

	public SyncPotionRemovedEffect(int playerId, int effectId) {
		this.playerId = playerId;
		this.effectId = effectId;
	}

	@Override

	public void encode(final SyncPotionRemovedEffect message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeInt(message.effectId);
	}

	@Override
	public SyncPotionRemovedEffect decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		int effectId = buffer.readInt();

		return new SyncPotionRemovedEffect(playerId, effectId);
	}

	@Override
	public void handle(final SyncPotionRemovedEffect message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncPotionRemovedEffect(message));
		}

		context.setPacketHandled(true);
	}
}