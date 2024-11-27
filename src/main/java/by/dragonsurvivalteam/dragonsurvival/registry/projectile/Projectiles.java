package by.dragonsurvivalteam.dragonsurvival.registry.projectile;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileDamageEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectilePointTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectileTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects.ProjectileExplosionEffect;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;
import java.util.Optional;

public class Projectiles {
    @Translation(type = Translation.Type.PROJECTILE, comments = {"Test fireball projectile."})
    public static final ResourceKey<ProjectileData> FIRE_BALL_TEST = key("fire_ball_projectile_test");

    public static void registerProjectiles(final BootstrapContext<ProjectileData> context) {
        context.register(FIRE_BALL_TEST, new ProjectileData(
                // TODO: How should we be handling translations here for this object?
                //  Since it is a registry I guess we can reference back to its key instead of
                //  passing data like this?
                Component.literal("fireball"),
                Either.right(
                        new ProjectileData.GenericBallData(
                                ParticleTypes.SMOKE,
                                List.of(new ProjectilePointTarget(
                                        new ProjectileTargeting.WorldTargeting(
                                                Optional.empty(),
                                                Optional.empty(),
                                                new ProjectileExplosionEffect(
                                                        context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.FIREBALL),
                                                        LevelBasedValue.constant(10),
                                                        true,
                                                        true,
                                                        true
                                                ),
                                                1)
                                        )
                                ),
                                LevelBasedValue.constant(0),
                                LevelBasedValue.constant(64),
                                LevelBasedValue.constant(1000)
                        )
                ),
                Optional.empty(),
                List.of(),
                List.of(),
                List.of(
                        new ProjectileDamageEffect(
                                context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.FIREBALL),
                                LevelBasedValue.constant(10)
                        )
                ),
                List.of())
        );
    }

    private static ResourceKey<ProjectileData> key(final String path) {
        return key(DragonSurvival.res(path));
    }

    public static ResourceKey<ProjectileData> key(final ResourceLocation location) {
        return ResourceKey.create(ProjectileData.REGISTRY, location);
    }
}
