package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

// TODO :: entity the dragon is looking at
// Radius will be optional, by default 0 - if it's larger than 0 then the are around the entity will also be considered
public record EntityTarget(LevelBasedValue radius) implements Targeting {
    @Override
    public void apply(ServerLevel level, Player dragon, DragonAbilityInstance ability) {

    }

    @Override
    public MapCodec<? extends Targeting> codec() {
        return null;
    }
}
