package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ClawInventory implements DragonCapability
{
	/*
		Slot 0: Sword
		Slot 1: Pickaxe
		Slot 2: Axe
		Slot 3: Shovel
	 */
	private SimpleContainer clawsInventory = new SimpleContainer(4);
	
	private boolean clawsMenuOpen = false;
	public boolean renderClaws = true;
	
	public SimpleContainer getClawsInventory()
	{
		return clawsInventory;
	}
	
	public boolean isClawsMenuOpen()
	{
		return clawsMenuOpen;
	}
	
	public void setClawsMenuOpen(boolean clawsMenuOpen)
	{
		this.clawsMenuOpen = clawsMenuOpen;
	}
	
	public void setClawsInventory(SimpleContainer clawsInventory)
	{
		this.clawsInventory = clawsInventory;
	}
	
	@Override
	public Tag writeNBT()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putBoolean("clawsMenu", clawsMenuOpen);
		tag.put("clawsInventory", saveClawInventory(clawsInventory));
		tag.putBoolean("renderClaws", renderClaws);
		
		return tag;
	}
	
	@Override
	public void readNBT(Tag base)
	{
		CompoundTag tag = (CompoundTag) base;
		
		clawsMenuOpen = tag.getBoolean("clawsMenu");
		renderClaws = tag.getBoolean("renderClaws");
		
		ListTag clawInv = tag.getList("clawsInventory", 10);
		clawsInventory = readClawInventory(clawInv);
	}
	
	
	public static SimpleContainer readClawInventory(ListTag clawInv)
	{
		SimpleContainer inventory = new SimpleContainer(4);
		
		for(int i = 0; i < clawInv.size(); ++i) {
			CompoundTag compoundnbt = clawInv.getCompound(i);
			int j = compoundnbt.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.of(compoundnbt);
			if (!itemstack.isEmpty()) {
				if (j >= 0 && j < inventory.getContainerSize()) {
					inventory.setItem(j, itemstack);
				}
			}
		}
		
		return inventory;
	}
	
	public static ListTag saveClawInventory(SimpleContainer inv)
	{
		ListTag nbt = new ListTag();
		
		for(int i = 0; i < inv.getContainerSize(); ++i) {
			if (!inv.getItem(i).isEmpty()) {
				CompoundTag compoundnbt = new CompoundTag();
				compoundnbt.putByte("Slot", (byte)i);
				inv.getItem(i).save(compoundnbt);
				nbt.add(compoundnbt);
			}
		}
		
		return nbt;
	}
}
