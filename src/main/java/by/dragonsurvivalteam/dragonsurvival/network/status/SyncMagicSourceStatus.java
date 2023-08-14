package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncMagicSourceStatus implements IMessage<SyncMagicSourceStatus> {
	public int playerId;
	public boolean state;
	public int timer;

	public SyncMagicSourceStatus() { /* Nothing to do */ }

	public SyncMagicSourceStatus(int playerId, boolean state, int timer) {
		this.playerId = playerId;
		this.state = state;
		this.timer = timer;
	}

	@Override
	public void encode(final SyncMagicSourceStatus message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
		buffer.writeInt(message.timer);
	}

	@Override
	public SyncMagicSourceStatus decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		int timer = buffer.readInt();
		return new SyncMagicSourceStatus(playerId, state, timer);
	}

	@Override
	public void handle(final SyncMagicSourceStatus message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncMagicSourceStatus(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				DragonStateProvider.getCap(sender).ifPresent(handler -> {
					handler.getMagicData().onMagicSource = message.state;
					handler.getMagicData().magicSourceTimer = message.timer;
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncMagicSourceStatus(sender.getId(), message.state, message.timer));
			}
		}

		context.setPacketHandled(true);
	}
}