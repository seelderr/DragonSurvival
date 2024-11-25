package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber
public class DataReloadHandler {
    @SubscribeEvent
    public static void handleDatapackReload(final TagsUpdatedEvent event) {
        DragonStage.update(event.getRegistryAccess());
        DragonAbility.update(event.getRegistryAccess());
        DragonType.update(event.getRegistryAccess());
    }
}
