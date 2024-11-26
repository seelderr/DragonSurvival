package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SupplyTrigger(int triggerRate, float maxSupply, float reductionRate, float regenerationRate, ResourceLocation supplyBarSprites) implements PenaltyTrigger {

    public static final MapCodec<SupplyTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("trigger_rate").forGetter(SupplyTrigger::triggerRate),
            Codec.FLOAT.fieldOf("max").forGetter(SupplyTrigger::maxSupply),
            Codec.FLOAT.fieldOf("reduction_rate").forGetter(SupplyTrigger::reductionRate),
            Codec.FLOAT.fieldOf("regeneration_rate").forGetter(SupplyTrigger::regenerationRate),
            ResourceLocation.CODEC.fieldOf("supply_bar").forGetter(SupplyTrigger::supplyBarSprites)
    ).apply(instance, SupplyTrigger::new));

    public boolean matches(final PenaltyInstance instance, boolean conditionMatched)  {
        if(conditionMatched) {
            instance.penaltySupply = Math.max(0, instance.penaltySupply - reductionRate);
        } else {
            instance.penaltySupply = Math.min(maxSupply, instance.penaltySupply + regenerationRate);
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
