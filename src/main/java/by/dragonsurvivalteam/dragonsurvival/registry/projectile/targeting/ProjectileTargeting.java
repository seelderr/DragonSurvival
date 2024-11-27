package by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
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

import java.util.Optional;
import java.util.function.Function;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public interface ProjectileTargeting {
    ResourceKey<Registry<MapCodec<? extends ProjectileTargeting>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("projectile_targeting"));
    Registry<MapCodec<? extends ProjectileTargeting>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<ProjectileTargeting> CODEC = REGISTRY.byNameCodec().dispatch(ProjectileTargeting::codec, Function.identity());

    record BlockTargeting(Optional<BlockPredicate> targetConditions, ProjectileBlockEffect effect, int tickRate) {
        public static final Codec<ProjectileTargeting.BlockTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(ProjectileTargeting.BlockTargeting::targetConditions),
                ProjectileBlockEffect.CODEC.fieldOf("block_effect").forGetter(ProjectileTargeting.BlockTargeting::effect),
                Codec.INT.optionalFieldOf("tick_rate", 1).forGetter(ProjectileTargeting.BlockTargeting::tickRate)
        ).apply(instance, ProjectileTargeting.BlockTargeting::new));
    }

    record EntityTargeting(Optional<EntityPredicate> targetConditions, ProjectileEntityEffect effect, int tickRate) {
        public static final Codec<ProjectileTargeting.EntityTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(ProjectileTargeting.EntityTargeting::targetConditions),
                ProjectileEntityEffect.CODEC.fieldOf("entity_effect").forGetter(ProjectileTargeting.EntityTargeting::effect),
                Codec.INT.optionalFieldOf("tick_rate", 1).forGetter(ProjectileTargeting.EntityTargeting::tickRate)
        ).apply(instance, ProjectileTargeting.EntityTargeting::new));
    }

    record WorldTargeting(Optional<LocationPredicate> locationConditions, Optional<WeatherPredicate> weatherConditions, ProjectileWorldEffect effect, int tickRate) {
        public static final Codec<ProjectileTargeting.WorldTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LocationPredicate.CODEC.optionalFieldOf("location_conditions").forGetter(ProjectileTargeting.WorldTargeting::locationConditions),
                WeatherPredicate.CODEC.optionalFieldOf("weather_conditions").forGetter(ProjectileTargeting.WorldTargeting::weatherConditions),
                ProjectileWorldEffect.CODEC.fieldOf("world_effect").forGetter(ProjectileTargeting.WorldTargeting::effect),
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
