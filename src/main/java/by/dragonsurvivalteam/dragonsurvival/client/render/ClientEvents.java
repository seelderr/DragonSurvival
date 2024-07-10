package by.dragonsurvivalteam.dragonsurvival.client.render;

import by.dragonsurvivalteam.dragonsurvival.client.util.ClientUtils;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    private static boolean hasUpdatedSinceChangingLavaVision = false;
    private static boolean hasLavaVisionPrev = false;

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (ClientUtils.hasLavaVision() && event.getCamera().getFluidInCamera() == FogType.LAVA) {
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

        if (ClientUtils.hasLavaVision()) {
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

        if (ClientUtils.hasLavaVision() && event.getCamera().getFluidInCamera() == FogType.LAVA) {
            adjustment = 0.15f;
        } else if (ClientUtils.hasWaterVision() && event.getCamera().getFluidInCamera() == FogType.WATER) {
            adjustment = 0.25f;
        }

        event.setBlue(event.getBlue() * adjustment);
        event.setRed(event.getRed() * adjustment);
        event.setGreen(event.getGreen() * adjustment);
    }

    @SubscribeEvent
    public static void removeFireOverlay(RenderBlockScreenEffectEvent event) {
        if (event.getOverlayType() != RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            return;
        }

        DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(handler -> {
            if (DragonUtils.isDragonType(handler, DragonTypes.CAVE)) {
                event.setCanceled(true);
            }
        });
    }
}
