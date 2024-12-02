//package by.dragonsurvivalteam.dragonsurvival.common.items.food;
//
//import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
//import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
//import by.dragonsurvivalteam.dragonsurvival.util.Functions;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.effect.MobEffects;
//import net.minecraft.world.food.FoodProperties;
//
//public class ChargedSoupItem extends DragonFoodItem {
//    public ChargedSoupItem(Properties properties) {
//        super(properties.food(new FoodProperties.Builder()
//                .nutrition(1)
//                .saturationModifier(0.4f)
//                .alwaysEdible()
//                .effect(() -> new MobEffectInstance(MobEffects.POISON, Functions.secondsToTicks(15), 0), 1)
//                .effect(() -> new MobEffectInstance(DSEffects.FIRE, Functions.secondsToTicks(DragonFoodHandler.chargedSoupBuffDuration), 0), 1)
//                .build())
//        );
//    }
//}