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

	private SimpleContainer clawsInventory = new SimpleContainer(4);

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