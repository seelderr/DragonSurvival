package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtil;

@Mixin(value = GeoModel.class, remap = false)
public abstract class MixinGeoModel<T extends GeoAnimatable> {

    // The dragon entity doesn't tick, so we apply the time it would've taken for it to tick in this mixin for GeckoLib to work properly
    @ModifyArg(method = "handleAnimations", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/animation/AnimatableManager;updatedAt(D)V"))
    private double applyDragonTickTime(double updateTime, @Local(argsOnly = true) T animatable, @Local(argsOnly = true) long instanceId) {
        if (animatable instanceof DragonEntity) {
            AnimatableManager<GeoAnimatable> manager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);
            return RenderUtil.getCurrentTick() - manager.getFirstTickTime();
        }

        return updateTime;
    }
}
