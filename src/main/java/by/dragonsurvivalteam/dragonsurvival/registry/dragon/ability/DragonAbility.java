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

    public List<Component> getInfo(final Player dragon, final DragonAbilityInstance ability) {
        List<Component> info = new ArrayList<>();
        for(ActionContainer action : actions) {
            info.addAll(action.effect().getAllEffectDescriptions(dragon, ability));
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
