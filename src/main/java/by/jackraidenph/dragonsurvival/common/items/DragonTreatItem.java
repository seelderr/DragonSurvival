package by.jackraidenph.dragonsurvival.common.items;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class DragonTreatItem extends Item
{
	public DragonType type;
	
	public DragonTreatItem(DragonType type, Properties p_i48487_1_)
	{
		super(p_i48487_1_.food(new FoodProperties.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat()
				                       .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 20 * 15, 0), 1.0F)
				                       .build()));
		this.type = type;
	}
	
	@Override
	public void appendHoverText(ItemStack p_77624_1_, @Nullable
			Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_)
	{
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslatableComponent("ds.description." + type.name().toLowerCase() + "DragonTreat"));
	}
	
	public ItemStack finishUsingItem(ItemStack p_77654_1_, Level  p_77654_2_, LivingEntity entity) {
		if(entity instanceof Player){
			Player player = (Player)entity;
			
			if(DragonStateProvider.getCap(player).map((cap) -> cap.getType()).get() == type) {
				DragonUtils.replenishMana(player, DragonUtils.getMaxMana(player));
			}
		}
		
		return this.isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
	}
}
