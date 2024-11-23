package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber
public class DataReloadHandler {
    @SubscribeEvent
    public static void handleDatapackReload(final TagsUpdatedEvent event) {
        DragonStage.update(event.getRegistryAccess());
    }
}
