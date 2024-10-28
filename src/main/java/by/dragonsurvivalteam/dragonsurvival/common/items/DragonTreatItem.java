package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Handle food which can replenish mana
 */
public class DragonTreatItem extends Item {
	public AbstractDragonType type;

	public DragonTreatItem(final AbstractDragonType type, final Properties foodProperties) {
		super(foodProperties.food(new FoodProperties.Builder().nutrition(1).alwaysEdible().saturationModifier(0.4F).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 20 * 15, 0), 1.0F).build()));
		this.type = type;
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull final ItemStack itemStack, @NotNull final Level level, @NotNull final LivingEntity livingEntity) {
		if (livingEntity instanceof Player player) {
			if (DragonUtils.isDragonType(player, type)) {
				ManaHandler.replenishMana(player, ManaHandler.getMaxMana(player));
				player.addEffect(new MobEffectInstance(DSEffects.SOURCE_OF_MAGIC, Functions.minutesToTicks(1)));
			}
		}

		return livingEntity.eat(level, itemStack);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
		super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
		pTooltipComponents.add(Component.translatable("ds.description." + type.getTypeNameLowerCase() + "DragonTreat"));
	}
}