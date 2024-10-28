package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.DSEntityTypeTags;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class EntityHandler {
	/**
	 * Adds dragon avoidance goal
	 */
	@SubscribeEvent
	public static void onJoin(EntityJoinLevelEvent joinWorldEvent) {
		if (joinWorldEvent.getEntity() instanceof Animal animal && !animal.getType().is(DSEntityTypeTags.ANIMAL_AVOID_BLACKLIST)) {
			animal.goalSelector.addGoal(5, new AvoidEntityGoal<>(animal, Player.class, living -> ServerConfig.dragonsAreScary && !living.hasEffect(DSEffects.ANIMAL_PEACE) && DragonStateProvider.isDragon(living), 20.0F, 1.3F, 1.5F, s -> true));
		}
	}
}
