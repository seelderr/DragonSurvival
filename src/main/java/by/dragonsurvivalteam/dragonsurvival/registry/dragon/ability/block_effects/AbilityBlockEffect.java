package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.common_effects.SummonEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.DamageEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.ModifierEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.PotionEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.ProjectileEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

/*
- convert block
- destroy block
- explode block
- do something with falling block entities to make blocks explode and move around but not actually destroy them through that?
*/
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public interface AbilityBlockEffect {
    ResourceKey<Registry<MapCodec<? extends AbilityBlockEffect>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("ability_block_effects"));
    Registry<MapCodec<? extends AbilityBlockEffect>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<AbilityBlockEffect> CODEC = REGISTRY.byNameCodec().dispatch(AbilityBlockEffect::blockCodec, Function.identity());

    void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final BlockPos position);
    MapCodec<? extends AbilityBlockEffect> blockCodec();

    @SubscribeEvent
    static void register(final NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    @SubscribeEvent
    static void registerEntries(final RegisterEvent event) {
        if (event.getRegistry() == REGISTRY) {
            event.register(REGISTRY_KEY, DragonSurvival.res("bonemeal"), () -> BonemealEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("conversion"), () -> ConversionEffect.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("summon_entity"), () -> SummonEntityEffect.CODEC);
        }
    }
}
