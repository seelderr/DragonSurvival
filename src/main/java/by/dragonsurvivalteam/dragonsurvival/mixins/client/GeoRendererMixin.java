package by.dragonsurvivalteam.dragonsurvival.mixins.client;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import software.bernie.geckolib.renderer.GeoRenderer;

@Mixin(value = GeoRenderer.class, remap = false)
public interface GeoRendererMixin {
    @ModifyArg(
        method = "defaultRender",
        at = @At(
            value = "INVOKE",
            target = "Lsoftware/bernie/geckolib/renderer/GeoRenderer;preApplyRenderLayers(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;FII)V"
        ),
        index = 6
    )
    private float dragonSurvival$usePartialTickForPreRenderLayers(final float packedLight,
                                                                  @Local(argsOnly = true, ordinal = 1) final float partialTick) {
        return partialTick;
    }
}
