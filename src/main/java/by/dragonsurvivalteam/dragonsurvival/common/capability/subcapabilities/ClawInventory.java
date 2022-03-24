package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/capability/DragonCapabilities/ClawInventory.java
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/capability/subcapabilities/ClawInventory.java

public class ClawInventory extends SubCap{
	public boolean renderClaws = true;
	/*
		Slot 0: Sword
		Slot 1: Pickaxe
		Slot 2: Axe
		Slot 3: Shovel
	 */
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/capability/DragonCapabilities/ClawInventory.java
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
=======
	private Inventory clawsInventory = new Inventory(4);
	private boolean clawsMenuOpen = false;

	public ClawInventory(DragonStateHandler handler){
		super(handler);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/capability/subcapabilities/ClawInventory.java
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/capability/DragonCapabilities/ClawInventory.java
	public Tag writeNBT()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putBoolean("clawsMenu", clawsMenuOpen);
		tag.put("clawsInventory", saveClawInventory(clawsInventory));
=======
	public CompoundNBT writeNBT(){
		CompoundNBT tag = new CompoundNBT();

		tag.putBoolean("clawsMenu", isClawsMenuOpen());
		tag.put("clawsInventory", saveClawInventory(getClawsInventory()));
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/capability/subcapabilities/ClawInventory.java
		tag.putBoolean("renderClaws", renderClaws);

		return tag;
	}

	public Inventory getClawsInventory(){
		return clawsInventory;
	}

	public boolean isClawsMenuOpen(){
		return clawsMenuOpen;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/capability/DragonCapabilities/ClawInventory.java
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
=======
	public void readNBT(CompoundNBT tag){
		setClawsMenuOpen(tag.getBoolean("clawsMenu"));
		renderClaws = tag.getBoolean("renderClaws");

		ListNBT clawInv = tag.getList("clawsInventory", 10);
		setClawsInventory(readClawInventory(clawInv));
	}

	public void setClawsMenuOpen(boolean clawsMenuOpen){
		this.clawsMenuOpen = clawsMenuOpen;
	}

	public void setClawsInventory(Inventory clawsInventory){
		this.clawsInventory = clawsInventory;
	}

	public static Inventory readClawInventory(ListNBT clawInv){
		Inventory inventory = new Inventory(4);

		for(int i = 0; i < clawInv.size(); ++i){
			CompoundNBT compoundnbt = clawInv.getCompound(i);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/capability/subcapabilities/ClawInventory.java
			int j = compoundnbt.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.of(compoundnbt);
			if(!itemstack.isEmpty()){
				if(j >= 0 && j < inventory.getContainerSize()){
					inventory.setItem(j, itemstack);
				}
			}
		}

		return inventory;
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/capability/DragonCapabilities/ClawInventory.java
	
	public static ListTag saveClawInventory(SimpleContainer inv)
	{
		ListTag nbt = new ListTag();
		
		for(int i = 0; i < inv.getContainerSize(); ++i) {
			if (!inv.getItem(i).isEmpty()) {
				CompoundTag compoundnbt = new CompoundTag();
=======

	public static ListNBT saveClawInventory(Inventory inv){
		ListNBT nbt = new ListNBT();

		for(int i = 0; i < inv.getContainerSize(); ++i){
			if(!inv.getItem(i).isEmpty()){
				CompoundNBT compoundnbt = new CompoundNBT();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/capability/subcapabilities/ClawInventory.java
				compoundnbt.putByte("Slot", (byte)i);
				inv.getItem(i).save(compoundnbt);
				nbt.add(compoundnbt);
			}
		}

		return nbt;
	}
}