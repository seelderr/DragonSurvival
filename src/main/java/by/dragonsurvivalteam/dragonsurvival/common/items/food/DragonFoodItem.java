package by.dragonsurvivalteam.dragonsurvival.common.items.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;


public class DragonFoodItem extends Item{
	public DragonFoodItem(Properties p_i48487_1_){
		super(p_i48487_1_.food(new FoodProperties.Builder().nutrition(1).saturationMod(0.4F).meat().effect(() -> new MobEffectInstance(MobEffects.HUNGER, 20 * 15, 0), 1.0F).build()));
	}
}