package by.dragonsurvivalteam.dragonsurvival.server.containers.slots;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;


public class ClawToolSlot extends Slot{
	static final ResourceLocation AXE_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_axe");
	static final ResourceLocation PICKAXE_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_pickaxe");
	static final ResourceLocation SHOVEL_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_shovel");
	static final ResourceLocation SWORD_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_sword");
	DragonContainer dragonContainer;
	int num;

	public ClawToolSlot(DragonContainer container, Container inv, int index, int x, int y, int num){
		super(inv, index, x, y);
		this.dragonContainer = container;
		this.num = num;
	}

	@Override
	public boolean mayPlace(ItemStack stack){
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

	@Override
	public void set(ItemStack p_75215_1_){
		super.set(p_75215_1_);
		syncSlots();
	}

	@Nullable
	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(){
		return Pair.of(InventoryMenu.BLOCK_ATLAS, num == 0 ? SWORD_TEXTURE : num == 2 ? AXE_TEXTURE : num == 1 ? PICKAXE_TEXTURE : SHOVEL_TEXTURE);
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
			DragonStateHandler handler = DragonUtils.getHandler(dragonContainer.player);

			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> dragonContainer.player), new SyncDragonClawsMenu(dragonContainer.player.getId(), handler.getClawInventory().isClawsMenuOpen(), handler.getClawInventory().getClawsInventory()));
		}
	}
}