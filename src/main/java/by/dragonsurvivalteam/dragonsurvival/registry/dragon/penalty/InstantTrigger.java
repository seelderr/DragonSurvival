package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record InstantTrigger(int triggerRate) implements PenaltyTrigger {

    public static final MapCodec<InstantTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("trigger_rate").forGetter(InstantTrigger::triggerRate)
    ).apply(instance, InstantTrigger::new));

    @Override
    public boolean matches(PenaltyInstance instance, boolean conditionMatched) {
        if(conditionMatched) {
            instance.penaltySupply = Math.max(0, instance.penaltySupply - 1);
        } else {
            instance.penaltySupply = triggerRate;
        }

        if(instance.penaltySupply == 0) {
            instance.penaltySupply = triggerRate;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public MapCodec<? extends PenaltyTrigger> codec() {
        return CODEC;
    }
}
