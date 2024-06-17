package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
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
    public static void onJoin(EntityJoinLevelEvent joinWorldEvent){
        Entity entity = joinWorldEvent.getEntity();
        if(entity instanceof Animal && !(entity instanceof Wolf || entity instanceof Hoglin)){
            ((Animal)entity).goalSelector.addGoal(5, new AvoidEntityGoal((Animal)entity, Player.class, living -> DragonStateProvider.isDragon((Player)living) && !((Player)living).hasEffect(DSEffects.ANIMAL_PEACE), 20.0F, 1.3F, 1.5F, s -> true));
        }
    }
}
