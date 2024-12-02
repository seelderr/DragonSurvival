package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;

public record InstantTrigger(int triggerRate) implements PenaltyTrigger {
    public static final MapCodec<InstantTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("trigger_rate").forGetter(InstantTrigger::triggerRate)
    ).apply(instance, InstantTrigger::new));

    @Override
    public boolean matches(final Player dragon, final PenaltyInstance instance, boolean conditionMatched) {
        // TODO :: check trigger_rate against player tickCount?
        return conditionMatched;
    }

    @Override
    public MapCodec<? extends PenaltyTrigger> codec() {
        return CODEC;
    }
}
