package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SupplyTrigger(int triggerRate, int durationToTrigger, float supplyRegenRate, ResourceLocation supplyBarSprites) implements PenaltyTrigger {

    public static final MapCodec<SupplyTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("trigger_rate").forGetter(SupplyTrigger::triggerRate),
            Codec.INT.fieldOf("duration_to_trigger").forGetter(SupplyTrigger::durationToTrigger),
            Codec.FLOAT.fieldOf("supply_regen_rate").forGetter(SupplyTrigger::supplyRegenRate),
            ResourceLocation.CODEC.fieldOf("supply_bar_sprites").forGetter(SupplyTrigger::supplyBarSprites)
    ).apply(instance, SupplyTrigger::new));

    public boolean matches(final PenaltyInstance instance, boolean conditionMatched)  {
        if(conditionMatched) {
            instance.penaltySupply = Math.max(0, instance.penaltySupply - 1);
        } else {
            instance.penaltySupply = Math.min(durationToTrigger, instance.penaltySupply + (int) Math.ceil(durationToTrigger * supplyRegenRate));
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
