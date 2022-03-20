package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class DragonTreatItem extends Item{
	public DragonType type;

	public DragonTreatItem(DragonType type, Properties p_i48487_1_){
		super(p_i48487_1_.food(new Food.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat().effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F).build()));
		this.type = type;
	}

	public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity entity){
		if(entity instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)entity;

			if(DragonStateProvider.getCap(player).map((cap) -> cap.getType()).get() == type){
				ManaHandler.replenishMana(player, ManaHandler.getMaxMana(player));
			}
		}

		return this.isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
	}

	@Override
	public void appendHoverText(ItemStack p_77624_1_,
		@Nullable
			World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_){
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslationTextComponent("ds.description." + type.name().toLowerCase() + "DragonTreat"));
	}
}