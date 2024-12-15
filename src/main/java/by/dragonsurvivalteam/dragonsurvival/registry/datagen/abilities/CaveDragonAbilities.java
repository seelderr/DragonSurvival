package by.dragonsurvivalteam.dragonsurvival.registry.datagen.abilities;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.*;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ActionContainer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ManaCost;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation.AnimationLayer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation.SimpleAbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.common.particles.LargeFireParticleOption;
import by.dragonsurvivalteam.dragonsurvival.common.particles.SmallFireParticleOption;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSSounds;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSBlockTags;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects.FireEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.*;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AbilityTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AreaTarget;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.Optional;

public class CaveDragonAbilities {
    // --- Active --- //

    @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "■ Ranged attack: shoots out a fireball that §cexplodes§r and sets the area on fire.")
    @Translation(type = Translation.Type.ABILITY, comments = "Fireball")
    public static final ResourceKey<DragonAbility> FIRE_BALL = DragonAbilities.key("fire_ball");

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
    public static final ResourceKey<DragonAbility> NETHER_BREATH = DragonAbilities.key("nether_breath");

    @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "■ Personal buff: makes lava more §2transparent§r while active.")
    @Translation(type = Translation.Type.ABILITY, comments = "Lava Vision")
    public static final ResourceKey<DragonAbility> LAVA_VISION = DragonAbilities.key("lava_vision");

    @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Grants additional armor points to all entities in an area around the dragon.")
    @Translation(type = Translation.Type.ABILITY, comments = "Sturdy Skin") // TODO :: strong leather, tough skin or sturdy skin?
    public static final ResourceKey<DragonAbility> TOUGH_SKIN = DragonAbilities.key("tough_skin");

    // TODO :: These should be interpreted from the ResourceLocation instead of manually defined
    @Translation(type = Translation.Type.MODIFIER, comments = "Cave Athletics", key = "cave_athletics")
    public static final ResourceLocation CAVE_ATHLETICS_MODIFIER = DragonSurvival.res("cave_athletics");

    // TODO :: These should be interpreted from the ResourceLocation instead of manually defined
    @Translation(type = Translation.Type.MODIFIER, comments = "Sturdy Skin", key = "sturdy_skin")
    public static final ResourceLocation STURDY_SKIN_MODIFIER = DragonSurvival.res("sturdy_skin");

    // --- Passive --- //
    @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "■ Standing on stone surfaces will increase your movement speed.")
    @Translation(type = Translation.Type.ABILITY, comments = "Cave Athletics")
    public static final ResourceKey<DragonAbility> CAVE_ATHLETICS = DragonAbilities.key("cave_athletics");

    @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
            "■ Your target has a chance to receive the §c«Burned»§r effect from your attacks.\n",
            "The effect deals damage when the target moves.\n",
            "The faster the movement, the more damage is done.\n",
            "■ Creatures with fire resistance are immune to this effect."
    })
    @Translation(type = Translation.Type.ABILITY, comments = "Burn")
    public static final ResourceKey<DragonAbility> BURN = DragonAbilities.key("burn");

    @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
            "■ Upgrading this ability increases your maximum mana pool. Cave dragon mana is restored by standing on hot blocks.\n",
    })
    @Translation(type = Translation.Type.ABILITY, comments = "Cave Magic")
    public static final ResourceKey<DragonAbility> CAVE_MAGIC = DragonAbilities.key("cave_magic");


    @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
            "■ You are resistant to Rain, snow and snowfall for: §2%s§rs\n",
            "■ Water, potions and snowballs are still dangerous"
    })
    @Translation(type = Translation.Type.ABILITY, comments = "Contrast Shower")
    public static final ResourceKey<DragonAbility> CONTRAST_SHOWER = DragonAbilities.key("contrast_shower");

    public static void registerAbilities(final BootstrapContext<DragonAbility> context) {
        registerActiveAbilities(context);
        registerPassiveAbilities(context);
    }

    private static void registerActiveAbilities(final BootstrapContext<DragonAbility> context) {
        context.register(FIRE_BALL, new DragonAbility(
                false,
                new Activation(
                        Activation.Type.ACTIVE_SIMPLE,
                        Optional.of(LevelBasedValue.constant(1)),
                        Optional.empty(),
                        Optional.of(LevelBasedValue.constant(Functions.secondsToTicks(2))),
                        Optional.of(LevelBasedValue.constant(Functions.secondsToTicks(7))),
                        Optional.of(new Activation.Sound(
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.of(SoundEvents.FIRECHARGE_USE)
                        )),
                        Optional.of(new Activation.Animations(
                                Optional.of(Either.right(new SimpleAbilityAnimation("breath", AnimationLayer.BREATH, 5, false, false))),
                                Optional.empty(),
                                Optional.empty()
                        ))
                ),
                Optional.of(new Upgrade(Upgrade.Type.PASSIVE, 4, LevelBasedValue.lookup(List.of(0f, 20f, 40f, 45f), LevelBasedValue.perLevel(15)))),
                Optional.of(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setFluid(FluidPredicate.Builder.fluid().of(Fluids.WATER))).build()),
                List.of(new ActionContainer(new SelfTarget(Either.right(
                        new AbilityTargeting.EntityTargeting(
                                Optional.of(Condition.living()),
                                List.of(new ProjectileEffect(
                                        context.lookup(ProjectileData.REGISTRY).getOrThrow(Projectiles.FIREBALL),
                                        TargetDirection.lookingAt(),
                                        LevelBasedValue.constant(1),
                                        LevelBasedValue.constant(0),
                                        LevelBasedValue.constant(1)
                                )),
                                AbilityTargeting.EntityTargetingMode.TARGET_ALL
                        )
                ), false), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/fireball_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/fireball_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/fireball_3.png"), 3),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/fireball_4.png"), 4)
                ))
        ));

        context.register(NETHER_BREATH, new DragonAbility(
                false,
                new Activation(
                        Activation.Type.ACTIVE_CHANNELED,
                        Optional.empty(),
                        Optional.of(ManaCost.ticking(LevelBasedValue.constant(0.025f))),
                        Optional.of(LevelBasedValue.constant(Functions.secondsToTicks(1))),
                        Optional.of(LevelBasedValue.constant(Functions.secondsToTicks(2))),
                        Optional.of(new Activation.Sound(
                                Optional.of(DSSounds.FIRE_BREATH_START.get()),
                                Optional.empty(),
                                Optional.of(DSSounds.FIRE_BREATH_LOOP.get()),
                                Optional.of(DSSounds.FIRE_BREATH_END.get())
                        )),
                        Optional.of(new Activation.Animations(
                                Optional.empty(),
                                Optional.of(new SimpleAbilityAnimation("breath", AnimationLayer.BREATH, 5, false, false)),
                                Optional.empty()
                        ))
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
                                                new IgniteEffect(
                                                        LevelBasedValue.perLevel(Functions.secondsToTicks(5))
                                                ),
                                                new PotionEffect(
                                                        HolderSet.direct(DSEffects.BURN),
                                                        LevelBasedValue.constant(0),
                                                        LevelBasedValue.constant(Functions.secondsToTicks(10)),
                                                        LevelBasedValue.constant(1)
                                                )
                                        ),
                                        AbilityTargeting.EntityTargetingMode.TARGET_ENEMIES
                                )
                        ), LevelBasedValue.constant(1)), LevelBasedValue.constant(10)),
                        new ActionContainer(new DragonBreathTarget(Either.left(
                                new AbilityTargeting.BlockTargeting(
                                        Optional.empty(),
                                        List.of(new FireEffect(
                                                LevelBasedValue.constant(0.05f)
                                        )))
                        ), LevelBasedValue.constant(1)), LevelBasedValue.constant(1)),
                        new ActionContainer(new SelfTarget(Either.right(
                                new AbilityTargeting.EntityTargeting(
                                        Optional.empty(),
                                        List.of(new BreathParticlesEffect(
                                                0.2f,
                                                0.02f,
                                                new SmallFireParticleOption(37, true),
                                                new LargeFireParticleOption(37, false)
                                        )),
                                        AbilityTargeting.EntityTargetingMode.TARGET_ALL
                                )
                        ), false), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_3.png"), 3),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/nether_breath_4.png"), 4)
                ))
        ));

        context.register(LAVA_VISION, new DragonAbility(
                false,
                new Activation(
                        Activation.Type.ACTIVE_SIMPLE,
                        Optional.of(LevelBasedValue.constant(1)),
                        Optional.empty(),
                        Optional.of(LevelBasedValue.constant(Functions.secondsToTicks(1))),
                        Optional.of(LevelBasedValue.constant(Functions.secondsToTicks(30))),
                        Optional.of(new Activation.Sound(
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.of(SoundEvents.UI_TOAST_IN)
                        )),
                        Optional.of(new Activation.Animations(
                                Optional.of(Either.right(new SimpleAbilityAnimation("cast_self_buff", AnimationLayer.BASE, 2, true, false))),
                                Optional.empty(),
                                Optional.of(new SimpleAbilityAnimation("self_buff", AnimationLayer.BASE, 0, true, false))
                        ))
                ),
                Optional.of(new Upgrade(Upgrade.Type.PASSIVE, 4, LevelBasedValue.lookup(List.of(0f, 25f, 45f, 60f), LevelBasedValue.perLevel(15)))),
                Optional.empty(),
                List.of(new ActionContainer(new SelfTarget(Either.right(
                        new AbilityTargeting.EntityTargeting(
                                Optional.empty(),
                                List.of(new PotionEffect(
                                        HolderSet.direct(DSEffects.LAVA_VISION),
                                        LevelBasedValue.constant(0),
                                        LevelBasedValue.perLevel(Functions.secondsToTicks(30)),
                                        LevelBasedValue.constant(1)
                                )),
                                AbilityTargeting.EntityTargetingMode.TARGET_FRIENDLIES
                        )
                ), false), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/lava_vision_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/lava_vision_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/lava_vision_3.png"), 3),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/lava_vision_4.png"), 4)
                ))
        ));

        context.register(TOUGH_SKIN, new DragonAbility(
                false,
                new Activation(
                        Activation.Type.ACTIVE_SIMPLE,
                        Optional.of(LevelBasedValue.constant(1)),
                        Optional.empty(),
                        Optional.of(LevelBasedValue.constant(Functions.secondsToTicks(1))),
                        Optional.of(LevelBasedValue.constant(Functions.secondsToTicks(30))),
                        Optional.of(new Activation.Sound(
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.of(SoundEvents.UI_TOAST_IN)
                        )),
                        Optional.of(new Activation.Animations(
                                Optional.of(Either.right(new SimpleAbilityAnimation("cast_mass_buff", AnimationLayer.BASE, 2, true, true))),
                                Optional.empty(),
                                Optional.of(new SimpleAbilityAnimation("mass_buff", AnimationLayer.BASE, 0, true, true))
                        ))
                ),
                Optional.of(new Upgrade(Upgrade.Type.PASSIVE, 3, LevelBasedValue.lookup(List.of(0f, 15f, 35f), LevelBasedValue.perLevel(15)))),
                Optional.empty(),
                List.of(new ActionContainer(new AreaTarget(Either.right(
                        new AbilityTargeting.EntityTargeting(
                                Optional.empty(),
                                ModifierEffect.single(new ModifierWithDuration(
                                        STURDY_SKIN_MODIFIER,
                                        DragonSurvival.res("textures/modifiers/strong_leather.png"),
                                        List.of(new Modifier(Attributes.ARMOR, LevelBasedValue.constant(3), AttributeModifier.Operation.ADD_VALUE, Optional.empty())),
                                        LevelBasedValue.perLevel(Functions.secondsToTicks(60)),
                                        false
                                )),
                                AbilityTargeting.EntityTargetingMode.TARGET_FRIENDLIES
                        )),
                        LevelBasedValue.constant(5)
                ), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/strong_leather_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/strong_leather_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/strong_leather_3.png"), 3)
                ))
        ));
    }

    private static void registerPassiveAbilities(final BootstrapContext<DragonAbility> context) {
        context.register(CAVE_ATHLETICS, new DragonAbility(
                false,
                Activation.passive(),
                Optional.of(new Upgrade(Upgrade.Type.MANUAL, 5, LevelBasedValue.perLevel(15))), // FIXME :: not the actual values
                Optional.empty(),
                List.of(new ActionContainer(new SelfTarget(Either.right(
                        new AbilityTargeting.EntityTargeting(
                                Optional.of(Condition.onBlock(DSBlockTags.SPEEDS_UP_CAVE_DRAGON)),
                                ModifierEffect.single(new ModifierWithDuration(
                                        CAVE_ATHLETICS_MODIFIER,
                                        /* FIXME */ DragonSurvival.res("textures/modifiers/strong_leather.png"),
                                        // FIXME :: not the final value
                                        List.of(new Modifier(Attributes.MOVEMENT_SPEED, LevelBasedValue.perLevel(0.02f), AttributeModifier.Operation.ADD_VALUE, Optional.empty())),
                                        LevelBasedValue.constant(ModifierWithDuration.INFINITE_DURATION),
                                        false
                                )), AbilityTargeting.EntityTargetingMode.TARGET_FRIENDLIES)), true), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_athletics_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_athletics_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_athletics_3.png"), 3),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_athletics_4.png"), 4),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_athletics_5.png"), 5)
                ))
        ));

        context.register(BURN, new DragonAbility(
                false,
                Activation.passive(),
                Optional.of(new Upgrade(Upgrade.Type.MANUAL, 4, LevelBasedValue.perLevel(15))),
                Optional.empty(),
                List.of(new ActionContainer(new SelfTarget(Either.right(
                        new AbilityTargeting.EntityTargeting(
                                Optional.empty(),
                                List.of(new OnAttackEffect(
                                        HolderSet.direct(DSEffects.BURN),
                                        LevelBasedValue.constant(0),
                                        LevelBasedValue.perLevel(Functions.secondsToTicks(5)),
                                        LevelBasedValue.perLevel(0.15f)
                                )),
                                AbilityTargeting.EntityTargetingMode.TARGET_ALL
                        )
                ), false), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/burn_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/burn_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/burn_3.png"), 3),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/burn_4.png"), 4)
                ))
        ));

        context.register(CAVE_MAGIC, new DragonAbility(
                false,
                Activation.passive(),
                Optional.of(new Upgrade(Upgrade.Type.MANUAL, 10, LevelBasedValue.perLevel(15))),
                Optional.empty(),
                List.of(new ActionContainer(new SelfTarget(Either.right(
                        new AbilityTargeting.EntityTargeting(
                                Optional.empty(),
                                List.of(new ModifierEffect(
                                        List.of(
                                                new ModifierWithDuration(
                                                        DragonSurvival.res("cave_magic"),
                                                        ModifierWithDuration.DEFAULT_MODIFIER_ICON,
                                                        List.of(new Modifier(DSAttributes.MANA, LevelBasedValue.perLevel(1), AttributeModifier.Operation.ADD_VALUE, Optional.empty())),
                                                        LevelBasedValue.constant(ModifierWithDuration.INFINITE_DURATION),
                                                        true
                                                )
                                        )
                                )),
                                AbilityTargeting.EntityTargetingMode.TARGET_FRIENDLIES
                        )
                ), false), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_3.png"), 3),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_4.png"), 4),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_5.png"), 5),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_6.png"), 6),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_7.png"), 7),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_8.png"), 8),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_9.png"), 9),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/cave_magic_10.png"), 10)
                ))
        ));

        context.register(CONTRAST_SHOWER, new DragonAbility(
                false,
                Activation.passive(),
                Optional.of(new Upgrade(Upgrade.Type.MANUAL, 5, LevelBasedValue.perLevel(15))),
                Optional.empty(),
                List.of(new ActionContainer(new SelfTarget(Either.right(
                        new AbilityTargeting.EntityTargeting(
                                Optional.empty(),
                                List.of(new ModifierEffect(
                                        List.of(
                                                new ModifierWithDuration(
                                                        DragonSurvival.res("contrast_shower"),
                                                        ModifierWithDuration.DEFAULT_MODIFIER_ICON,
                                                        List.of(new Modifier(DSAttributes.PENALTY_RESISTANCE_TIME, LevelBasedValue.perLevel(30), AttributeModifier.Operation.ADD_VALUE, Optional.empty())),
                                                        LevelBasedValue.perLevel(Functions.secondsToTicks(5)),
                                                        true
                                                )
                                        )
                                )),
                                AbilityTargeting.EntityTargetingMode.TARGET_FRIENDLIES
                        )
                ), false), LevelBasedValue.constant(1))),
                new LevelBasedResource(List.of(
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/contrast_shower_1.png"), 1),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/contrast_shower_2.png"), 2),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/contrast_shower_3.png"), 3),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/contrast_shower_4.png"), 4),
                        new LevelBasedResource.TextureEntry(DragonSurvival.res("textures/skills/cave/contrast_shower_5.png"), 5)
                ))
        ));
    }
}
