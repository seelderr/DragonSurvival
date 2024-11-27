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
public interface AbilityEntityEffect { // TODO :: split into entity and block effects?
    ResourceKey<Registry<MapCodec<? extends AbilityEntityEffect>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("ability_entity_effects"));
    Registry<MapCodec<? extends AbilityEntityEffect>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<AbilityEntityEffect> CODEC = REGISTRY.byNameCodec().dispatch(AbilityEntityEffect::entityCodec, Function.identity());

    void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity);
    MapCodec<? extends AbilityEntityEffect> entityCodec();

    @SubscribeEvent
    static void register(final NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    @SubscribeEvent
    static void registerEntries(final RegisterEvent event) {
        if (event.getRegistry() == REGISTRY) {
            event.register(REGISTRY_KEY, DragonSurvival.res("damage"), () -> DamageEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("modifier"), () -> ModifierEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("potion"), () -> PotionEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("projectile"), () -> ProjectileEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("summon_entity"), () -> SummonEntityEffect.CODEC);
        }
    }
}
