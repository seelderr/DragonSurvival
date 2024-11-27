package by.dragonsurvivalteam.dragonsurvival.registry.projectile;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileDamageEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectilePointTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectileTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects.ProjectileExplosionEffect;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;
import java.util.Optional;

public class Projectiles {
    @Translation(type = Translation.Type.PROJECTILE, comments = {"Fireball"})
    public static final ResourceKey<ProjectileData> FIREBALL = key("fireball");

    @Translation(type = Translation.Type.PROJECTILE, comments = {"Spike"})
    public static final ResourceKey<ProjectileData> SPIKE = key("spike");

    public static void registerProjectiles(final BootstrapContext<ProjectileData> context) {
        context.register(FIREBALL, new ProjectileData(
                FIREBALL.location(),
                Either.right(
                        new ProjectileData.GenericBallData(
                                Optional.of(ParticleTypes.LARGE_SMOKE),
                                List.of(new ProjectilePointTarget(
                                        new ProjectileTargeting.WorldTargeting(
                                                Optional.empty(),
                                                Optional.empty(),
                                                new ProjectileExplosionEffect(
                                                        context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.FIREBALL),
                                                        LevelBasedValue.perLevel(1),
                                                        true,
                                                        true,
                                                        true
                                                ),
                                                1)
                                        )
                                ),
                                LevelBasedValue.constant(1f),
                                LevelBasedValue.constant(1f),
                                LevelBasedValue.constant(0),
                                LevelBasedValue.constant(32),
                                LevelBasedValue.constant(100)
                        )
                ),
                Optional.empty(),
                List.of(),
                List.of(),
                List.of(
                        new ProjectileDamageEffect(
                                context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.FIREBALL),
                                LevelBasedValue.perLevel(5)
                        )
                ),
                List.of())
        );

        context.register(SPIKE, new ProjectileData(
                SPIKE.location(),
                Either.left(
                        new ProjectileData.GenericArrowData(
                                LevelBasedValue.constant(3)
                        )
                ),
                Optional.empty(),
                List.of(),
                List.of(),
                List.of(new ProjectileDamageEffect(
                        context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DSDamageTypes.FOREST_DRAGON_SPIKE),
                        LevelBasedValue.perLevel(2)
                )),
                List.of()
        ));
    }

    private static ResourceKey<ProjectileData> key(final String path) {
        return key(DragonSurvival.res(path));
    }

    public static ResourceKey<ProjectileData> key(final ResourceLocation location) {
        return ResourceKey.create(ProjectileData.REGISTRY, location);
    }
}
