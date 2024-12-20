package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates.EyeInFluidPredicate;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates.WeatherPredicate;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAddPenaltySupply;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncRemovePenaltySupply;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.PenaltySupply;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonPenalty(ResourceLocation icon, List<Condition> conditions, PenaltyEffect effect, PenaltyTrigger trigger) {
    public static final Codec<DragonPenalty> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(DragonPenalty::icon),
            Condition.CODEC.listOf().fieldOf("conditions").forGetter(DragonPenalty::conditions),
            PenaltyEffect.CODEC.fieldOf("effect").forGetter(DragonPenalty::effect),
            PenaltyTrigger.CODEC.fieldOf("trigger").forGetter(DragonPenalty::trigger)
    ).apply(instance, DragonPenalty::new));

    public static final ResourceKey<Registry<DragonPenalty>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_penalties"));
    public static final Codec<Holder<DragonPenalty>> CODEC = RegistryFixedCodec.create(REGISTRY);

    public record Condition(Optional<EyeInFluidPredicate> eyeInFluidPredicate, Optional<EntityPredicate> entityPredicate, Optional<WeatherPredicate> weatherPredicate) {
        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EyeInFluidPredicate.CODEC.optionalFieldOf("eye_in_fluid_conditions").forGetter(Condition::eyeInFluidPredicate),
                EntityPredicate.CODEC.optionalFieldOf("entity_conditions").forGetter(Condition::entityPredicate),
                WeatherPredicate.CODEC.optionalFieldOf("weather_conditions").forGetter(Condition::weatherPredicate)
        ).apply(instance, Condition::new));
    }

    public void apply(ServerPlayer dragon) {
        PenaltySupply penaltySupply = dragon.getData(DSDataAttachments.PENALTY_SUPPLY);
        if(trigger instanceof SupplyTrigger supplyTrigger) {
            AttributeInstance penaltyResistance = dragon.getAttribute(supplyTrigger.attributeToUseAsBase());
            int penaltyResistanceTime = penaltyResistance != null ? (int) (penaltyResistance.getValue()) : 0;
            if(penaltySupply.getMaxSupply(supplyTrigger.id()) != penaltyResistanceTime) {
                penaltySupply.initialize(supplyTrigger.id(), penaltyResistanceTime, supplyTrigger.reductionRateMultiplier(), supplyTrigger.regenerationRate());
                PacketDistributor.sendToPlayer(dragon, new SyncAddPenaltySupply(supplyTrigger.id(), penaltyResistanceTime, supplyTrigger.reductionRateMultiplier(), supplyTrigger.regenerationRate()));
            }
        }

        boolean conditionMet = false;
        for(Condition condition : conditions) {
            if((condition.entityPredicate.isEmpty() || condition.entityPredicate.get().matches((ServerLevel)dragon.level(), dragon.position(), dragon))
                    && (condition.weatherPredicate.isEmpty() || condition.weatherPredicate.get().matches((ServerLevel) dragon.level(), dragon.position()))
                    && (condition.eyeInFluidPredicate.isEmpty() || condition.eyeInFluidPredicate.get().matches(dragon))) {
                conditionMet = true;
                break;
            }
        }

        if(trigger.matches(dragon, conditionMet)) {
            effect.apply(dragon);
        }
    }

    public void remove(ServerPlayer dragon) {
        PenaltySupply penaltySupply = dragon.getData(DSDataAttachments.PENALTY_SUPPLY);
        if(trigger instanceof SupplyTrigger supplyTrigger) {
            penaltySupply.remove(supplyTrigger.id());
            PacketDistributor.sendToPlayer(dragon, new SyncRemovePenaltySupply(supplyTrigger.id()));
        }
    }

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }
}
