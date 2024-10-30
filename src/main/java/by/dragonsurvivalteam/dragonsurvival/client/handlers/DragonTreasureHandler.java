package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncTreasureRestStatus;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.util.Color;

/** See {@link by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonTreasureHandler} for common logic */
@EventBusSubscriber(Dist.CLIENT)
public class DragonTreasureHandler {
    private static int sleepTimer = 0;

    @SubscribeEvent
    public static void playerTick(ClientTickEvent.Post event) {
        Player player = DragonSurvivalMod.PROXY.getLocalPlayer();

        if (DragonStateProvider.isDragon(player)) {
            DragonStateHandler handler = DragonStateProvider.getData(player);

            if (handler.treasureResting) {
                Vec3 velocity = player.getDeltaMovement();
                float groundSpeed = Mth.sqrt((float) (velocity.x * velocity.x + velocity.z * velocity.z));
                if (Math.abs(groundSpeed) > 0.05 || handler.getMovementData().dig) {
                    handler.treasureResting = false;
                    PacketDistributor.sendToServer(new SyncTreasureRestStatus.Data(player.getId(), false));
                }
            }
        }
    }

    @SubscribeEvent
    public static void sleepScreenRender(RenderGuiLayerEvent.Post event) {
        Player playerEntity = Minecraft.getInstance().player;

        if (!DragonStateProvider.isDragon(playerEntity) || playerEntity.isSpectator()) {
            return;
        }

        DragonStateProvider.getOptional(playerEntity).ifPresent(cap -> {
            if (event.getName() == VanillaGuiLayers.AIR_LEVEL) {

                Window window = Minecraft.getInstance().getWindow();
                float f = playerEntity.level().getSunAngle(1.0F);

                float f1 = f < (float) Math.PI ? 0.0F : (float) Math.PI * 2F;
                f = f + (f1 - f) * 0.2F;
                double val = Mth.cos(f);
                if (cap.treasureResting && val < 0.25 && sleepTimer < 100) {
                    sleepTimer++;
                } else if (sleepTimer > 0) {
                    sleepTimer--;
                }
                if (sleepTimer > 0) {
                    Color darkening = Color.ofRGBA(0.05f, 0.05f, 0.05f, Mth.lerp(Math.min(sleepTimer, 100) / 100f, 0, 0.5F));
                    event.getGuiGraphics().fill(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), darkening.getColor());
                }
            }
        });
    }
}
