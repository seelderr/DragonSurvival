package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Camera;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @ModifyReturnValue(method = "getFluidInCamera", at = @At(value = "RETURN", ordinal = 2))
    private FogType dragonSurvival$enableLavaVision(final FogType type) {
        if (DragonUtils.hasLavaVision()) {
            return FogType.NONE;
        }

        return type;
    }
}
