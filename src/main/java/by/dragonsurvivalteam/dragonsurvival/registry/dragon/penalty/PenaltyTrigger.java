package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public interface PenaltyTrigger {
    ResourceKey<Registry<MapCodec<? extends PenaltyTrigger>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("penalty_triggers"));
    Registry<MapCodec<? extends PenaltyTrigger>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<PenaltyTrigger> CODEC = REGISTRY.byNameCodec().dispatch("effect_type", PenaltyTrigger::codec, Function.identity());

    boolean matches(final ServerPlayer dragon, boolean conditionMatched);
    MapCodec<? extends PenaltyTrigger> codec();
    default String id() { return ""; }

    @SubscribeEvent
    static void register(final NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    @SubscribeEvent
    static void registerEntries(final RegisterEvent event) {
        if (event.getRegistry() == REGISTRY) {
            event.register(REGISTRY_KEY, DragonSurvival.res("supply_trigger"), () -> SupplyTrigger.CODEC);
            event.register(REGISTRY_KEY, DragonSurvival.res("instant_trigger"), () -> InstantTrigger.CODEC);
        }
    }
}
