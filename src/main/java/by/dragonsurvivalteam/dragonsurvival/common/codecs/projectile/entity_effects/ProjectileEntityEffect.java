package by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.ProjectileInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

public interface ProjectileEntityEffect {
    ResourceKey<Registry<MapCodec<? extends ProjectileEntityEffect>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("projectile_entity_effects"));
    Registry<MapCodec<? extends ProjectileEntityEffect>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<ProjectileEntityEffect> CODEC = REGISTRY.byNameCodec().dispatch(ProjectileEntityEffect::entityCodec, Function.identity());

    void apply(final ServerLevel level, final ServerPlayer player, final ProjectileInstance projectile, final Entity entity);
    MapCodec<? extends ProjectileEntityEffect> entityCodec();
}
