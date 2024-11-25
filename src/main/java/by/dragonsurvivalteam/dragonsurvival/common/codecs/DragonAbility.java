package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.AttributeModifierSupplier;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonAbility(
        List<Modifier> passiveModifiers,
        Optional<LevelBasedValue> upgradeCost,
        AbilitySlot slot,
        Optional<EntityPredicate> usageConditions,
        Optional<ActiveAbility> activeAbility,
        List<PenaltyEffect> penaltyEffects,
        Component description) implements AttributeModifierSupplier {

    public enum AbilityType {
        PASSIVE,
        ACTIVE,
        INNATE
    }

    public record AbilitySlot(int pos, AbilityType type) {
        public static final Codec<AbilitySlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("pos").forGetter(AbilitySlot::pos),
                Codec.STRING.xmap(AbilityType::valueOf, AbilityType::name).fieldOf("type").forGetter(AbilitySlot::type)
        ).apply(instance, instance.stable(AbilitySlot::new)));
    }

    enum ActivationType {
        CHANNELED,
        CHARGE,
        TOGGLE,
        INSTANT
    }

    public record ActivationMode(
            ActivationType activationType,
            Optional<Integer> chargeTime,
            Optional<Double> breathRangeMultiplier) {
        public static final Codec<ActivationMode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.xmap(ActivationType::valueOf, ActivationType::name).fieldOf("activation_type").forGetter(ActivationMode::activationType),
                Codec.INT.optionalFieldOf("charge_time").forGetter(ActivationMode::chargeTime),
                // Dragons already have a BREATH_RANGE attribute, but this lets you modify it for specific abilities
                Codec.DOUBLE.optionalFieldOf("breath_range_multiplier").forGetter(ActivationMode::breathRangeMultiplier)
        ).apply(instance, instance.stable(ActivationMode::new)));
    }

    // TODO: How to handle block destruction/interactions for breath abilities? (e.g. cave dragon setting things on fire and igniting TNT)
    // TODO: How do we properly display the scaling of the abilities with player level as we do currently in the abilities UI?
    public record ActiveAbility(
            Optional<EntityPredicate> targetConditions,
            ActivationMode activationMode,
            int manaCost,
            // TODO: Store this cooldown somehow in a map on the dragon player?
            int skillCooldown,
            // TODO: For breath abilities, should we spawn particles through this entity effect or handle it ourselves?
            EnchantmentEntityEffect effect,
            ResourceLocation icon) {
        public static final Codec<ActiveAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(ActiveAbility::targetConditions),
                ActivationMode.CODEC.fieldOf("activation_mode").forGetter(ActiveAbility::activationMode),
                Codec.INT.fieldOf("mana_cost").forGetter(ActiveAbility::manaCost),
                Codec.INT.fieldOf("skill_cooldown").forGetter(ActiveAbility::skillCooldown),
                EnchantmentEntityEffect.CODEC.fieldOf("effect").forGetter(ActiveAbility::effect),
                ResourceLocation.CODEC.fieldOf("icon").forGetter(ActiveAbility::icon)
        ).apply(instance, instance.stable(ActiveAbility::new)));
    }

    public record PenaltyEffect(
            EntityPredicate penaltyConditions,
            Modifier modifierPenalties,
            EnchantmentEntityEffect effectPenalties,
            // TODO: Also need to store on the player
            int durationToTrigger,
            // Ticks per each penalty effect activation
            int triggerRate,
            // TODO: Will need to break up the sprite sheet for each penalty ability to show the correct icons
            // No resource sprites = this penalty triggers instantly (cave dragon in water)
            Optional<ResourceLocation> resourceSprites) {
        public static final Codec<PenaltyEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.fieldOf("penalty_conditions").forGetter(PenaltyEffect::penaltyConditions),
                Modifier.CODEC.fieldOf("modifier_penalties").forGetter(PenaltyEffect::modifierPenalties),
                EnchantmentEntityEffect.CODEC.fieldOf("effect_penalties").forGetter(PenaltyEffect::effectPenalties),
                Codec.INT.fieldOf("duration_to_trigger").forGetter(PenaltyEffect::durationToTrigger),
                Codec.INT.fieldOf("trigger_rate").forGetter(PenaltyEffect::triggerRate),
                ResourceLocation.CODEC.optionalFieldOf("resource_sprites").forGetter(PenaltyEffect::resourceSprites)
        ).apply(instance, instance.stable(PenaltyEffect::new)));
    }

    public static final ResourceKey<Registry<DragonAbility>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_abilities"));

    public static final Codec<DragonAbility> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            // TODO: We can remove the innate claw abilities from the DragonStages class and just add them here instead
            Modifier.CODEC.listOf().optionalFieldOf("passive_modifiers", List.of()).forGetter(DragonAbility::passiveModifiers),
            LevelBasedValue.CODEC.optionalFieldOf("upgrade_cost").forGetter(DragonAbility::upgradeCost),
            AbilitySlot.CODEC.fieldOf("slot").forGetter(DragonAbility::slot),
            EntityPredicate.CODEC.optionalFieldOf("usage_conditions").forGetter(DragonAbility::usageConditions),
            ActiveAbility.CODEC.optionalFieldOf("active_ability").forGetter(DragonAbility::activeAbility),
            PenaltyEffect.CODEC.listOf().optionalFieldOf("penalty_effects", List.of()).forGetter(DragonAbility::penaltyEffects),
            // TODO: How do we handle descriptions that are fed various values from the ability itself?
            ComponentSerialization.CODEC.fieldOf("description").forGetter(DragonAbility::description)
    ).apply(instance, instance.stable(DragonAbility::new)));

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }

    public static void update(@Nullable final HolderLookup.Provider provider) {
        validate(provider);
    }

    private static void validate(@Nullable final HolderLookup.Provider provider) {
        StringBuilder nextAbilityCheck = new StringBuilder("The following abilities are incorrectly defined:");
        AtomicBoolean areAbilitiesValid = new AtomicBoolean(true);

        ResourceHelper.keys(provider, REGISTRY).forEach(key -> {
            //noinspection OptionalGetWithoutIsPresent -> ignore
            Holder.Reference<DragonAbility> ability = ResourceHelper.get(provider, key, REGISTRY).get();

            if(ability.value().slot.type != AbilityType.ACTIVE && ability.value().activeAbility.isPresent()) {
                nextAbilityCheck.append("\n- Ability [").append(key.location()).append("] has an active effect but is not defined as active");
                areAbilitiesValid.set(false);
            }

            if(ability.value().slot.type == AbilityType.PASSIVE && ability.value().passiveModifiers.isEmpty()) {
                nextAbilityCheck.append("\n- Ability [").append(key.location()).append("] has no passive modifiers but is defined as passive");
                areAbilitiesValid.set(false);
            }

            if(ability.value().slot.type == AbilityType.PASSIVE && ability.value().upgradeCost.isPresent()) {
                nextAbilityCheck.append("\n- Ability [").append(key.location()).append("] has an upgrade cost but is defined as passive");
                areAbilitiesValid.set(false);
            }

            if(ability.value().slot.type != AbilityType.INNATE && ability.value().penaltyEffects.isEmpty()) {
                nextAbilityCheck.append("\n- Ability [").append(key.location()).append("] has a penalty effect but is not defined as innate");
                areAbilitiesValid.set(false);
            }
        });

        if (!areAbilitiesValid.get()) {
            throw new IllegalStateException(nextAbilityCheck.toString());
        }
    }
}
