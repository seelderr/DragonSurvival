package by.dragonsurvivalteam.dragonsurvival.mixins.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(value = GeoEntityRenderer.class, remap = false)
public abstract class GeoEntityRendererMixin {
    @Inject(
        method = "actuallyRender",
        at = @At(
            value = "INVOKE",
            target = "Lsoftware/bernie/geckolib/renderer/GeoRenderer;actuallyRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIII)V"
        )
    )
    private void dragonSurvival$applyIkAfterAnimations(final PoseStack poseStack,
                                                        final Entity animatable,
                                                        final BakedGeoModel model,
                                                        final RenderType renderType,
                                                        final MultiBufferSource bufferSource,
                                                        final VertexConsumer buffer,
                                                        final boolean isReRender,
                                                        final float partialTick,
                                                        final int packedLight,
                                                        final int packedOverlay,
                                                        final int color,
                                                        final CallbackInfo callback) {
        if (!isReRender && (Object)this instanceof DragonRenderer renderer && animatable instanceof DragonEntity dragon) {
            renderer.dragonSurvival$applyIk(poseStack, dragon, model, bufferSource, partialTick);
        }
    }
}
