package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
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
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

/*
- convert block
- destroy block
- explode block
- do something with falling block entities to make blocks explode and move around but not actually destroy them through that?
*/
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public interface BlockEffect {
    ResourceKey<Registry<MapCodec<? extends BlockEffect>>> REGISTRY_KEY = ResourceKey.createRegistryKey(DragonSurvival.res("block_effects"));
    Registry<MapCodec<? extends BlockEffect>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();

    Codec<BlockEffect> CODEC = REGISTRY.byNameCodec().dispatch(BlockEffect::blockCodec, Function.identity());

    void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final BlockPos position);
    MapCodec<? extends BlockEffect> blockCodec();

    @SubscribeEvent
    static void register(final NewRegistryEvent event) {
        event.register(REGISTRY);
    }
}
