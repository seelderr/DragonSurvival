package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class RequestSpinResync implements IMessage<RequestSpinResync> {
	public RequestSpinResync() { /* Nothing to do */ }

	@Override
	public void encode(final RequestSpinResync message, FriendlyByteBuf buffer) { /* Nothing to do */ }

	@Override
	public RequestSpinResync decode(final FriendlyByteBuf buffer) {
		return new RequestSpinResync();
	}

	@Override
	public void handle(final RequestSpinResync message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		ServerPlayer sender = context.getSender();

		if (sender != null) {
			DragonStateProvider.getCap(sender).ifPresent(handler -> NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncSpinStatus(sender.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned)));
		}

		context.setPacketHandled(true);
	}
}