package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Condition;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.LevelBasedResource;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ActionContainer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ManaCost;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.common.particles.LargeFireParticleOption;
import by.dragonsurvivalteam.dragonsurvival.common.particles.SmallFireParticleOption;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.*;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AbilityTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.DragonBreathTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.SelfTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileData;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.Projectiles;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FluidPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.Optional;

public class DragonAbilities {
    @Translation(type = Translation.Type.ABILITY, comments = "Test fireball ability.")
    public static final ResourceKey<DragonAbility> FIRE_BALL_TEST = key("fire_ball_test");

    @Translation(type = Translation.Type.ABILITY, comments = "Spike test.")
    public static final ResourceKey<DragonAbility> SPIKE_TEST = key("spike_test");

    @Translation(type = Translation.Type.ABILITY, comments = "Test ball lightning ability.")
    public static final ResourceKey<DragonAbility> BALL_LIGHTNING = key("ball_lightning_test");

    // TODO: How to actually do this in the new system?
    //  have one generic translation part which applies to all abilities (name, cooldown, duration, level)
    //  people can move them around using the string.format syntax, e.g. -> '%2$' etc. (example for using the second parameter)
    //  the ability description itself needs to be generic since there is no reasonable way to supply what each effect does etc.
    //  (at most you might be able to give the range / radius of each target)
    @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
            "■ Elemental breath: a stream of fire that ignites enemies and blocks. Range depends on age of the dragon.",
            "■ Is able to destroy some blocks. Cannot be used under water, and during rain."
    })
    @Translation(type = Translation.Type.ABILITY, comments = "Nether breath")
    public static final ResourceKey<DragonAbility> NETHER_BREATH = key("nether_breath");

    public static void registerAbilities(final BootstrapContext<DragonAbility> context) {
        context.register(FIRE_BALL_TEST, new DragonAbility(
                new Activation(
                        Activation.Type.ACTIVE_SIMPLE,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(1))),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(2))),
                        Optional.empty()
                ),
                Optional.empty(),
                Optional.empty(),
                List.of(new ActionContainer(
                        new SelfTarget(
                                Either.right(
                                        new AbilityTargeting.EntityTargeting(
                                                Optional.of(Condition.living()),
                                                List.of(new ProjectileEffect(
                                                        context.lookup(ProjectileData.REGISTRY).getOrThrow(Projectiles.FIREBALL),
                                                        LevelBasedValue.constant(1),
                                                        LevelBasedValue.constant(0),
                                                        LevelBasedValue.constant(1)
                                                )),
                                                false
                                        )
                                )
                        ),
                        LevelBasedValue.constant(1)
                )),
                new LevelBasedResource(
                        List.of(new LevelBasedResource.TextureEntry(
                                ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "textures/icons/body_type_central.png"),
                                1
                        ))
                )
        ));

        context.register(SPIKE_TEST, new DragonAbility(
                new Activation(
                        Activation.Type.ACTIVE_SIMPLE,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(1))),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(2))),
                        Optional.empty()
                ),
                Optional.empty(),
                Optional.empty(),
                List.of(new ActionContainer(
                        new SelfTarget(
                                Either.right(
                                        new AbilityTargeting.EntityTargeting(
                                                Optional.of(Condition.living()),
                                                List.of(new ProjectileEffect(
                                                        context.lookup(ProjectileData.REGISTRY).getOrThrow(Projectiles.SPIKE),
                                                        LevelBasedValue.constant(1),
                                                        LevelBasedValue.constant(0),
                                                        LevelBasedValue.constant(1)
                                                )),
                                                false
                                        )
                                )
                        ),
                        LevelBasedValue.constant(1)
                )),
                new LevelBasedResource(
                        List.of(new LevelBasedResource.TextureEntry(
                                ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "textures/icons/body_type_central.png"),
                                1
                        ))
                )
        ));

        context.register(BALL_LIGHTNING, new DragonAbility(
                new Activation(
                        Activation.Type.ACTIVE_SIMPLE,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(1))),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(2))),
                        Optional.empty()
                ),
                Optional.empty(),
                Optional.empty(),
                List.of(new ActionContainer(
                        new SelfTarget(
                                Either.right(
                                        new AbilityTargeting.EntityTargeting(
                                                Optional.of(Condition.living()),
                                                List.of(new ProjectileEffect(
                                                        context.lookup(ProjectileData.REGISTRY).getOrThrow(Projectiles.BALL_LIGHTNING),
                                                        LevelBasedValue.constant(1),
                                                        LevelBasedValue.constant(0),
                                                        LevelBasedValue.constant(1)
                                                )),
                                                false
                                        )
                                )
                        ),
                        LevelBasedValue.constant(1)
                )),
                new LevelBasedResource(
                        List.of(new LevelBasedResource.TextureEntry(
                                ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "textures/icons/body_type_central.png"),
                                1
                        ))
                )
        ));

        context.register(NETHER_BREATH, new DragonAbility(
                new Activation(
                        Activation.Type.ACTIVE_CHANNELED,
                        Optional.empty(),
                        Optional.of(ManaCost.ticking(LevelBasedValue.constant(0.025f))),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(1))),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(2))),
                        Optional.empty()
                ),
                Optional.of(new Upgrade(Upgrade.Type.PASSIVE, 4, LevelBasedValue.lookup(List.of(0f, 10f, 30f, 50f), LevelBasedValue.perLevel(15)))),
                Optional.of(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setFluid(FluidPredicate.Builder.fluid().of(Fluids.WATER))).build()),
                List.of(new ActionContainer(new DragonBreathTarget(Either.right(
                                new AbilityTargeting.EntityTargeting(
                                        Optional.of(Condition.living()),
                                        List.of(
                                                new DamageEffect(
                                                        context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DSDamageTypes.CAVE_DRAGON_BREATH),
                                                        LevelBasedValue.perLevel(3)
                                                ),
                                                new FireEffect(
                                                        LevelBasedValue.perLevel(Functions.secondsToTicks(5))
                                                ),
                                                new PotionEffect(
                                                        HolderSet.direct(DSEffects.BURN),
                                                        LevelBasedValue.constant(0),
                                                        LevelBasedValue.constant(Functions.secondsToTicks(10)),
                                                        LevelBasedValue.constant(1)
                                                )
                                        ),
                                        false
                                )
                        ), LevelBasedValue.constant(1)), LevelBasedValue.constant(10)),
                        new ActionContainer(new SelfTarget(Either.right(
                                new AbilityTargeting.EntityTargeting(
                                        Optional.empty(),
                                        List.of(new BreathParticlesEffect(
                                                0.2f,
                                                0.02f,
                                                new SmallFireParticleOption(37, true),
                                                new LargeFireParticleOption(37, false)
                                        )),
                                        true
                                )
                        )), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_0.png"), 0),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_3.png"), 3),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_4.png"), 4)
                ))
        ));
    }

    public static ResourceKey<DragonAbility> key(final ResourceLocation location) {
        return ResourceKey.create(DragonAbility.REGISTRY, location);
    }

    private static ResourceKey<DragonAbility> key(final String path) {
        return key(DragonSurvival.res(path));
    }
}
