package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.items.food.DragonFoodItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ChargedCoalItem extends DragonFoodItem {
	public ChargedCoalItem(Properties properties, AbstractDragonType dragonType, Consumer<LivingEntity> onEat) {
		super(properties, dragonType, onEat);
	}


	@Override
	public int getBurnTime(@NotNull ItemStack itemStack, RecipeType<?> recipeType) {
		return 4000;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {

		super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
		pTooltipComponents.add(Component.translatable("ds.description.chargedCoal"));
	}
}