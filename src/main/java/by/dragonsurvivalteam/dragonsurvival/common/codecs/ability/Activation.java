package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public record Activation(Type type, int castTime, int cooldown) {
    public static final int INSTANT = -1;
    public static final int NO_COOLDOWN = 0;

    public static final Codec<Activation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.optionalFieldOf("type", Type.NONE).forGetter(Activation::type),
            // TODO :: validate
            Codec.INT.optionalFieldOf("cast_time", INSTANT).forGetter(Activation::castTime),
            Codec.INT.optionalFieldOf("cooldown", NO_COOLDOWN).forGetter(Activation::cooldown)
    ).apply(instance, Activation::new));

    // Generally the logic triggers on 'InputEvent.Key' when they key KEY_PRESSED
    public enum Type implements StringRepresentable {
        // If the cast time has passed the 'InputEvent.Key' will trigger the existing effects while they key is KEY_HELD
        CHANNELED("channeled"),
        // If the cast time has passed a boolean (in a map?) gets switched
        // While that boolean is active the effects will try to run based on their 'Application'
        TOGGLED("charged"),
        // If the cast time has passed the effects will trigger once
        NONE("none");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}