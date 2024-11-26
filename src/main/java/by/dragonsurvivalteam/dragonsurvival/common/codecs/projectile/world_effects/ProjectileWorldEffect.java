package by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.world_effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.ProjectileInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

public interface ProjectileWorldEffect {
    ResourceKey<Registry<MapCodec<? extends ProjectileWorldEffect>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("projectile_world_effects"));
    Registry<MapCodec<? extends ProjectileWorldEffect>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<ProjectileWorldEffect> CODEC = REGISTRY.byNameCodec().dispatch(ProjectileWorldEffect::worldCodec, Function.identity());

    void apply(final ServerLevel level, final ServerPlayer dragon, final ProjectileInstance projectile, final Vec3 position);
    MapCodec<? extends ProjectileWorldEffect> worldCodec();
}