package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ClawInventory extends SubCap {
	public enum Slot {
		SWORD,
		PICKAXE,
		AXE,
		SHOVEL;

		/** Equivalent to the container size */
		public static int size() {
			return values().length;
		}
	}

	private final SimpleContainer clawsInventory = new SimpleContainer(4);

	public boolean shouldRenderClaws = true;

	private boolean isMenuOpen;

	public ClawInventory(final DragonStateHandler handler) {
		super(handler);
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();

		tag.putBoolean("clawsMenu", isMenuOpen);
		tag.put("clawsInventory", clawsInventory.createTag(provider));
		tag.putBoolean("renderClaws", shouldRenderClaws);

		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag){
		setMenuOpen(tag.getBoolean("clawsMenu"));
		shouldRenderClaws = tag.getBoolean("renderClaws");

		ListTag listTag = tag.getList("clawsInventory", 10);
		clawsInventory.fromTag(listTag, provider);
	}

	public ItemStack get(final Slot slot) {
		return clawsInventory.getItem(slot.ordinal());
	}

	public ItemStack getSword() {
		return clawsInventory.getItem(Slot.SWORD.ordinal());
	}

	public ItemStack getPickaxe() {
		return clawsInventory.getItem(Slot.PICKAXE.ordinal());
	}

	public ItemStack getAxe() {
		return clawsInventory.getItem(Slot.AXE.ordinal());
	}

	public ItemStack getShovel() {
		return clawsInventory.getItem(Slot.SHOVEL.ordinal());
	}

	public void setMenuOpen(boolean isMenuOpen) {
		this.isMenuOpen = isMenuOpen;
	}

	public SimpleContainer getClawsInventory() {
		return clawsInventory;
	}

	public boolean isMenuOpen() {
		return isMenuOpen;
	}
}