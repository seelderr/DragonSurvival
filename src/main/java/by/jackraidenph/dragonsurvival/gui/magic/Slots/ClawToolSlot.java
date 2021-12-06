package by.jackraidenph.dragonsurvival.gui.magic.Slots;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.containers.DragonContainer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class ClawToolSlot extends Slot
{
	public ToolType type;
	DragonContainer dragonContainer;
	
	static final ResourceLocation AXE_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_axe");
	static final ResourceLocation PICKAXE_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_pickaxe");
	static final ResourceLocation SHOVEL_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_shovel");
	static final ResourceLocation SWORD_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_sword");
	
	public ClawToolSlot(DragonContainer container, IInventory inv, ToolType type, int index, int x, int y)
	{
		super(inv, index, x, y);
		this.type = type;
		this.dragonContainer = container;
	}
	
	@Override
	public boolean isActive()
	{
		return dragonContainer.menuStatus == 1;
	}
	
	@Nullable
	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
	{
		return Pair.of(PlayerContainer.BLOCK_ATLAS, type == null ? SWORD_TEXTURE : type == ToolType.AXE ? AXE_TEXTURE : type == ToolType.PICKAXE ? PICKAXE_TEXTURE : SHOVEL_TEXTURE);
	}
	
	@Override
	public ItemStack remove(int p_75209_1_)
	{
		ItemStack stack = super.remove(p_75209_1_);
		return stack;
	}
	
	@Override
	public void set(ItemStack p_75215_1_)
	{
		super.set(p_75215_1_);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return type == null && (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem || stack.getToolTypes().contains(ToolType.AXE)) || stack.getItem().getToolTypes(stack).contains(type);
	}
}
