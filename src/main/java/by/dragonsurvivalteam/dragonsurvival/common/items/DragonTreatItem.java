package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
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

public class DragonTreatItem extends Item{
	public AbstractDragonType type;

	public DragonTreatItem(AbstractDragonType type, Properties p_i48487_1_){
		super(p_i48487_1_.food(new FoodProperties.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat().effect(() -> new MobEffectInstance(MobEffects.HUNGER, 20 * 15, 0), 1.0F).build()));
		this.type = type;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack p_77654_1_, Level p_77654_2_, LivingEntity entity){
		if(entity instanceof Player player){
			
			if(DragonUtils.isDragonType(player, type)){
				ManaHandler.replenishMana(player, ManaHandler.getMaxMana(player));
				player.addEffect(new MobEffectInstance(DragonEffects.SOURCE_OF_MAGIC, Functions.minutesToTicks(1)));
			}
		}

		return isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
	}

	@Override
	public void appendHoverText(ItemStack p_77624_1_,
		@Nullable
			Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_){
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslatableComponent("ds.description." + type.getTypeName().toLowerCase() + "DragonTreat"));
	}
}