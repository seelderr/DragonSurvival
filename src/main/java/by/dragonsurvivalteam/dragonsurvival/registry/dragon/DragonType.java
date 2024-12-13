package by.dragonsurvivalteam.dragonsurvival.registry.dragon;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.DietEntry;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscDragonTextures;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DataReloadHandler;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DragonType implements AttributeModifierSupplier {
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
            DietEntry.CODEC.listOf().optionalFieldOf("diet", List.of()).forGetter(DragonType::diet),
            MiscDragonTextures.CODEC.fieldOf("misc_resources").forGetter(DragonType::miscResources)
    ).apply(instance, instance.stable(DragonType::new)));

    public static final Codec<Holder<DragonType>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    private final HolderSet<DragonStage> stages;
    private final HolderSet<DragonBody> bodies;
    private final HolderSet<DragonAbility> abilities;
    private final List<Holder<DragonPenalty>> penalties;
    private final List<Modifier> modifiers;
    private final List<DietEntry> dietEntries;
    private final MiscDragonTextures miscResources;

    // TODO :: not entirely sure how many instances of this would be present
    //  resource key is a singleton (due to its internal VALUES map) but this object is probably not?
    //  maybe have a global static list to reduce memory overhead? would the memory amount even be relevant enough to care about it?
    //  that might run into concurrency issues on single player though (meaning we have to use concurrent hash map etc.)
    private @Nullable Map<ResourceLocation, FoodProperties> diet;
    private long lastDietUpdate;

    public DragonType(final HolderSet<DragonStage> stages, final HolderSet<DragonBody> bodies, final HolderSet<DragonAbility> abilities, final List<Holder<DragonPenalty>> penalties, final List<Modifier> modifiers, final List<DietEntry> dietEntries, final MiscDragonTextures miscResources) {
        this.stages = stages;
        this.bodies = bodies;
        this.abilities = abilities;
        this.penalties = penalties;
        this.modifiers = modifiers;
        this.dietEntries = dietEntries;
        this.miscResources = miscResources;
    }

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

    public @Nullable FoodProperties getDiet(final Item item) {
        if (diet == null || lastDietUpdate < DataReloadHandler.lastReload) {
            lastDietUpdate = System.currentTimeMillis();
            diet = DietEntry.map(dietEntries);
        }

        //noinspection deprecation,DataFlowIssue -> ignore deprecated / key is present
        return diet.get(item.builtInRegistryHolder().getKey().location());
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

    @Override
    public List<Modifier> modifiers() {
        return modifiers;
    }

    public HolderSet<DragonStage> stages() {
        return stages;
    }

    public HolderSet<DragonBody> bodies() {
        return bodies;
    }

    public HolderSet<DragonAbility> abilities() {
        return abilities;
    }

    public List<Holder<DragonPenalty>> penalties() {
        return penalties;
    }

    public List<DietEntry> diet() {
        return dietEntries;
    }

    public MiscDragonTextures miscResources() {
        return miscResources;
    }
}
