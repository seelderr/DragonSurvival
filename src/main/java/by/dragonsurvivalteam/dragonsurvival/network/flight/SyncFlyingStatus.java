package by.dragonsurvivalteam.dragonsurvival.network.flight;

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

public class SyncFlyingStatus implements IMessage<SyncFlyingStatus> {
	public int playerId;
	public boolean state;

	public SyncFlyingStatus() { /* Nothing to do */ }

	public SyncFlyingStatus(int playerId, boolean state) {
		this.state = state;
		this.playerId = playerId;
	}

	@Override
	public void encode(final SyncFlyingStatus message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}

	@Override
	public SyncFlyingStatus decode(final FriendlyByteBuf buffer) {
		return new SyncFlyingStatus(buffer.readInt(), buffer.readBoolean());
	}

	@Override
	public void handle(final SyncFlyingStatus message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncFlyingStatus(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				DragonStateProvider.getCap(sender).ifPresent(handler -> handler.setWingsSpread(message.state));
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncFlyingStatus(sender.getId(), message.state));
			}
		}

		context.setPacketHandled(true);
	}
}