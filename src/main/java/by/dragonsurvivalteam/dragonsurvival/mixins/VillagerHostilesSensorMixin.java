package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {

	@Inject(at = @At(value = "HEAD"), method = "isClose", cancellable = true)
	public void isCloseToMarkedEntity(LivingEntity pTarget, LivingEntity pAttacker, CallbackInfoReturnable<Boolean> cir) {
		if (pAttacker.hasEffect(DSEffects.HUNTER_OMEN)) {
			cir.setReturnValue(pTarget.distanceToSqr(pAttacker) <= (double) (8.0F * 8.0F));
		}
	}

	@ModifyReturnValue(at = @At(value = "RETURN"), method = "isHostile")
	public boolean isHostileToMarkedEntity(boolean original, @Local(argsOnly = true) LivingEntity pAttacker) {
		if (pAttacker.hasEffect(DSEffects.HUNTER_OMEN)) {
			return true;
		} else {
			return original;
		}
	}
}
