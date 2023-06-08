package by.dragonsurvivalteam.dragonsurvival.api.appleskin;

import by.dragonsurvivalteam.dragonsurvival.api.DragonFood;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class AppleSkinEventHandler {
    @SubscribeEvent
    public void onToolTipEvent(final FoodValuesEvent event) {
        if (!DragonUtils.isDragon(event.player)) {
            return;
        }

        FoodProperties foodProperties = DragonFood.getEffectiveFoodProperties(event.itemStack.getItem(), Minecraft.getInstance().player);

        if (foodProperties == null) {
            return;
        }

        // TODO :: remove (+ accesstransformer part)
        // This also removes those effects from the food
//        if (foodProperties.getNutrition() > 0) {
//            FoodProperties itemFoodProperties = event.itemStack.getItem().getFoodProperties(event.itemStack, event.player);
//            List<Pair<Supplier<MobEffectInstance>, Float>> effects = itemFoodProperties.effects;
//
//            // Could also do: !(pair.getFirst().get().getEffect().getCategory() == MobEffectCategory.HARMFUL))
//            effects = effects.stream()
//                    .filter(pair -> isEffectRelevant(pair.getFirst()))
//                    .toList();
//
//            itemFoodProperties.effects = effects;
//        }

        event.modifiedFoodValues = new FoodValues(foodProperties.getNutrition(), foodProperties.getSaturationModifier());
    }

    private boolean isEffectRelevant(final Supplier<MobEffectInstance> first) {
        MobEffect effect = first.get().getEffect();
        return !effect.equals(MobEffects.HUNGER) && !effect.equals(MobEffects.POISON);
    }
}