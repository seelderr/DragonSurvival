package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PenaltyTrigger(int triggerRate, int durationToTrigger, float supplyRegenRate) {

    public static final Codec<PenaltyTrigger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("trigger_rate").forGetter(PenaltyTrigger::triggerRate),
            Codec.INT.fieldOf("duration_to_trigger").forGetter(PenaltyTrigger::durationToTrigger),
            Codec.FLOAT.fieldOf("supply_regen_rate").forGetter(PenaltyTrigger::supplyRegenRate)
    ).apply(instance, PenaltyTrigger::new));

    boolean matches(final PenaltyInstance instance, boolean conditionMatched)  {
        if(conditionMatched) {
            instance.penaltySupply = Math.max(0, instance.penaltySupply - 1);
        } else if(durationToTrigger > 0) {
            instance.penaltySupply = Math.min(durationToTrigger, instance.penaltySupply + (int) Math.ceil(durationToTrigger * supplyRegenRate));
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
}
