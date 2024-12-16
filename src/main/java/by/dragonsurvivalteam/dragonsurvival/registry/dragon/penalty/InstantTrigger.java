package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record InstantTrigger(int triggerRate) implements PenaltyTrigger {
    public static final MapCodec<InstantTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("trigger_rate").forGetter(InstantTrigger::triggerRate)
    ).apply(instance, InstantTrigger::new));

    @Override
    public boolean matches(final ServerPlayer dragon, boolean conditionMatched) {
        if(dragon.level().getGameTime() % triggerRate == 0) {
            return conditionMatched;
        } else {
            return false;
        }
    }

    @Override
    public MapCodec<? extends PenaltyTrigger> codec() {
        return CODEC;
    }
}
