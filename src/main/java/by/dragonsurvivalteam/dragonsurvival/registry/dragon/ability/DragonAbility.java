package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.LevelBasedResource;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ActionContainer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects.AbilityBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.*;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AbilityTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AreaTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.DragonBreathTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.block_effects.ProjectileBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileDamageEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectileAreaTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting.ProjectileTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects.ProjectileExplosionEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects.ProjectileWorldEffect;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonAbility(
        boolean isInnate,
        Activation activation,
        Optional<Upgrade> upgrade,
        Optional<EntityPredicate> usageBlocked,
        List<ActionContainer> actions,
        LevelBasedResource icon
) {
    public static final ResourceKey<Registry<DragonAbility>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_abilities"));

    public static final Codec<DragonAbility> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("innate", false).forGetter(DragonAbility::isInnate),
            Activation.codec().fieldOf("activation").forGetter(DragonAbility::activation),
            Upgrade.CODEC.optionalFieldOf("upgrade").forGetter(DragonAbility::upgrade),
            EntityPredicate.CODEC.optionalFieldOf("usage_blocked").forGetter(DragonAbility::usageBlocked),
            ActionContainer.CODEC.listOf().optionalFieldOf("actions", List.of()).forGetter(DragonAbility::actions),
            LevelBasedResource.CODEC.fieldOf("icon").forGetter(DragonAbility::icon)
    ).apply(instance, instance.stable(DragonAbility::new)));

    public static final Codec<Holder<DragonAbility>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonAbility>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    public int getCooldown(int abilityLevel) {
        return activation.cooldown().map(cooldown -> cooldown.calculate(abilityLevel)).orElse(0f).intValue();
    }

    public int getChargeTime(int abilityLevel) {
        return activation.castTime().map(castTime -> castTime.calculate(abilityLevel)).orElse(0f).intValue();
    }

    public int getMaxLevel() {
        return upgrade.map(Upgrade::maximumLevel).orElse(DragonAbilityInstance.MIN_LEVEL);
    }

    public static void validate(RegistryAccess access) {
        StringBuilder validationError = new StringBuilder("The following stages are incorrectly defined:");
        AtomicBoolean areStagesValid = new AtomicBoolean(true);

        ResourceHelper.keys(access, REGISTRY).forEach(key -> {
            //noinspection OptionalGetWithoutIsPresent -> ignore
            Holder.Reference<DragonAbility> ability = ResourceHelper.get(access, key).get();

            if(ability.value().activation().type() != Activation.Type.PASSIVE) {
                if(ability.value().isInnate()) {
                    validationError.append("\n- ").append(key.location()).append(" is marked as innate but is not passive");
                    areStagesValid.set(false);
                }
            }
        });

        if(!areStagesValid.get()) {
            throw new IllegalStateException(validationError.toString());
        }
    }

    private MutableComponent getTargetingComponent(ProjectileTargeting targeting, int level) {
        if(targeting instanceof ProjectileAreaTarget areaTargeting) {
            String targetingTypeString = LangKey.ABILITY_TARGET_ALL_ENTITIES;
            return Component.translatable(LangKey.ABILITY_TO_TARGET_AREA, targetingTypeString, areaTargeting.radius().calculate(level));
        }

        return Component.empty();
    }

    private MutableComponent getTargetingComponent(AbilityTargeting targeting, final Player dragon, final DragonAbilityInstance ability) {
        String targetingTypeString = "";
        if(targeting.target().right().isPresent()) {
            switch(targeting.target().right().get().targetingMode()) {
                case TARGET_ALL -> targetingTypeString = LangKey.ABILITY_TARGET_ALL_ENTITIES;
                case TARGET_ENEMIES -> targetingTypeString = LangKey.ABILITY_TARGET_ENEMIES;
                case TARGET_FRIENDLIES -> targetingTypeString = LangKey.ABILITY_TARGET_ALLIES;
            }
        }

        if(targeting instanceof AreaTarget areaTarget) {
            if(!targetingTypeString.isEmpty()) {
                return Component.translatable(LangKey.ABILITY_TO_TARGET_AREA, Component.translatable(targetingTypeString), areaTarget.radius().calculate(ability.level()));
            } else {
                return Component.translatable(LangKey.ABILITY_AREA, areaTarget.radius().calculate(ability.level()));
            }
        } else if(targeting instanceof DragonBreathTarget breathTarget) {
            if(!targetingTypeString.isEmpty()) {
                return Component.translatable(LangKey.ABILITY_TO_TARGET_CONE, Component.translatable(targetingTypeString), breathTarget.getRange(dragon, ability));
            } else {
                return Component.translatable(LangKey.ABILITY_CONE, breathTarget.getRange(dragon, ability));
            }
        }

        return Component.empty();
    }

    private List<MutableComponent> getProjectileEntityEffectComponents(ProjectileEntityEffect effect, final Player dragon, final DragonAbilityInstance ability, boolean ticking) {
        if (effect instanceof ProjectileDamageEffect damageEffect) {
            return List.of(Component.translatable(LangKey.ABILITY_DAMAGE, Component.translatable(LangKey.ABILITY_PROJECTILE), damageEffect.amount().calculate(ability.level())));
        }

        return List.of();
    }

    private List<MutableComponent> getProjectileWorldEffectComponents(ProjectileWorldEffect effect, final Player dragon, final DragonAbilityInstance ability, boolean ticking) {
        if (effect instanceof ProjectileExplosionEffect explosionEffect) {
            return List.of(Component.translatable(LangKey.ABILITY_EXPLOSION_POWER, Component.translatable(LangKey.ABILITY_PROJECTILE), explosionEffect.explosionPower().calculate(ability.level())));
        }

        return List.of();
    }

    private List<MutableComponent> getProjectileBlockEffectComponents(ProjectileBlockEffect effect, final Player dragon, final DragonAbilityInstance ability, boolean ticking) {
        return List.of();
    }

    private List<MutableComponent> getProjectileTargetingComponents(ProjectileTargeting targeting, final Player dragon, final DragonAbilityInstance ability, boolean ticking) {
        List<MutableComponent> components = new ArrayList<>();
        MutableComponent targetingComponent = getTargetingComponent(targeting, ability.level());
        if(targeting.target().right().isPresent()) {
            for(ProjectileWorldEffect effect : targeting.target().right().get().effects()) {
                List<MutableComponent> effectComponents = getProjectileWorldEffectComponents(effect, dragon, ability, ticking);
                for(MutableComponent effectComponent : effectComponents) {
                    components.add(effectComponent.append(targetingComponent));
                }
            }
        }

        if(targeting.target().left().isPresent()) {
            if(targeting.target().left().get().left().isPresent()) {
                for(ProjectileBlockEffect effect : targeting.target().left().get().left().get().effects()) {
                    List<MutableComponent> effectComponents = getProjectileBlockEffectComponents(effect, dragon, ability, ticking);
                    for(MutableComponent effectComponent : effectComponents) {
                        components.add(effectComponent.append(targetingComponent));
                    }
                }
            } else if(targeting.target().left().get().right().isPresent()) {
                for(ProjectileEntityEffect effect : targeting.target().left().get().right().get().effects()) {
                    List<MutableComponent> effectComponents = getProjectileEntityEffectComponents(effect, dragon, ability, ticking);
                    for(MutableComponent effectComponent : effectComponents) {
                        components.add(effectComponent.append(targetingComponent));
                    }
                }
            }
        }



        return components;
    }

    private List<MutableComponent> getProjectileEffectComponents(ProjectileEffect effect, final Player dragon, final DragonAbilityInstance ability) {
        List<MutableComponent> components = new ArrayList<>();
        for(ProjectileEntityEffect entityHitEffect : effect.projectileData().value().entityHitEffects()) {
            List<MutableComponent> effectComponents = getProjectileEntityEffectComponents(entityHitEffect, dragon, ability, false);
            components.addAll(effectComponents);
        }

        for(ProjectileBlockEffect blockHitEffect : effect.projectileData().value().blockHitEffects()) {
            List<MutableComponent> effectComponents = getProjectileBlockEffectComponents(blockHitEffect, dragon, ability, false);
            components.addAll(effectComponents);
        }

        for(ProjectileTargeting tickingEffect : effect.projectileData().value().tickingEffects()) {
            List<MutableComponent> effectComponents = getProjectileTargetingComponents(tickingEffect, dragon, ability, true);
            components.addAll(effectComponents);
        }

        for(ProjectileTargeting commonEffect : effect.projectileData().value().commonHitEffects()) {
            List<MutableComponent> effectComponents = getProjectileTargetingComponents(commonEffect, dragon, ability, false);
            components.addAll(effectComponents);
        }

        if(effect.projectileData().value().specificProjectileData().left().isPresent()) {
            for(ProjectileTargeting onDestroyEffect : effect.projectileData().value().specificProjectileData().left().get().onDestroyEffects()) {
                List<MutableComponent> effectComponents = getProjectileTargetingComponents(onDestroyEffect, dragon, ability, false);
                components.addAll(effectComponents);
            }
        }

        return components;
    }

    private List<MutableComponent> getAbilityEntityEffectComponents(MutableComponent targetingComponent, AbilityEntityEffect effect, final Player dragon, final DragonAbilityInstance ability) {
        if (effect instanceof DamageEffect damageEffect) {
            return List.of(Component.translatable(LangKey.ABILITY_DAMAGE, " ", damageEffect.amount().calculate(ability.level())));
        }

        if (effect instanceof ProjectileEffect projectileEffect) {
            return getProjectileEffectComponents(projectileEffect, dragon, ability);
        }

        if (effect instanceof PotionEffect potionEffect) {
            List<MutableComponent> components = new ArrayList<>();
            float duration = potionEffect.duration().calculate(ability.level()) / 20.f;
            for (Holder<MobEffect> mobEffect : potionEffect.effects()) {
                MutableComponent name = Component.literal("■ ").append(Component.translatable(LangKey.ABILITY_APPLIES).append(Component.translatable(mobEffect.value().getDescriptionId())).withColor(-219136));

                int amplifier = (int) potionEffect.amplifier().calculate(ability.level());
                if (amplifier > 0) {
                    name.append(Component.literal(Integer.toString(amplifier)).withColor(-219136));
                }

                if (targetingComponent != null) {
                    name = name.append(targetingComponent);
                }

                name.append(Component.translatable(LangKey.ABILITY_EFFECT_DURATION, duration));

                float probability = potionEffect.probability().calculate(ability.level());
                if (probability < 1) {
                    name.append(Component.translatable(LangKey.ABILITY_EFFECT_CHANCE, probability));
                }

                components.add(name);
            }

            return components;
        }

        if (effect instanceof ModifierEffect modifierEffect) {
            List<MutableComponent> components = new ArrayList<>();
            for (ModifierWithDuration modifierWithDuration : modifierEffect.modifiers()) {
                float duration = modifierWithDuration.duration().calculate(ability.level()) / 20.f;
                for (Modifier modifier : modifierWithDuration.modifiers()) {
                    MutableComponent name = Component.literal("■ ").append(Component.translatable(modifier.attribute().value().getDescriptionId())).withColor(-219136);
                    float amount = modifier.amount().calculate(ability.level());
                    String number;
                    if (amount > 0) {
                        number = "+" + amount;
                    } else {
                        number = String.valueOf(amount);
                    }
                    Component value = Component.literal(": ").withColor(-219136).append(Component.literal(number).withStyle(modifier.attribute().value().getStyle(amount > 0)));
                    name = name.append(value);
                    if (targetingComponent != null) {
                        name = name.append(targetingComponent);
                    }

                    if (duration > 0) {
                        name = name.append(Component.translatable(LangKey.ABILITY_EFFECT_DURATION, duration));
                    }

                    components.add(name);
                }
            }

            return components;
        }

        return List.of();
    }

    private List<MutableComponent> getAbilityBlockEffectComponents(MutableComponent targetingComponent, AbilityBlockEffect effect, final Player dragon, final DragonAbilityInstance ability) {
        return List.of();
    }

    private List<MutableComponent> getAbilityTargetingComponents(AbilityTargeting targeting, final Player dragon, final DragonAbilityInstance ability) {
        List<MutableComponent> components = new ArrayList<>();
        MutableComponent targetingComponent = getTargetingComponent(targeting, dragon, ability);
        if(targeting.target().right().isPresent()) {
            for(AbilityEntityEffect effect : targeting.target().right().get().effect()) {
                List<MutableComponent> effectComponents = getAbilityEntityEffectComponents(targetingComponent, effect, dragon, ability);
                components.addAll(effectComponents);
            }
        }

        if(targeting.target().left().isPresent()) {
            for(AbilityBlockEffect effect : targeting.target().left().get().effect()) {
                List<MutableComponent> effectComponents = getAbilityBlockEffectComponents(targetingComponent, effect, dragon, ability);
                components.addAll(effectComponents);
            }
        }

        return components;
    }

    public List<Component> getInfo(final Player dragon, final DragonAbilityInstance ability) {
        List<Component> info = new ArrayList<>();
        for(ActionContainer action : actions) {
            info.addAll(getAbilityTargetingComponents(action.effect(), dragon, ability));
        }

        if(ability.getCastTime() > 0) {
            info.add(Component.translatable(LangKey.ABILITY_CAST_TIME, ability.getCastTime()));
        }
        if(ability.ability().value().getCooldown(ability.level()) > 0) {
            info.add(Component.translatable(LangKey.ABILITY_COOLDOWN, ability.ability().value().getCooldown(ability.level()) / 20));
        }
        if(ability.ability().value().activation().initialManaCost().isPresent()) {
            info.add(Component.translatable(LangKey.ABILITY_INITIAL_MANA_COST, ability.ability().value().activation().initialManaCost().get().calculate(ability.level())));
        }
        if(ability.ability().value().activation().continuousManaCost().isPresent()) {
            info.add(Component.translatable(LangKey.ABILITY_CONTINUOUS_MANA_COST, ability.ability().value().activation().continuousManaCost().get().manaCost().calculate(ability.level())));
        }

        return info;
    }

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }
}
