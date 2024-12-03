package by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public record WeatherPredicate(Optional<Boolean> isRaining, Optional<Boolean> isThundering) {
    public static final Codec<WeatherPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("is_raining").forGetter(WeatherPredicate::isRaining),
            Codec.BOOL.optionalFieldOf("is_thundering").forGetter(WeatherPredicate::isThundering)
    ).apply(instance, WeatherPredicate::new));

    public boolean matches(ServerLevel level) {
        return (this.isRaining.isEmpty() || this.isRaining.get() == level.isRaining()) && (this.isThundering.isEmpty() || this.isThundering.get() == level.isThundering());
    }
}
