package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates.EyeInFluidPredicate;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates.WeatherPredicate;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAddPenaltySupply;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncRemovePenaltySupply;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.PenaltySupply;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty.PenaltyEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty.PenaltyTrigger;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty.SupplyTrigger;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public record PenaltyAbilityEffect(List<Condition> conditions, PenaltyEffect effect, PenaltyTrigger trigger) implements AbilityEntityEffect {
    public static final MapCodec<PenaltyAbilityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Condition.CODEC.listOf().fieldOf("conditions").forGetter(PenaltyAbilityEffect::conditions),
            PenaltyEffect.CODEC.fieldOf("effect").forGetter(PenaltyAbilityEffect::effect),
            PenaltyTrigger.CODEC.fieldOf("trigger").forGetter(PenaltyAbilityEffect::trigger)
    ).apply(instance, PenaltyAbilityEffect::new));


    public record Condition(Optional<EyeInFluidPredicate> eyeInFluidPredicate, Optional<EntityPredicate> entityPredicate, Optional<WeatherPredicate> weatherPredicate) {
        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EyeInFluidPredicate.CODEC.optionalFieldOf("eye_in_fluid_conditions").forGetter(Condition::eyeInFluidPredicate),
                EntityPredicate.CODEC.optionalFieldOf("entity_conditions").forGetter(Condition::entityPredicate),
                WeatherPredicate.CODEC.optionalFieldOf("weather_conditions").forGetter(Condition::weatherPredicate)
        ).apply(instance, Condition::new));
    }

    @Override
    public void apply(ServerPlayer dragon, DragonAbilityInstance ability, Entity entity) {
        if(dragon != entity) {
            throw new IllegalArgumentException("The entity must be the same as the dragon for PenaltyAbilityEffect");
        }

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

    @Override
    public void remove(ServerPlayer dragon, DragonAbilityInstance ability, Entity entity) {
        if(dragon != entity) {
            throw new IllegalArgumentException("The entity must be the same as the dragon for PenaltyAbilityEffect");
        }

        PenaltySupply penaltySupply = dragon.getData(DSDataAttachments.PENALTY_SUPPLY);
        if(trigger instanceof SupplyTrigger supplyTrigger) {
            penaltySupply.remove(supplyTrigger.id());
            PacketDistributor.sendToPlayer(dragon, new SyncRemovePenaltySupply(supplyTrigger.id()));
        }
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
