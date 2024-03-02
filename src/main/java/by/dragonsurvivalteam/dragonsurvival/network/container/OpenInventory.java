package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenInventory implements IMessage<OpenInventory> {
	@Override
	public void encode(final OpenInventory message, final FriendlyByteBuf buffer) { /* Nothing to do */ }

	@Override
	public OpenInventory decode(final FriendlyByteBuf buffer) {
		return new OpenInventory();
	}

	@Override
	public void handle(final OpenInventory message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		ServerPlayer sender = context.getSender();

		if (sender != null) {
			context.enqueueWork(() -> {
				if (sender.containerMenu != null) { // TODO :: container might never be null?
					sender.containerMenu.removed(sender);
				}

				InventoryMenu inventory = sender.inventoryMenu;
				sender.initMenu(inventory);
			});
		}

		context.setPacketHandled(true);
	}
}