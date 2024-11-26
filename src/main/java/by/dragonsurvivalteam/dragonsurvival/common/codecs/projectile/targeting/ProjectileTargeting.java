package by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.targeting;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.WeatherPredicate;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.ProjectileInstance;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.world_effects.ProjectileWorldEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Optional;
import java.util.function.Function;

public interface ProjectileTargeting {
    ResourceKey<Registry<MapCodec<? extends ProjectileTargeting>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("projectile_targeting"));
    Registry<MapCodec<? extends ProjectileTargeting>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<ProjectileTargeting> CODEC = REGISTRY.byNameCodec().dispatch(ProjectileTargeting::codec, Function.identity());

    record BlockTargeting(Optional<BlockPredicate> targetConditions, ProjectileBlockEffect effect) {
        public static final Codec<ProjectileTargeting.BlockTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(ProjectileTargeting.BlockTargeting::targetConditions),
                ProjectileBlockEffect.CODEC.fieldOf("block_effect").forGetter(ProjectileTargeting.BlockTargeting::effect)
        ).apply(instance, ProjectileTargeting.BlockTargeting::new));
    }

    record EntityTargeting(Optional<EntityPredicate> targetConditions, ProjectileEntityEffect effect) {
        public static final Codec<ProjectileTargeting.EntityTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(ProjectileTargeting.EntityTargeting::targetConditions),
                ProjectileEntityEffect.CODEC.fieldOf("entity_effect").forGetter(ProjectileTargeting.EntityTargeting::effect)
        ).apply(instance, ProjectileTargeting.EntityTargeting::new));
    }

    record WorldTargeting(Optional<LocationPredicate> locationConditions, Optional<WeatherPredicate> weatherConditions, ProjectileWorldEffect effect) {
        public static final Codec<ProjectileTargeting.WorldTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LocationPredicate.CODEC.optionalFieldOf("location_conditions").forGetter(ProjectileTargeting.WorldTargeting::locationConditions),
                WeatherPredicate.CODEC.optionalFieldOf("weather_conditions").forGetter(ProjectileTargeting.WorldTargeting::weatherConditions),
                ProjectileWorldEffect.CODEC.fieldOf("world_effect").forGetter(ProjectileTargeting.WorldTargeting::effect)
        ).apply(instance, ProjectileTargeting.WorldTargeting::new));
    }

    void apply(final ServerLevel level, final ServerPlayer player, final ProjectileInstance projectile, final Vec3 position);
    MapCodec<? extends ProjectileTargeting> codec();
}
