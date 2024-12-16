package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.PenaltySupply;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record SupplyTrigger(String id, Holder<Attribute> attributeToUseAsBase, int triggerRate, float reductionRateMultiplier, float regenerationRate) implements PenaltyTrigger {
    public static final MapCodec<SupplyTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(SupplyTrigger::id),
            Attribute.CODEC.optionalFieldOf("attribute", DSAttributes.PENALTY_RESISTANCE_TIME).forGetter(SupplyTrigger::attributeToUseAsBase),
            Codec.INT.fieldOf("trigger_rate").forGetter(SupplyTrigger::triggerRate),
            Codec.FLOAT.fieldOf("reduction_rate").forGetter(SupplyTrigger::reductionRateMultiplier),
            Codec.FLOAT.fieldOf("regeneration_rate").forGetter(SupplyTrigger::regenerationRate)
    ).apply(instance, SupplyTrigger::new));

    public boolean matches(final ServerPlayer dragon, boolean conditionMatched) {
        PenaltySupply penaltySupply = dragon.getData(DSDataAttachments.PENALTY_SUPPLY);
        if (conditionMatched) {
            penaltySupply.reduce(dragon, id());
        } else {
            penaltySupply.regenerate(dragon, id());
        }

        if(dragon.level().getGameTime() % triggerRate() == 0) {
            return !penaltySupply.hasSupply(id());
        } else {
            return false;
        }
    }

    @Override
    public MapCodec<? extends PenaltyTrigger> codec() {
        return CODEC;
    }
}