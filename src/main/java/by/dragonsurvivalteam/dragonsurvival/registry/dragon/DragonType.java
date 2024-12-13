package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.*;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty.DragonPenalty;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonType(
        Optional<Double> startingSize,
        HolderSet<DragonStage> stages,
        HolderSet<DragonBody> bodies,
        HolderSet<DragonAbility> abilities,
        List<Holder<DragonPenalty>> penalties,
        List<Modifier> modifiers,
        List<FoodModifier> foodData,
        MiscDragonTextures miscResources
) implements AttributeModifierSupplier {
    public static final ResourceKey<Registry<DragonType>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_types"));

    public static final Codec<DragonType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("starting_size").forGetter(DragonType::startingSize),
            // No defined stages means all are applicable
            RegistryCodecs.homogeneousList(DragonStage.REGISTRY).optionalFieldOf("stages", HolderSet.empty()).forGetter(DragonType::stages),
            // No defined bodies means all are applicable
            RegistryCodecs.homogeneousList(DragonBody.REGISTRY).optionalFieldOf("bodies", HolderSet.empty()).forGetter(DragonType::bodies),
            RegistryCodecs.homogeneousList(DragonAbility.REGISTRY).optionalFieldOf("abilities", HolderSet.empty()).forGetter(DragonType::abilities),
            DragonPenalty.CODEC.listOf().optionalFieldOf("penalties", List.of()).forGetter(DragonType::penalties),
            Modifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(DragonType::modifiers),
            FoodModifier.CODEC.listOf().optionalFieldOf("food_data", List.of()).forGetter(DragonType::foodData),
            MiscDragonTextures.CODEC.fieldOf("misc_resources").forGetter(DragonType::miscResources)
    ).apply(instance, instance.stable(DragonType::new)));

    public static final Codec<Holder<DragonType>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    public static void validate(@Nullable final HolderLookup.Provider provider) {
        StringBuilder validationError = new StringBuilder("The following types are incorrectly defined:");
        AtomicBoolean areTypesValid = new AtomicBoolean(true);

        ResourceHelper.keys(provider, REGISTRY).forEach(key -> {
            //noinspection OptionalGetWithoutIsPresent -> ignore
            Holder.Reference<DragonType> type = ResourceHelper.get(provider, key, REGISTRY).get();
            if(type.value().stages.size() != 0) {
               if(!DragonStage.stagesHaveContinousSizeRange(type.value().stages, validationError, false)) {
                     areTypesValid.set(false);
               }
            }
        });

        if(!areTypesValid.get()) {
            throw new IllegalStateException(validationError.toString());
        }
    }

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }

    @Override
    public List<Modifier> modifiers() {
        return modifiers;
    }

    @Override
    public ModifierType getModifierType() {
        return ModifierType.DRAGON_TYPE;
    }

    public double getStartingSize(@Nullable final HolderLookup.Provider provider) {
        if(startingSize.isPresent()) {
            return startingSize.get();
        } else if(stages.size() > 0) {
            return DragonStage.getStartingSize(stages);
        } else {
            return DragonStage.getStartingSize(DragonStage.getDefaultStages(provider));
        }
    }
}
