package by.dragonsurvivalteam.dragonsurvival.network.magic;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDragonAbilitySlot implements IMessage<SyncDragonAbilitySlot> {
	public int playerId;
	private int selectedSlot;
	private boolean displayHotbar;

	public SyncDragonAbilitySlot() { /* Nothing to do */ }

	public SyncDragonAbilitySlot(int playerId, int selectedSlot, boolean displayHotbar) {
		this.playerId = playerId;
		this.selectedSlot = selectedSlot;
		this.displayHotbar = displayHotbar;
	}

	@Override
	public void encode(final SyncDragonAbilitySlot message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeInt(message.selectedSlot);
		buffer.writeBoolean(message.displayHotbar);
	}

	@Override
	public SyncDragonAbilitySlot decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		int selectedSlot = buffer.readInt();
		boolean hideHotbar = buffer.readBoolean();

		return new SyncDragonAbilitySlot(playerId, selectedSlot, hideHotbar);
	}

	@Override
	public void handle(final SyncDragonAbilitySlot message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		context.enqueueWork(()-> DragonStateProvider.getCap(context.getSender()).ifPresent(cap -> {
			if (cap.getMagicData().getAbilityFromSlot(cap.getMagicData().getSelectedAbilitySlot()) != null) {
				cap.getMagicData().getAbilityFromSlot(cap.getMagicData().getSelectedAbilitySlot()).onKeyReleased(context.getSender());
			}

			cap.getMagicData().setSelectedAbilitySlot(message.selectedSlot);
			cap.getMagicData().setRenderAbilities(message.displayHotbar);
		}));

		context.setPacketHandled(true);
	}
}