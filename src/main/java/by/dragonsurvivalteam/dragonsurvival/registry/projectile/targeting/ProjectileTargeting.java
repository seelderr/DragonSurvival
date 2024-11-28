package by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.RandomPredicate;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.WeatherPredicate;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects.ProjectileWorldEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public interface ProjectileTargeting {
    ResourceKey<Registry<MapCodec<? extends ProjectileTargeting>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("projectile_targeting"));
    Registry<MapCodec<? extends ProjectileTargeting>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<ProjectileTargeting> CODEC = REGISTRY.byNameCodec().dispatch(ProjectileTargeting::codec, Function.identity());

    record BlockTargeting(Optional<BlockPredicate> targetConditions, Optional<WeatherPredicate> weatherConditions, Optional<RandomPredicate> randomCondition, List<ProjectileBlockEffect> effects, int tickRate) {
        public static final Codec<ProjectileTargeting.BlockTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(ProjectileTargeting.BlockTargeting::targetConditions),
                WeatherPredicate.CODEC.optionalFieldOf("weather_conditions").forGetter(ProjectileTargeting.BlockTargeting::weatherConditions),
                RandomPredicate.CODEC.optionalFieldOf("random_conditions").forGetter(ProjectileTargeting.BlockTargeting::randomCondition),
                ProjectileBlockEffect.CODEC.listOf().fieldOf("block_effects").forGetter(ProjectileTargeting.BlockTargeting::effects),
                Codec.INT.optionalFieldOf("tick_rate", 1).forGetter(ProjectileTargeting.BlockTargeting::tickRate)
        ).apply(instance, ProjectileTargeting.BlockTargeting::new));
    }

    record EntityTargeting(Optional<EntityPredicate> targetConditions, Optional<WeatherPredicate> weatherConditions, Optional<RandomPredicate> randomCondition, List<ProjectileEntityEffect> effects, int tickRate) {
        public static final Codec<ProjectileTargeting.EntityTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(ProjectileTargeting.EntityTargeting::targetConditions),
                WeatherPredicate.CODEC.optionalFieldOf("weather_conditions").forGetter(ProjectileTargeting.EntityTargeting::weatherConditions),
                RandomPredicate.CODEC.optionalFieldOf("random_conditions").forGetter(ProjectileTargeting.EntityTargeting::randomCondition),
                ProjectileEntityEffect.CODEC.listOf().fieldOf("entity_effects").forGetter(ProjectileTargeting.EntityTargeting::effects),
                Codec.INT.optionalFieldOf("tick_rate", 1).forGetter(ProjectileTargeting.EntityTargeting::tickRate)
        ).apply(instance, ProjectileTargeting.EntityTargeting::new));
    }

    record WorldTargeting(Optional<LocationPredicate> locationConditions, Optional<WeatherPredicate> weatherConditions, Optional<RandomPredicate> randomConditions, List<ProjectileWorldEffect> effects, int tickRate) {
        public static final Codec<ProjectileTargeting.WorldTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LocationPredicate.CODEC.optionalFieldOf("location_conditions").forGetter(ProjectileTargeting.WorldTargeting::locationConditions),
                WeatherPredicate.CODEC.optionalFieldOf("weather_conditions").forGetter(ProjectileTargeting.WorldTargeting::weatherConditions),
                RandomPredicate.CODEC.optionalFieldOf("random_conditions").forGetter(ProjectileTargeting.WorldTargeting::randomConditions),
                ProjectileWorldEffect.CODEC.listOf().fieldOf("world_effects").forGetter(ProjectileTargeting.WorldTargeting::effects),
                Codec.INT.optionalFieldOf("tick_rate", 1).forGetter(ProjectileTargeting.WorldTargeting::tickRate)
        ).apply(instance, ProjectileTargeting.WorldTargeting::new));
    }

    @SubscribeEvent
    static void register(final NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    @SubscribeEvent
    static void registerEntries(final RegisterEvent event) {
        if (event.getRegistry() == REGISTRY) {
            event.register(REGISTRY_KEY, DragonSurvival.res("area"), () -> ProjectileAreaTarget.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("point"), () -> ProjectilePointTarget.CODEC);
        }
    }

    void apply(final Projectile projectile, int projectileLevel);
    MapCodec<? extends ProjectileTargeting> codec();
}
