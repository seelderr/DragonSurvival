package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.common_effects.SummonEntityEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public interface AbilityEntityEffect {
    ResourceKey<Registry<MapCodec<? extends AbilityEntityEffect>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("ability_entity_effects"));
    Registry<MapCodec<? extends AbilityEntityEffect>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<AbilityEntityEffect> CODEC = REGISTRY.byNameCodec().dispatch("effect_type", AbilityEntityEffect::entityCodec, Function.identity());

    void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity);
    MapCodec<? extends AbilityEntityEffect> entityCodec();

    @SubscribeEvent
    static void register(final NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    @SubscribeEvent
    static void registerEntries(final RegisterEvent event) {
        if (event.getRegistry() == REGISTRY) {
            event.register(REGISTRY_KEY, DragonSurvival.res("damage_effect"), () -> DamageEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("modifier_effect"), () -> ModifierEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("potion_effect"), () -> PotionEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("projectile_effect"), () -> ProjectileEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("summon_entity_effect"), () -> SummonEntityEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("damage_modification_effect"), () -> DamageModificationEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("breath_particles_effect"), () -> BreathParticlesEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("fire_effect"), () -> FireEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("harvest_bonus_effect"), () -> HarvestBonusEffect.CODEC);
        }
    }
}
