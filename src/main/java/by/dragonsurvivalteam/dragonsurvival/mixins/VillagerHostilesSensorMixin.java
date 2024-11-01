package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {
    @ModifyVariable(method = "isClose", at = @At(value = "STORE")) // 'attacker' is the villager itself
    public float dragonSurvival$setHunterOmenDistance(float distance, @Local(argsOnly = true, ordinal = 1) final LivingEntity target) {
        if (target.hasEffect(DSEffects.HUNTER_OMEN) && distance < 8) {
            distance = 8;
        }

        return distance;
    }

    @ModifyReturnValue(method = "isHostile", at = @At(value = "RETURN"))
    public boolean dragonSurvival$setHunterOmenAsHostile(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        return original || entity.hasEffect(DSEffects.HUNTER_OMEN);
    }
}
