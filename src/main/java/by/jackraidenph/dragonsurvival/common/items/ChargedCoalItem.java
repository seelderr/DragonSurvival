package by.jackraidenph.dragonsurvival.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ChargedCoalItem extends Item
{
	public ChargedCoalItem(Properties p_i48487_1_)
	{
		super(p_i48487_1_);
	}
	
	@Override
	public int getBurnTime(ItemStack itemStack, @org.jetbrains.annotations.Nullable RecipeType<?> recipeType)
	{
		return 4000;
	}
	
	@Override
	public void appendHoverText(ItemStack p_77624_1_, @Nullable
			Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_)
	{
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslatableComponent("ds.description.chargedCoal"));
	}
}
