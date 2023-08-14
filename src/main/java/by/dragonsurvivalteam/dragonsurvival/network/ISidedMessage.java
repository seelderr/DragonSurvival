package by.dragonsurvivalteam.dragonsurvival.network;

import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public abstract class ISidedMessage<T extends ISidedMessage<T>> implements IMessage<T> {
	public int playerId;

	public ISidedMessage(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void handle(final T message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			ClientProxy.handleRunClient(message, context);
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				runServer(message, context, sender);
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), create(message));
			}
		}

		runCommon(message, context);
		context.setPacketHandled(true);
	}

	public abstract T create(T message);

	public abstract void runClient(T message, NetworkEvent.Context context, Player targetPlayer);

	public abstract void runCommon(T message, NetworkEvent.Context context);

	public abstract void runServer(T message, NetworkEvent.Context context, ServerPlayer sender);
}