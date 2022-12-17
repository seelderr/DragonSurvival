package by.dragonsurvivalteam.dragonsurvival.common.items.food;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


public class DragonFoodItem extends Item{
	private MobEffectInstance[] effects;
	private AbstractDragonType dragonType;
	private Consumer<LivingEntity> onEat;

	public DragonFoodItem(Properties p_i48487_1_){
		super(p_i48487_1_.food(genFoodProperties(null, null)));
	}

	public DragonFoodItem(Properties p_i48487_1_, MobEffectInstance... effectInstances){
		super(p_i48487_1_.food(genFoodProperties(null, effectInstances)));
		effects = effectInstances;
	}


	public DragonFoodItem(Properties p_i48487_1_, AbstractDragonType dragonType, MobEffectInstance... effectInstances){
		super(p_i48487_1_.food(genFoodProperties(dragonType, effectInstances)));
		this.dragonType = dragonType;
		effects = effectInstances;
	}

	public DragonFoodItem(Properties p_i48487_1_, AbstractDragonType dragonType, Consumer<LivingEntity> onEat, MobEffectInstance... effectInstances){
		super(p_i48487_1_.food(genFoodProperties(dragonType, effectInstances)));
		this.dragonType = dragonType;
		effects = effectInstances;
		this.onEat = onEat;
	}

	public DragonFoodItem(Properties p_i48487_1_, AbstractDragonType dragonType, Consumer<LivingEntity> onEat){
		super(p_i48487_1_.food(genFoodProperties(dragonType, null)));
		this.dragonType = dragonType;
		this.onEat = onEat;
	}

	@NotNull
	private static FoodProperties genFoodProperties(AbstractDragonType dragonType, MobEffectInstance... effectInstances){
		Builder builder = new Builder();
		builder.nutrition(1);
		builder.saturationMod(0.4F);
        builder.effect(() -> new MobEffectInstance(MobEffects.HUNGER, 20 * 15, 0), 1.0F);

		if(effectInstances != null){
			builder.alwaysEat();
		}

		return builder.build();
	}


	@Override
	public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity){
		ItemStack stack = super.finishUsingItem(pStack, pLevel, pLivingEntity);

		if(onEat != null){
			if(DragonUtils.isDragon(pLivingEntity) && (dragonType == null || DragonUtils.isType(pLivingEntity, dragonType))){
				onEat.accept(pLivingEntity);
			}
		}
		if(effects != null){
			if(DragonUtils.isDragon(pLivingEntity) && (dragonType == null || DragonUtils.isType(pLivingEntity, dragonType))){
				for(MobEffectInstance effect : effects){
					pLivingEntity.addEffect(effect);
				}
			}
		}

		return stack;
	}

}