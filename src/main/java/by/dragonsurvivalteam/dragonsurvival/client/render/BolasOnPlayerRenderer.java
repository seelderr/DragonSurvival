package by.dragonsurvivalteam.dragonsurvival.client.render;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

@EventBusSubscriber
public class BolasOnPlayerRenderer {
    private static ItemStack BOLAS;

    @SubscribeEvent
    public static void renderTrap(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> postEvent){
        LivingEntity entity = postEvent.getEntity();
        if(entity.getEffect(DSEffects.TRAPPED) != null) {
            int light = postEvent.getPackedLight();
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(entity, 0);
            MultiBufferSource buffers = postEvent.getMultiBufferSource();
            PoseStack matrixStack = postEvent.getPoseStack();
            float scale = entity.getEyeHeight();
            if(entity instanceof Player) {
                DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(entity);
                if(handler != null && handler.isDragon()) {
                    scale = (float) DragonSizeHandler.calculateDragonEyeHeight(handler.getSize());
                }
            }
            renderBolas(light, overlayCoords, buffers, matrixStack, scale);
        }
    }

    public static void renderBolas(int light, int overlayCoords, MultiBufferSource buffers, PoseStack matrixStack, float eyeHeight){
        matrixStack.pushPose();
        matrixStack.translate(0, 0.9f + eyeHeight / 8.f, 0);
        matrixStack.scale(1.6f + eyeHeight / 8.f, 1.6f + eyeHeight / 8.f, 1.6f + eyeHeight / 8.f);
        if(BOLAS == null){
            BOLAS = new ItemStack(DSItems.HUNTING_NET);
        }
        Minecraft.getInstance().getItemRenderer().renderStatic(BOLAS, ItemDisplayContext.NONE, light, overlayCoords, matrixStack, buffers, Minecraft.getInstance().level, 0);
        matrixStack.popPose();
    }
}
