package by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.block_effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.ProjectileInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;


public interface ProjectileBlockEffect {
    ResourceKey<Registry<MapCodec<? extends ProjectileBlockEffect>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("projectile_block_effects"));
    Registry<MapCodec<? extends ProjectileBlockEffect>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<ProjectileBlockEffect> CODEC = REGISTRY.byNameCodec().dispatch(ProjectileBlockEffect::blockCodec, Function.identity());

    void apply(final ServerLevel level, final ServerPlayer player, final ProjectileInstance projectile, final BlockPos position);
    MapCodec<? extends ProjectileBlockEffect> blockCodec();
}
