package by.dragonsurvivalteam.dragonsurvival.api.appleskin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import squeek.appleskin.api.event.FoodValuesEvent;

@OnlyIn(Dist.CLIENT)
public class AppleSkinEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void applyDragonFoodPropertiesToAppleSkin(final FoodValuesEvent event) {
        DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(handler -> {
            if (handler.isDragon()) {
                event.modifiedFoodProperties = DragonFoodHandler.getDragonFoodProperties(event.itemStack, handler.getType());
            }
        });
    }
}