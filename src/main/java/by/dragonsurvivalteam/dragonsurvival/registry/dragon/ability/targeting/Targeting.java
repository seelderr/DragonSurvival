package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

public interface Targeting {
    ResourceKey<Registry<MapCodec<? extends Targeting>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("targeting"));
    Registry<MapCodec<? extends Targeting>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<Targeting> CODEC = REGISTRY.byNameCodec().dispatch(Targeting::codec, Function.identity());

    void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability);
    MapCodec<? extends Targeting> codec();
}
