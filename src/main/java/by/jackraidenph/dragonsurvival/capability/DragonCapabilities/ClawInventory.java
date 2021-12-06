package by.jackraidenph.dragonsurvival.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class ClawInventory implements DragonCapability
{
	/*
		Slot 0: Sword
		Slot 1: Pickaxe
		Slot 2: Axe
		Slot 3: Shovel
	 */
	private Inventory clawsInventory = new Inventory(4);
	private boolean clawsMenuOpen = false;
	
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
	public INBT writeNBT(Capability<DragonStateHandler> capability,  Direction side)
	{
		CompoundNBT tag = new CompoundNBT();
		
		tag.putBoolean("clawsMenu", isClawsMenuOpen());
		tag.put("clawsInventory", saveClawInventory(getClawsInventory()));
		
		return tag;
	}
	
	@Override
	public void readNBT(Capability<DragonStateHandler> capability, Direction side, INBT base)
	{
		CompoundNBT tag = (CompoundNBT) base;
		
		setClawsMenuOpen(tag.getBoolean("clawsMenu"));
		ListNBT clawInv = tag.getList("clawsInventory", 10);
		setClawsInventory(readClawInventory(clawInv));
	}
	
	@Override
	public void clone(DragonStateHandler oldCap)
	{
		setClawsInventory(oldCap.getClawInventory().getClawsInventory());
		setClawsMenuOpen(oldCap.getClawInventory().isClawsMenuOpen());
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
