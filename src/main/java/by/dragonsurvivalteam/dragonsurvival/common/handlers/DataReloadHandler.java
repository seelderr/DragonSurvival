package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber
public class DataReloadHandler {
    @SubscribeEvent
    public static void handleDatapackReload(final TagsUpdatedEvent event) {
        DragonLevel.update(event.getRegistryAccess());
    }
}
