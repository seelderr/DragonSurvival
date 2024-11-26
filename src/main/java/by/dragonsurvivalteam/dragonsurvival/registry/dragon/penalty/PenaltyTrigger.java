package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

public interface PenaltyTrigger {
    ResourceKey<Registry<MapCodec<? extends PenaltyTrigger>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("penalty_triggers"));
    Registry<MapCodec<? extends PenaltyTrigger>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<PenaltyTrigger> CODEC = REGISTRY.byNameCodec().dispatch(PenaltyTrigger::codec, Function.identity());

    boolean matches(final PenaltyInstance instance, boolean conditionMatched);
    MapCodec<? extends PenaltyTrigger> codec();
}
