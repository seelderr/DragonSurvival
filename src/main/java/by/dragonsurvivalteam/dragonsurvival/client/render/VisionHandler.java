package by.dragonsurvivalteam.dragonsurvival.client.render;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/** Handles water and lava vision effects */
@EventBusSubscriber(Dist.CLIENT)
public class VisionHandler {
    private static boolean hasUpdatedSinceChangingLavaVision = false;
    private static boolean hasLavaVisionPrev = false;

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (hasLavaVision() && event.getCamera().getFluidInCamera() == FogType.LAVA) {
            event.setNearPlaneDistance(0);
            event.setFarPlaneDistance(event.getRenderer().getRenderDistance() * 0.2f);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLastEvent(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        if (hasLavaVision()) {
            if (!hasLavaVisionPrev) {
                hasUpdatedSinceChangingLavaVision = false;
            }

            hasLavaVisionPrev = true;
        } else {
            if (hasLavaVisionPrev) {
                hasUpdatedSinceChangingLavaVision = false;
            }

            hasLavaVisionPrev = false;
        }

        if (!hasUpdatedSinceChangingLavaVision) {
            hasUpdatedSinceChangingLavaVision = true;
            event.getLevelRenderer().allChanged();
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.ComputeFogColor event) {
        float adjustment = 1;

        if (hasLavaVision() && event.getCamera().getFluidInCamera() == FogType.LAVA) {
            adjustment = 0.15f;
        } else if (hasWaterVision() && event.getCamera().getFluidInCamera() == FogType.WATER) {
            adjustment = 0.25f;
        }

        event.setBlue(event.getBlue() * adjustment);
        event.setRed(event.getRed() * adjustment);
        event.setGreen(event.getGreen() * adjustment);
    }

    public static boolean hasLavaVision() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return false;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            return player.hasEffect(DSEffects.LAVA_VISION);
        }

        return false;
    }

    public static boolean hasWaterVision() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return false;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            return player.hasEffect(DSEffects.WATER_VISION);
        }

        return false;
    }
}
