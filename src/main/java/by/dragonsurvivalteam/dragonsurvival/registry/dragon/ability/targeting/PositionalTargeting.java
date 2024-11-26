package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

public interface PositionalTargeting extends Targeting {
    ResourceKey<Registry<MapCodec<? extends PositionalTargeting>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("positional_targeting"));
    Registry<MapCodec<? extends PositionalTargeting>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<PositionalTargeting> CODEC = REGISTRY.byNameCodec().dispatch(PositionalTargeting::codec, Function.identity());

    void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final Vec3 position);
    MapCodec<? extends PositionalTargeting> codec();
}
