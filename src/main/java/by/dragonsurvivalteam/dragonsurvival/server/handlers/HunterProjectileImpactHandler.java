package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.DragonHunter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@EventBusSubscriber
public class HunterProjectileImpactHandler {

    @SubscribeEvent
    public static void onHunterProjectileImpact(ProjectileImpactEvent event) {
        if(event.getProjectile() instanceof AbstractArrow arrow) {
            if(arrow.getOwner() instanceof DragonHunter) {
                if (event.getRayTraceResult() instanceof EntityHitResult result) {
                    Entity entity = result.getEntity();
                    if(entity instanceof DragonHunter) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
