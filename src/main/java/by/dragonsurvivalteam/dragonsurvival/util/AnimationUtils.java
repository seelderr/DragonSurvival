package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.mixins.client.AnimationControllerAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

public class AnimationUtils {
    public static RawAnimation createAnimation(@Nullable final RawAnimation builder, @NotNull final RawAnimation staticAnimation) {
        if (builder == null) {
            return staticAnimation;
        }

        assert staticAnimation.getAnimationStages().size() == 1;
        RawAnimation.Stage stage = staticAnimation.getAnimationStages().get(0);
        builder.then(stage.animationName(), stage.loopType());

        return builder;
    }

    public static <E extends GeoAnimatable> void setAnimationSpeed(double speed, double currentAnimationTick, AnimationController<E> controller) {

        if (speed == controller.getAnimationSpeed()) {
            return;
        }

        if (controller.getCurrentAnimation() != null) {
            double distance = currentAnimationTick - ((AnimationControllerAccessor) controller).dragonSurvival$getTickOffset();
            ((AnimationControllerAccessor) controller).dragonSurvival$setTickOffset(currentAnimationTick - distance * (controller.getAnimationSpeed() / speed));
            controller.setAnimationSpeed(speed);
        }
    }

    // TODO: This is a hack since GeckoLib's state.isCurrentAnimation() doesn't work. If they ever fix that, we can remove this.
    public static boolean isAnimationPlaying(AnimationController<?> controller, String animationName) {
        return controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animation().name().equals(animationName);
    }

    public static double getMovementSpeed(LivingEntity of) {
        return Math.sqrt(Math.pow(of.getX() - of.xo, 2) + Math.pow(of.getZ() - of.zo, 2));
    }
}
