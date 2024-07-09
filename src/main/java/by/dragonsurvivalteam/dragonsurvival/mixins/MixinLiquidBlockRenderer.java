package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin( LiquidBlockRenderer.class )
public class MixinLiquidBlockRenderer {
    @ModifyVariable(method = "tesselate", at = @At(value = "STORE"), ordinal = 0)
    private float modifyAlphaForLavaVision(float value, @Local(ordinal = 0) boolean isLava) {
        if (isLava && DragonUtils.hasLavaVision()) {
            return value * 0.25f;
        }

        return value;
    }
}
