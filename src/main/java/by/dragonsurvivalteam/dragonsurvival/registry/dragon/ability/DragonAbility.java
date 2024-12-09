package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.LevelBasedResource;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ActionContainer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.*;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileDamageEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects.ProjectileEntityEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonAbility(
        Activation activation,
        Optional<Upgrade> upgrade,
        Optional<EntityPredicate> usageBlocked,
        List<ActionContainer> actions,
        LevelBasedResource icon
) {
    public static final ResourceKey<Registry<DragonAbility>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_abilities"));

    public static final Codec<DragonAbility> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
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
        return upgrade.map(Upgrade::maximumLevel).orElse(0);
    }

    public List<Component> getInfo(final Player dragon, final DragonAbilityInstance ability) {
        float highestRange = -1;
        float highestArea = -1;
        float highestDamage = -1;
        float highestDuration = -1;
        // TODO :: This has proven very hard to extract from the projectile effects
        float highestExplosion = -1;
        List<Component> info = new ArrayList<>();
        for(ActionContainer action : actions) {
            float range = action.effect().getRange(dragon, ability);
            float area = action.effect().getArea(dragon, ability);
            if(range > highestRange) {
                highestRange = range;
            }
            if(area > highestArea) {
                highestArea = area;
            }

            if(action.effect().target().right().isPresent()) {
                for(AbilityEntityEffect effect : action.effect().target().right().get().effect()) {
                    if(effect instanceof DamageEffect damageEffect) {
                        float damage = damageEffect.amount().calculate(ability.level());
                        if(damage > highestDamage) {
                            highestDamage = damage;
                        }
                    }

                    if(effect instanceof ProjectileEffect projectileEffect) {
                        for(ProjectileEntityEffect entityHitEffect : projectileEffect.projectileData().value().entityHitEffects()) {
                            if(entityHitEffect instanceof ProjectileDamageEffect damageEffect) {
                                float damage = damageEffect.amount().calculate(ability.level());
                                if(damage > highestDamage) {
                                    highestDamage = damage;
                                }
                            }
                        }
                    }

                    if(effect instanceof PotionEffect potionEffect) {
                        float duration = potionEffect.duration().calculate(ability.level());
                        if(duration > highestDuration) {
                            highestDuration = duration;
                        }
                    }

                    if(effect instanceof ModifierEffect modifierEffect) {
                        for(ModifierWithDuration modifier : modifierEffect.modifiers()) {
                            float duration = modifier.duration().calculate(ability.level());
                            if(duration > highestDuration) {
                                highestDuration = duration;
                            }
                        }
                    }
                }
            }
        }
        if(highestRange > 0) {
            info.add(Component.translatable(LangKey.ABILITY_RANGE, highestRange));
        }
        if(highestArea > 0) {
            info.add(Component.translatable(LangKey.ABILITY_AOE, highestArea));
        }
        if(highestDamage > 0) {
            info.add(Component.translatable(LangKey.ABILITY_DAMAGE, highestDamage));
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
        if(highestDuration > 0) {
            info.add(Component.translatable(LangKey.ABILITY_EFFECT_DURATION, highestDuration / 20));
        }

        return info;
    }

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }
}
