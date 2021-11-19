package by.jackraidenph.dragonsurvival.network.Abilities;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDragonAbilitySlot implements IMessage<SyncDragonAbilitySlot>
{
	
	public int playerId;
	private int selectedSlot;
	private boolean displayHotbar;
	
	public SyncDragonAbilitySlot() {
	
	}
	
	public SyncDragonAbilitySlot(int playerId, int selectedSlot, boolean displayHotbar) {
		this.playerId = playerId;
		this.selectedSlot = selectedSlot;
		this.displayHotbar = displayHotbar;
	}
	
	@Override
	public void encode(SyncDragonAbilitySlot message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeInt(message.selectedSlot);
		buffer.writeBoolean(message.displayHotbar);
	}
	
	@Override
	public SyncDragonAbilitySlot decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		int selectedSlot = buffer.readInt();
		boolean hideHotbar = buffer.readBoolean();
		
		return new SyncDragonAbilitySlot(playerId, selectedSlot, hideHotbar);
	}
	
	@Override
	public void handle(SyncDragonAbilitySlot message, Supplier<NetworkEvent.Context> supplier) {
		DragonStateProvider.getCap(supplier.get().getSender()).ifPresent(cap -> {
			cap.setSelectedAbilitySlot(message.selectedSlot);
			cap.setRenderAbilities(message.displayHotbar);
		});
	}
}