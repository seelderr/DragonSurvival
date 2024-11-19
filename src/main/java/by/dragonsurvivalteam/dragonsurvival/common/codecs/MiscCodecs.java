package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public class MiscCodecs {
    public static Codec<Double> doubleRange(double min, double max) {
        return Codec.DOUBLE.validate(value -> value.compareTo(min) > 0 && value.compareTo(max) <= 0
                ? DataResult.success(value)
                : DataResult.error(() -> "Value must be within range [" + min + ";" + max + "]: " + value)
        );
    }
}
