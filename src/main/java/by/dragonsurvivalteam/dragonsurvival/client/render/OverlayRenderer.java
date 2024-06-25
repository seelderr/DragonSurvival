package by.dragonsurvivalteam.dragonsurvival.client.render;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;

import java.util.Objects;

@EventBusSubscriber
public class OverlayRenderer {
    @SubscribeEvent
    @OnlyIn( Dist.CLIENT )
    public static void removeFireOverlay(RenderBlockScreenEffectEvent event){
        LocalPlayer player = Minecraft.getInstance().player;
        DragonStateProvider.getCap(player).ifPresent(cap -> {
            if(cap.isDragon() && Objects.equals(cap.getType(), DragonTypes.CAVE) && event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE){
                event.setCanceled(true);
            }
        });
    }
}
