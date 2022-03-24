package by.dragonsurvivalteam.dragonsurvival.network.magic;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncDragonAbilitySlot.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncDragonAbilitySlot.java

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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncDragonAbilitySlot.java
	public void encode(SyncDragonAbilitySlot message, FriendlyByteBuf buffer) {
=======
	public void encode(SyncDragonAbilitySlot message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncDragonAbilitySlot.java
		buffer.writeInt(message.playerId);
		buffer.writeInt(message.selectedSlot);
		buffer.writeBoolean(message.displayHotbar);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncDragonAbilitySlot.java
	public SyncDragonAbilitySlot decode(FriendlyByteBuf buffer) {
=======
	public SyncDragonAbilitySlot decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncDragonAbilitySlot.java
		int playerId = buffer.readInt();
		int selectedSlot = buffer.readInt();
		boolean hideHotbar = buffer.readBoolean();

		return new SyncDragonAbilitySlot(playerId, selectedSlot, hideHotbar);
	}

	@Override
	public void handle(SyncDragonAbilitySlot message, Supplier<NetworkEvent.Context> supplier){
		DragonStateProvider.getCap(supplier.get().getSender()).ifPresent(cap -> {
			cap.getMagic().setSelectedAbilitySlot(message.selectedSlot);
			cap.getMagic().setRenderAbilities(message.displayHotbar);
		});
	}
}