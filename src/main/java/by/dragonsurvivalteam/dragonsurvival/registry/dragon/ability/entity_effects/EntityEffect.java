package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

public interface EntityEffect { // TODO :: split into entity and block effects?
    ResourceKey<Registry<MapCodec<? extends EntityEffect>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("ability_effects"));
    Registry<MapCodec<? extends EntityEffect>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<EntityEffect> CODEC = REGISTRY.byNameCodec().dispatch(EntityEffect::entityCodec, Function.identity());

    /* Not sure if this would even work:
        ResourceKey<Registry<MapCodec<? extends AbilityEffect>>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_abilities"));
        Lazy<Codec<AbilityEffect>> CODEC = Lazy.of(() -> ((Registry<MapCodec<? extends AbilityEffect>>) BuiltInRegistries.REGISTRY.get(REGISTRY.registry())).byNameCodec().dispatch(AbilityEffect::codec, Function.identity()));
    */

    void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final Entity entity);
    MapCodec<? extends EntityEffect> entityCodec();
}
