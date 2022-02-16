package by.jackraidenph.dragonsurvival.common.capability.subcapabilities;

import by.jackraidenph.dragonsurvival.common.capability.NBTInterface;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class ClawInventory implements NBTInterface
{
	/*
		Slot 0: Sword
		Slot 1: Pickaxe
		Slot 2: Axe
		Slot 3: Shovel
	 */
	private Inventory clawsInventory = new Inventory(4);
	
	private boolean clawsMenuOpen = false;
	public boolean renderClaws = true;
	
	public Inventory getClawsInventory()
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
	
	public void setClawsInventory(Inventory clawsInventory)
	{
		this.clawsInventory = clawsInventory;
	}
	
	@Override
	public CompoundNBT writeNBT()
	{
		CompoundNBT tag = new CompoundNBT();
		
		tag.putBoolean("clawsMenu", isClawsMenuOpen());
		tag.put("clawsInventory", saveClawInventory(getClawsInventory()));
		tag.putBoolean("renderClaws", renderClaws);
		
		return tag;
	}
	
	@Override
	public void readNBT(CompoundNBT tag)
	{
		setClawsMenuOpen(tag.getBoolean("clawsMenu"));
		renderClaws = tag.getBoolean("renderClaws");
		
		ListNBT clawInv = tag.getList("clawsInventory", 10);
		setClawsInventory(readClawInventory(clawInv));
	}
	
	public static Inventory readClawInventory(ListNBT clawInv)
	{
		Inventory inventory = new Inventory(4);
		
		for(int i = 0; i < clawInv.size(); ++i) {
			CompoundNBT compoundnbt = clawInv.getCompound(i);
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
	
	public static ListNBT saveClawInventory(Inventory inv)
	{
		ListNBT nbt = new ListNBT();
		
		for(int i = 0; i < inv.getContainerSize(); ++i) {
			if (!inv.getItem(i).isEmpty()) {
				CompoundNBT compoundnbt = new CompoundNBT();
				compoundnbt.putByte("Slot", (byte)i);
				inv.getItem(i).save(compoundnbt);
				nbt.add(compoundnbt);
			}
		}
		
		return nbt;
	}
}
