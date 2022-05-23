package by.dragonsurvivalteam.dragonsurvival.common.items.food;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ChargedSoupItem extends Item{
	public ChargedSoupItem(Properties p_i48487_1_){
		super(p_i48487_1_.food(new FoodProperties.Builder().nutrition(1).saturationMod(0.4F).meat().alwaysEat().effect(() -> new MobEffectInstance(MobEffects.POISON, 20 * 15, 0), 1.0F).effect(() -> new MobEffectInstance(DragonEffects.FIRE, Functions.secondsToTicks(ServerConfig.chargedSoupBuffDuration), 0), 1.0F).build()));
	}

	@Override
	public void appendHoverText(ItemStack p_77624_1_,
		@Nullable
			Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_){
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslatableComponent("ds.description.chargedSoup"));
	}
}