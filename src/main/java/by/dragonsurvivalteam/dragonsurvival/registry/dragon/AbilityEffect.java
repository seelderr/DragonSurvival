package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface AbilityEffect {
    void apply(final ServerLevel level, final Player player, final DragonAbilityInstance ability, final Vec3 origin);
}
