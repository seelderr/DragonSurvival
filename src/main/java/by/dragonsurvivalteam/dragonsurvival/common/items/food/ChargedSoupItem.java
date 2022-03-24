package by.dragonsurvivalteam.dragonsurvival.common.items.food;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ChargedSoupItem extends Item{
	public ChargedSoupItem(Properties p_i48487_1_){
		super(p_i48487_1_.food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat().alwaysEat().effect(() -> new EffectInstance(Effects.POISON, 20 * 15, 0), 1.0F).effect(() -> new EffectInstance(DragonEffects.FIRE, Functions.secondsToTicks(ConfigHandler.SERVER.chargedSoupBuffDuration.get()), 0), 1.0F).build()));
	}

	@Override
	public void appendHoverText(ItemStack p_77624_1_,
		@Nullable
			World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_){
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslationTextComponent("ds.description.chargedSoup"));
	}
}