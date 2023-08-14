package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPotionAddedEffect implements IMessage<SyncPotionAddedEffect> {
	public int entityId;
	public int effectId;
	public int duration;
	public int amplifier;

	public SyncPotionAddedEffect() { /* Nothing to do */ }

	public SyncPotionAddedEffect(int playerId, int effectId, int duration, int amplifier) {
		entityId = playerId;
		this.effectId = effectId;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	@Override
	public void encode(final SyncPotionAddedEffect message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.entityId);
		buffer.writeInt(message.effectId);
		buffer.writeInt(message.duration);
		buffer.writeInt(message.amplifier);
	}

	@Override
	public SyncPotionAddedEffect decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		int effectId = buffer.readInt();
		int duration = buffer.readInt();
		int amplifier = buffer.readInt();

		return new SyncPotionAddedEffect(playerId, effectId, duration, amplifier);
	}

	@Override
	public void handle(final SyncPotionAddedEffect message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncPotionAddedEffect(message));
		}

		context.setPacketHandled(true);
	}
}