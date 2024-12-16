package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects.AbilityBlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.OnAttackEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.ProjectileEffect;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public interface AbilityTargeting {
    ResourceKey<Registry<MapCodec<? extends AbilityTargeting>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("targeting"));
    Registry<MapCodec<? extends AbilityTargeting>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<AbilityTargeting> CODEC = REGISTRY.byNameCodec().dispatch("target_type", AbilityTargeting::codec, Function.identity());

    enum EntityTargetingMode {
        TARGET_ALL,
        TARGET_ENEMIES,
        TARGET_FRIENDLIES
    }

    record BlockTargeting(Optional<BlockPredicate> targetConditions, List<AbilityBlockEffect> effect) {
        public static final Codec<BlockTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(BlockTargeting::targetConditions),
                AbilityBlockEffect.CODEC.listOf().fieldOf("block_effect").forGetter(BlockTargeting::effect)
        ).apply(instance, BlockTargeting::new));
    }

    record EntityTargeting(Optional<EntityPredicate> targetConditions, List<AbilityEntityEffect> effect, EntityTargetingMode targetingMode) {
        public static final Codec<EntityTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(EntityTargeting::targetConditions),
                AbilityEntityEffect.CODEC.listOf().fieldOf("entity_effect").forGetter(EntityTargeting::effect),
                Codec.STRING.xmap(EntityTargetingMode::valueOf, EntityTargetingMode::name).fieldOf("entity_targeting_mode").forGetter(EntityTargeting::targetingMode)
        ).apply(instance, EntityTargeting::new));
    }

    static <T extends AbilityTargeting> Products.P1<RecordCodecBuilder.Mu<T>, Either<BlockTargeting, EntityTargeting>> codecStart(final RecordCodecBuilder.Instance<T> instance) {
        return instance.group(Codec.either(BlockTargeting.CODEC, EntityTargeting.CODEC).fieldOf("applied_effects").forGetter(AbilityTargeting::target));
    }

    @SubscribeEvent
    static void register(final NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    @SubscribeEvent
    static void registerEntries(final RegisterEvent event) {
        if (event.getRegistry() == REGISTRY) {
            event.register(REGISTRY_KEY, DragonSurvival.res("area"), () -> AreaTarget.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("dragon_breath"), () -> DragonBreathTarget.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("looking_at"), () -> LookingAtTarget.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("self"), () -> SelfTarget.CODEC);
        }
    }

    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    default boolean isEntityRelevant(final ServerPlayer dragon, final EntityTargeting targeting, final Entity entity) {
        if(targeting.targetingMode == EntityTargetingMode.TARGET_ALL) {
            return true;
        }

        if (targeting.targetingMode == EntityTargetingMode.TARGET_ENEMIES && (dragon == entity || isFriendly(dragon, entity))) {
            return false;
        }

        if(targeting.targetingMode == EntityTargetingMode.TARGET_FRIENDLIES && !isFriendly(dragon, entity)) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    private boolean isFriendly(final ServerPlayer dragon, final Entity entity) {
        if (entity instanceof Player otherPlayer && !dragon.canHarmPlayer(otherPlayer)) {
            return true;
        }

        if (entity instanceof TamableAnimal tamable && tamable.getOwner() instanceof Player otherPlayer && (dragon == otherPlayer || !dragon.canHarmPlayer(otherPlayer))) {
            return true;
        }

        // TODO :: 'canHarmPlayer' returns true if friendly fire is enabled - do we want this behaviour?
        return false;
    }

    default List<MutableComponent> getAllEffectDescriptions(final Player dragon, final DragonAbilityInstance abilityInstance) {
        List<MutableComponent> descriptions = new ArrayList<>();
        MutableComponent targetDescription = getDescription(dragon, abilityInstance);
        if (target().right().isPresent()) {
            target().right().get().effect().forEach(effect -> {
                List<MutableComponent> abilityEffectDescriptions = effect.getDescription(dragon, abilityInstance);
                if(!effect.getDescription(dragon, abilityInstance).isEmpty()) {
                    if(!effect.shouldAppendSelfTargetingToDescription() && this instanceof SelfTarget) {
                        // Special case where we don't want to append the "self target" for certain effects
                        descriptions.addAll(effect.getDescription(dragon, abilityInstance));
                    } else {
                        descriptions.addAll(abilityEffectDescriptions.stream().map(abilityEffectDescription -> abilityEffectDescription.append(targetDescription)).toList());
                    }
                }
            });
        } else if (target().left().isPresent()) {
            target().left().get().effect().forEach(effect -> {
                List<MutableComponent> abilityEffectDescriptions = effect.getDescription(dragon, abilityInstance);
                if(!effect.getDescription(dragon, abilityInstance).isEmpty()) {
                    descriptions.addAll(abilityEffectDescriptions.stream().map(abilityEffectDescription -> abilityEffectDescription.append(targetDescription)).toList());
                }
            });
        }

        return descriptions;
    }


    MutableComponent getDescription(final Player dragon, final DragonAbilityInstance ability);
    void apply(final ServerPlayer dragon, final DragonAbilityInstance ability);
    default void remove(final ServerPlayer dragon, final DragonAbilityInstance ability) {};
    MapCodec<? extends AbilityTargeting> codec();
    Either<BlockTargeting, EntityTargeting> target();
}
