package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoModel;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtils;

@Mixin(value = GeoModel.class, remap = false) // FIXME :: Adopts the previous `setCustomAnimations` fix (needs looking into why this is needed) (need to make DragonEntity not extend Entity?)
public abstract class MixinGeoModel<T extends GeoAnimatable> implements CoreGeoModel<T> {
    @Unique private T dragonSurvival$animatable;
    @Unique private long dragonSurvival$instanceId;

    @Inject(method = "handleAnimations", at = @At("HEAD"))
    public void overrideTick(final T animatable, long instanceId, final AnimationState<T> animationState, final CallbackInfo callback) {
        dragonSurvival$animatable = animatable;
        dragonSurvival$instanceId = instanceId;
        /*animationState.getData(DataTickets.TICK);*/ // TODO :: Is always 0 -> because the dragon entity does not tick?
    }

    @ModifyVariable(method = "handleAnimations", at = @At("STORE"), name = "currentFrameTime")
    public double overrideTick(double value) {
        if (dragonSurvival$animatable instanceof DragonEntity) {
            AnimatableManager<GeoAnimatable> manager = dragonSurvival$animatable.getAnimatableInstanceCache().getManagerForId(dragonSurvival$instanceId);
            return RenderUtils.getCurrentTick() - manager.getFirstTickTime();
        }

        return value;
    }
}
