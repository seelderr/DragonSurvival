package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
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
	public CompoundTag writeNBT() {
		CompoundTag tag = new CompoundTag();

		tag.putBoolean("clawsMenu", isMenuOpen);
		tag.put("clawsInventory", saveClawInventory(clawsInventory));
		tag.putBoolean("renderClaws", shouldRenderClaws);

		return tag;
	}

	@Override
	public void readNBT(CompoundTag tag){
		setMenuOpen(tag.getBoolean("clawsMenu"));
		shouldRenderClaws = tag.getBoolean("renderClaws");

		ListTag listTag = tag.getList("clawsInventory", 10);
		setClawsInventory(readClawInventory(listTag));
	}

	public static SimpleContainer readClawInventory(final ListTag listTag) {
		SimpleContainer clawInventory = new SimpleContainer(4);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag CompoundTag = listTag.getCompound(i);
			int slot = CompoundTag.getByte("Slot") & 255; // Avoid negative values
			ItemStack itemstack = ItemStack.of(CompoundTag);

			if (!itemstack.isEmpty()) {
				if (slot < clawInventory.getContainerSize()) {
					clawInventory.setItem(slot, itemstack);
				}
			}
		}

		return clawInventory;
	}

	public static ListTag saveClawInventory(final SimpleContainer clawInventory) {
		ListTag listTag = new ListTag();

		for (int slot = 0; slot < clawInventory.getContainerSize(); slot++) {
			if (!clawInventory.getItem(slot).isEmpty()) {
				CompoundTag CompoundTag = new CompoundTag();
				CompoundTag.putByte("Slot", (byte)slot);
				clawInventory.getItem(slot).save(CompoundTag);

				listTag.add(CompoundTag);
			}
		}

		return listTag;
	}

	public void setClawsInventory(final SimpleContainer clawsInventory) {
		this.clawsInventory = clawsInventory;
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