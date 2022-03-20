package by.dragonsurvivalteam.dragonsurvival.server.containers.slots;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class ClawToolSlot extends Slot{
	static final ResourceLocation AXE_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_axe");
	static final ResourceLocation PICKAXE_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_pickaxe");
	static final ResourceLocation SHOVEL_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_shovel");
	static final ResourceLocation SWORD_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_sword");
	public ToolType type;
	DragonContainer dragonContainer;

	public ClawToolSlot(DragonContainer container, IInventory inv, ToolType type, int index, int x, int y){
		super(inv, index, x, y);
		this.type = type;
		this.dragonContainer = container;
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
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(){
		return Pair.of(PlayerContainer.BLOCK_ATLAS, type == null ? SWORD_TEXTURE : type == ToolType.AXE ? AXE_TEXTURE : type == ToolType.PICKAXE ? PICKAXE_TEXTURE : SHOVEL_TEXTURE);
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
}