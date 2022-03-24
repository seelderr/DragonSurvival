package by.dragonsurvivalteam.dragonsurvival.server.containers.slots;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/server/containers/slots/ClawToolSlot.java
import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.jackraidenph.dragonsurvival.server.containers.DragonContainer;
=======
import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/server/containers/slots/ClawToolSlot.java
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/server/containers/slots/ClawToolSlot.java
public class ClawToolSlot extends Slot
{
	DragonContainer dragonContainer;
	int num;
	
=======
public class ClawToolSlot extends Slot{
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/server/containers/slots/ClawToolSlot.java
	static final ResourceLocation AXE_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_axe");
	static final ResourceLocation PICKAXE_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_pickaxe");
	static final ResourceLocation SHOVEL_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_shovel");
	static final ResourceLocation SWORD_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_sword");
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/server/containers/slots/ClawToolSlot.java
	
	public ClawToolSlot(DragonContainer container, Container inv, int index, int x, int y, int num)
	{
=======
	public ToolType type;
	DragonContainer dragonContainer;

	public ClawToolSlot(DragonContainer container, IInventory inv, ToolType type, int index, int x, int y){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/server/containers/slots/ClawToolSlot.java
		super(inv, index, x, y);
		this.dragonContainer = container;
		this.num = num;
	}

	@Override
	public boolean mayPlace(ItemStack stack){
		return type == null && (stack.getEquipmentSlot() == EquipmentSlotType.MAINHAND || stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem || stack.getToolTypes().contains(ToolType.AXE)) || stack.getItem().getToolTypes(stack).contains(type);
	}

	@Override
	public void set(ItemStack p_75215_1_){
		super.set(p_75215_1_);
		syncSlots();
	}

	@Nullable
	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/server/containers/slots/ClawToolSlot.java
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
	{
		return Pair.of(InventoryMenu.BLOCK_ATLAS, num == 0 ? SWORD_TEXTURE : num == 2 ? AXE_TEXTURE : num == 1 ? PICKAXE_TEXTURE : SHOVEL_TEXTURE);
=======
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(){
		return Pair.of(PlayerContainer.BLOCK_ATLAS, type == null ? SWORD_TEXTURE : type == ToolType.AXE ? AXE_TEXTURE : type == ToolType.PICKAXE ? PICKAXE_TEXTURE : SHOVEL_TEXTURE);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/server/containers/slots/ClawToolSlot.java
	}

	@Override
	public ItemStack remove(int p_75209_1_){
		ItemStack stack = super.remove(p_75209_1_);
		syncSlots();
		return stack;
	}

	@Override
	public boolean isActive(){
		return dragonContainer.menuStatus == 1;
	}

	private void syncSlots(){
		if(!dragonContainer.player.level.isClientSide){
			DragonStateHandler handler = DragonStateProvider.getCap(dragonContainer.player).orElse(null);

			if(handler != null){
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> dragonContainer.player), new SyncDragonClawsMenu(dragonContainer.player.getId(), handler.getClawInventory().isClawsMenuOpen(), handler.getClawInventory().getClawsInventory()));
			}
		}
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/server/containers/slots/ClawToolSlot.java
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		switch(this.num){
			case 0:
				return stack.getEquipmentSlot() == EquipmentSlot.MAINHAND || stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
			case 1:
				return stack.getItem() instanceof PickaxeItem;
			case 2:
				return stack.getItem() instanceof AxeItem;
			case 3:
				return stack.getItem() instanceof ShovelItem;
		}
		return false;
	}
}
=======
}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/server/containers/slots/ClawToolSlot.java
