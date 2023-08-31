package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.SortingHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SortInventoryPacket implements IMessage<SortInventoryPacket> {
	public SortInventoryPacket() { /* Nothing to do */ }

	@Override
	public void encode(final SortInventoryPacket message, final FriendlyByteBuf buffer) { /* Nothing to do */ }

	@Override
	public SortInventoryPacket decode(final FriendlyByteBuf buffer) {
		return new SortInventoryPacket();
	}

	@Override
	public void handle(final SortInventoryPacket message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		ServerPlayer sender = context.getSender();

		if (sender != null) {
			context.enqueueWork(() -> SortingHandler.sortInventory(sender));
		}

		context.setPacketHandled(true);
	}
}