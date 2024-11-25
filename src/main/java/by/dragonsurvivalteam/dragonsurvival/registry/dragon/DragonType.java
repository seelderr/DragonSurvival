package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Penalty;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonType(
        HolderSet<DragonStage> stages,
        HolderSet<DragonBody> bodies,
        HolderSet<DragonAbility> abilities,
        List<Penalty> penalties
) {
    public static final ResourceKey<Registry<DragonType>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_types"));

    public static final Codec<DragonType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(DragonStage.REGISTRY).fieldOf("stages").forGetter(DragonType::stages),
            RegistryCodecs.homogeneousList(DragonBody.REGISTRY).fieldOf("bodies").forGetter(DragonType::bodies),
            MiscCodecs.dragonAbilityCodec().optionalFieldOf("abilities", HolderSet.empty()).forGetter(DragonType::abilities),
            Penalty.CODEC.listOf().optionalFieldOf("penalties", List.of()).forGetter(DragonType::penalties)
    ).apply(instance, instance.stable(DragonType::new)));

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }
}
