package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ClawInventory extends SubCap{
	public boolean renderClaws = true;
	/*
		Slot 0: Sword
		Slot 1: Pickaxe
		Slot 2: Axe
		Slot 3: Shovel
	 */
	private SimpleContainer clawsInventory = new SimpleContainer(4);
	private boolean clawsMenuOpen = false;

	public ClawInventory(DragonStateHandler handler){
		super(handler);
	}

	@Override
	public CompoundTag writeNBT(){
		CompoundTag tag = new CompoundTag();

		tag.putBoolean("clawsMenu", isClawsMenuOpen());
		tag.put("clawsInventory", saveClawInventory(getClawsInventory()));
		tag.putBoolean("renderClaws", renderClaws);

		return tag;
	}

	public SimpleContainer getClawsInventory(){
		return clawsInventory;
	}

	public boolean isClawsMenuOpen(){
		return clawsMenuOpen;
	}

	@Override
	public void readNBT(CompoundTag tag){
		setClawsMenuOpen(tag.getBoolean("clawsMenu"));
		renderClaws = tag.getBoolean("renderClaws");

		ListTag clawInv = tag.getList("clawsInventory", 10);
		setClawsInventory(readClawInventory(clawInv));
	}

	public void setClawsMenuOpen(boolean clawsMenuOpen){
		this.clawsMenuOpen = clawsMenuOpen;
	}

	public void setClawsInventory(SimpleContainer clawsInventory){
		this.clawsInventory = clawsInventory;
	}

	public static SimpleContainer readClawInventory(ListTag clawInv){
		SimpleContainer inventory = new SimpleContainer(4);

		for(int i = 0; i < clawInv.size(); ++i){
			CompoundTag CompoundTag = clawInv.getCompound(i);
			int j = CompoundTag.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.of(CompoundTag);
			if(!itemstack.isEmpty()){
				if(j >= 0 && j < inventory.getContainerSize()){
					inventory.setItem(j, itemstack);
				}
			}
		}

		return inventory;
	}

	public static ListTag saveClawInventory(SimpleContainer inv){
		ListTag nbt = new ListTag();

		for(int i = 0; i < inv.getContainerSize(); ++i){
			if(!inv.getItem(i).isEmpty()){
				CompoundTag CompoundTag = new CompoundTag();
				CompoundTag.putByte("Slot", (byte)i);
				inv.getItem(i).save(CompoundTag);
				nbt.add(CompoundTag);
			}
		}

		return nbt;
	}
}