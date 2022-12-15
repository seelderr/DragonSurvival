package by.dragonsurvivalteam.dragonsurvival.network.magic;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDragonAbilitySlot implements IMessage<SyncDragonAbilitySlot>{

	public int playerId;
	private int selectedSlot;
	private boolean displayHotbar;

	public SyncDragonAbilitySlot(){

	}

	public SyncDragonAbilitySlot(int playerId, int selectedSlot, boolean displayHotbar){
		this.playerId = playerId;
		this.selectedSlot = selectedSlot;
		this.displayHotbar = displayHotbar;
	}

	@Override

	public void encode(SyncDragonAbilitySlot message, FriendlyByteBuf buffer){

		buffer.writeInt(message.playerId);
		buffer.writeInt(message.selectedSlot);
		buffer.writeBoolean(message.displayHotbar);
	}

	@Override

	public SyncDragonAbilitySlot decode(FriendlyByteBuf buffer){

		int playerId = buffer.readInt();
		int selectedSlot = buffer.readInt();
		boolean hideHotbar = buffer.readBoolean();

		return new SyncDragonAbilitySlot(playerId, selectedSlot, hideHotbar);
	}

	@Override
	public void handle(SyncDragonAbilitySlot message, Supplier<NetworkEvent.Context> supplier){
		DragonStateProvider.getCap(supplier.get().getSender()).ifPresent(cap -> {
			if(cap.getMagicData().getAbilityFromSlot(cap.getMagicData().getSelectedAbilitySlot()) != null) cap.getMagicData().getAbilityFromSlot(cap.getMagicData().getSelectedAbilitySlot()).onKeyReleased(supplier.get().getSender());
			cap.getMagicData().setSelectedAbilitySlot(message.selectedSlot);
			cap.getMagicData().setRenderAbilities(message.displayHotbar);
		});
	}
}