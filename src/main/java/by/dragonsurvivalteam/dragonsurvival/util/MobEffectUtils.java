package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public class MobEffectUtils {

    public static Holder<MobEffect> getHolder(MobEffect effect) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(BuiltInRegistries.MOB_EFFECT.getKey(effect)).get();
    }
}
