package by.dragonsurvivalteam.dragonsurvival.common.items.food;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class CharredFoodItem extends Item{
	public CharredFoodItem(Properties p_i48487_1_){
		super(p_i48487_1_.food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat().effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F).build()));
	}
}