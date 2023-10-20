package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Unique private int dragonSurvival$lastLog;

    @Inject(method = "checkPoseStack", at = @At("HEAD"))
    private void clearPoseStack(final PoseStack poseStack, final CallbackInfo callback) {
        if (!ClientConfig.emptyPoseStack) {
            return;
        }

        // To make sure the size is at least 1
        poseStack.pushPose();

        int popped = -1;

        while (!poseStack.clear()) {
            poseStack.popPose();
            popped++;
        }

        if (Minecraft.getInstance().player != null) {
            if (Minecraft.getInstance().player.tickCount - dragonSurvival$lastLog > Functions.secondsToTicks(30)) {
                if (popped == -1) {
                    DragonSurvivalMod.LOGGER.warn("Pose stack had no elements");
                } else if (popped > 0) {
                    DragonSurvivalMod.LOGGER.warn("Pose stack had [{}] additional stacks", popped);
                }

                dragonSurvival$lastLog = Minecraft.getInstance().player.tickCount;
            }
        }
    }
}
