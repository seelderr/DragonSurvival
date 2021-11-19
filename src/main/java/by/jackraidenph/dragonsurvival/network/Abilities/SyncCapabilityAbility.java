package by.jackraidenph.dragonsurvival.network.Abilities;

import by.jackraidenph.dragonsurvival.abilities.DragonAbilities;
import by.jackraidenph.dragonsurvival.abilities.common.DragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class SyncCapabilityAbility implements IMessage<SyncCapabilityAbility>
{
	
	public int playerId;
	private ArrayList<DragonAbility> abilities = new ArrayList<>();
	private int selectedSlot;
	private int maxMana;
	private int currentMana;
	private boolean renderHotbar;
	
	public SyncCapabilityAbility() {
	
	}
	
	public SyncCapabilityAbility(int playerId, int selectedSlot, int maxMana, int currentMana, ArrayList<DragonAbility> abilities, boolean renderHotbar) {
		this.playerId = playerId;
		this.maxMana = maxMana;
		this.currentMana = currentMana;
		this.abilities = abilities;
		this.selectedSlot = selectedSlot;
		this.renderHotbar = renderHotbar;
	}
	
	@Override
	public void encode(SyncCapabilityAbility message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeInt(message.selectedSlot);
		buffer.writeInt(message.maxMana);
		buffer.writeInt(message.currentMana);
		buffer.writeBoolean(message.renderHotbar);
		
		CompoundNBT tag = new CompoundNBT();
		
		for(DragonAbility ab : message.abilities){
			tag.put(ab.getId(), ab.saveNBT());
		}
		
		buffer.writeNbt(tag);
	}
	
	@Override
	public SyncCapabilityAbility decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		ArrayList<DragonAbility> abilities = new ArrayList<>();
		int selectedSlot = buffer.readInt();
		int maxMana = buffer.readInt();
		int currentMana = buffer.readInt();
		boolean renderHotbar = buffer.readBoolean();
		
		CompoundNBT tag = buffer.readNbt();
		
		for(DragonAbility staticAbility : DragonAbilities.ABILITY_LOOKUP.values()){
			if(tag.contains(staticAbility.getId())){
				DragonAbility ability = staticAbility.createInstance();
				ability.loadNBT(tag.getCompound(staticAbility.getId()));
				abilities.add(ability);
			}
		}
		
		return new SyncCapabilityAbility(playerId, selectedSlot, maxMana, currentMana, abilities, renderHotbar);
	}
	
	@Override
	public void handle(SyncCapabilityAbility message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			if (supplier.get().getDirection().getReceptionSide().isClient() && (Minecraft.getInstance().player != null)) {
				DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
					cap.setMaxMana(message.maxMana);
					cap.setCurrentMana(message.currentMana);
					cap.getAbilities().clear();
					cap.getAbilities().addAll(message.abilities);
					cap.setSelectedAbilitySlot(message.selectedSlot);
					cap.setRenderAbilities(message.renderHotbar);
				});
			}
		});
	}
}