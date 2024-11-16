package by.dragonsurvivalteam.dragonsurvival.common.effects;

import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.ForestDragonConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.jetbrains.annotations.NotNull;

public class Stress extends MobEffect {
    public Stress(int color) {
        super(MobEffectCategory.HARMFUL, color);
    }

    @Override
    public boolean applyEffectTick(@NotNull final LivingEntity living, int amplifier) {
        if (living instanceof Player player) {
            FoodData food = player.getFoodData();

            if (food.getSaturationLevel() > 0) {
                int oldFood = food.getFoodLevel();
                food.eat(1, (float) ((-0.5f * food.getSaturationLevel()) * ForestDragonConfig.stressExhaustion));

                if (oldFood != 20) {
                    food.setFoodLevel((int) (food.getFoodLevel() - 1 * ForestDragonConfig.stressExhaustion));
                }
            }

            player.causeFoodExhaustion((float) (1.0f * ForestDragonConfig.stressExhaustion));
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int i = 20 >> amplifier;

        if (i > 0) {
            return duration % i == 0;
        } else {
            return true;
        }
    }
}