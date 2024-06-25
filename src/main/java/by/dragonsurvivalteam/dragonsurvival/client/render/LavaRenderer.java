package by.dragonsurvivalteam.dragonsurvival.client.render;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber
public class LavaRenderer {
    private static boolean hasUpdatedSinceChangingLavaVision = false;
    private static boolean hasLavaVisionPrev = false;

    @SubscribeEvent
    @OnlyIn( Dist.CLIENT )
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if(player.hasEffect(DSEffects.LAVA_VISION) && event.getCamera().getFluidInCamera() == FogType.LAVA) {
            event.setFarPlaneDistance(1000);
        }
    }

    @SubscribeEvent
    @OnlyIn( Dist.CLIENT )
    public static void onRenderWorldLastEvent(RenderLevelStageEvent event){
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES){
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if(player == null){
            return;
        }

        if(player.hasEffect(DSEffects.LAVA_VISION)) {
            if(!hasLavaVisionPrev) {
                hasUpdatedSinceChangingLavaVision = false;
            }

            hasLavaVisionPrev = true;
            if(!hasUpdatedSinceChangingLavaVision) {
                hasUpdatedSinceChangingLavaVision = true;
                event.getLevelRenderer().allChanged();
            }
        }
        else {
            if(hasLavaVisionPrev) {
                hasUpdatedSinceChangingLavaVision = false;
            }

            hasLavaVisionPrev = false;
            if(!hasUpdatedSinceChangingLavaVision) {
                hasUpdatedSinceChangingLavaVision = true;
                event.getLevelRenderer().allChanged();
            }
        }
    }
}
