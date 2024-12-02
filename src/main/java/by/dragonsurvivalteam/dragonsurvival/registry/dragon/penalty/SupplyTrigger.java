package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.PenaltySupply;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record SupplyTrigger(String id, int triggerRate, float maximumSupply, float reductionRate, float regenerationRate, ResourceLocation supplyBar) implements PenaltyTrigger {
    public static final MapCodec<SupplyTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(SupplyTrigger::id),
            Codec.INT.fieldOf("trigger_rate").forGetter(SupplyTrigger::triggerRate),
            Codec.FLOAT.fieldOf("maximum_supply").forGetter(SupplyTrigger::maximumSupply),
            Codec.FLOAT.fieldOf("reduction_rate").forGetter(SupplyTrigger::reductionRate),
            Codec.FLOAT.fieldOf("regeneration_rate").forGetter(SupplyTrigger::regenerationRate),
            ResourceLocation.CODEC.fieldOf("supply_bar").forGetter(SupplyTrigger::supplyBar)
    ).apply(instance, SupplyTrigger::new));

    public boolean matches(final Player dragon, final PenaltyInstance instance, boolean conditionMatched) {
        // TODO :: check trigger_rate against player tickCount?
        PenaltySupply penaltySupply = dragon.getData(DSDataAttachments.PENALTY_SUPPLY);
        penaltySupply.initialize(id(), maximumSupply(), reductionRate(), regenerationRate()); // TODO :: do this somewhere else?

        if (conditionMatched) {
            penaltySupply.reduce(id());
        } else {
            penaltySupply.regenerate(id());
        }

        return !penaltySupply.hasSupply(id());
    }

    @Override
    public MapCodec<? extends PenaltyTrigger> codec() {
        return CODEC;
    }
}