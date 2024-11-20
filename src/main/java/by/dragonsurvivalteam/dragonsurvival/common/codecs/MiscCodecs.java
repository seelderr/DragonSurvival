package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

public class MiscCodecs {
    public static Codec<Double> doubleRange(double min, double max) {
        return Codec.DOUBLE.validate(value -> value >= min && value <= max
                ? DataResult.success(value)
                : DataResult.error(() -> "Value must be within range [" + min + ";" + max + "]: " + value)
        );
    }

    public static Codec<MinMaxBounds.Doubles> sizeBounds() {
        return MinMaxBounds.Doubles.CODEC.validate(value -> {
            Double min = value.min().orElse(0d);

            if (min >= 1 && value.max().isEmpty() || value.max().get() > min) {
                return DataResult.success(value);
            } else {
                return DataResult.error(() -> "Min must be at least 1 and max must be larger than min if present " + value);
            }
        });
    }

    public record Bounds(double min, double max) {
        public static Codec<Bounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("min").forGetter(Bounds::min),
                Codec.DOUBLE.fieldOf("max").forGetter(Bounds::max)
        ).apply(instance, instance.stable(Bounds::new)));

        public boolean matches(double value) {
            return min <= value && value <= max;
        }
    }

    public static Codec<Bounds> bounds() {
        return Bounds.CODEC.validate(value -> {
            if (value.min() >= 1 && value.max() > value.min()) {
                return DataResult.success(value);
            } else {
                return DataResult.error(() -> "Min must be at least 1 and max must be larger than min " + value);
            }
        });
    }
}
