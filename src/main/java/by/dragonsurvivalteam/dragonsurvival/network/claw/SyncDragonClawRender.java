package by.dragonsurvivalteam.dragonsurvival.network.claw;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncDragonClawRender implements IMessage<SyncDragonClawRender> {
	public int playerId;
	public boolean state;

	public SyncDragonClawRender() { /* Nothing to do */ }

	public SyncDragonClawRender(int playerId, boolean state) {
		this.playerId = playerId;
		this.state = state;
	}

	@Override
	public void encode(final SyncDragonClawRender message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}

	@Override
	public SyncDragonClawRender decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		return new SyncDragonClawRender(playerId, state);
	}

	@Override
	public void handle(final SyncDragonClawRender message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			ClientProxy.handleSyncDragonClawRender(message);
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				DragonStateProvider.getCap(sender).ifPresent(handler -> handler.getClawToolData().shouldRenderClaws = message.state);

				if (ServerConfig.syncClawRender) {
					// Make the other clients aware of the changes (but only if the option to do so is enabled)
					// TODO :: If a player hides their claw and then the server config changes, won't that claw stay hidden for other players (until restart)?
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncDragonClawRender(sender.getId(), message.state));
				}
			}
		}

		supplier.get().setPacketHandled(true);
	}
}