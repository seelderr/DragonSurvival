package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty.DragonPenalty;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
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
        List<DragonPenalty> penalties
) {
    public static final ResourceKey<Registry<DragonType>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_types"));

    public static final Codec<DragonType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            // No defined stages means all are applicable
            RegistryCodecs.homogeneousList(DragonStage.REGISTRY).optionalFieldOf("stages", HolderSet.empty()).forGetter(DragonType::stages),
            // No defined bodies means all are applicable
            RegistryCodecs.homogeneousList(DragonBody.REGISTRY).optionalFieldOf("bodies", HolderSet.empty()).forGetter(DragonType::bodies),
            MiscCodecs.dragonAbilityCodec().optionalFieldOf("abilities", HolderSet.empty()).forGetter(DragonType::abilities),
            DragonPenalty.CODEC.listOf().optionalFieldOf("penalties", List.of()).forGetter(DragonType::penalties)
    ).apply(instance, instance.stable(DragonType::new)));

    public static final Codec<Holder<DragonType>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }
}
