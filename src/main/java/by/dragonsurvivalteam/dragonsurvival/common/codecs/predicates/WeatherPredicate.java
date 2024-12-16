package by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record WeatherPredicate(Optional<Boolean> isRaining, Optional<Boolean> isThundering) {
    public static final Codec<WeatherPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("is_raining").forGetter(WeatherPredicate::isRaining),
            Codec.BOOL.optionalFieldOf("is_thundering").forGetter(WeatherPredicate::isThundering)
    ).apply(instance, WeatherPredicate::new));

    public boolean matches(ServerLevel level, Vec3 position) {
        return (this.isRaining.isEmpty() || this.isRaining.get() == level.isRainingAt(new BlockPos((int) position.x, (int) position.y, (int) position.z))) && (this.isThundering.isEmpty() || this.isThundering.get() == level.isThundering());
    }
}
