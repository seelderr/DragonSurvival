package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

// TODO :: block the dragon is looking at
public record BlockTarget(LevelBasedValue radius) implements Targeting {
    @Override
    public void apply(ServerLevel level, Player dragon, DragonAbilityInstance ability) {

    }

    @Override
    public MapCodec<? extends Targeting> codec() {
        return null;
    }
}
