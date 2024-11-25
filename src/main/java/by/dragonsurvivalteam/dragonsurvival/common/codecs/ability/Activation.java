package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public record Activation(Type type, int castTime, int cooldown) {
    public static final int INSTANT = -1;
    public static final int NO_COOLDOWN = 0;

    public static final Codec<Activation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(Activation::type),
            // TODO :: validate
            Codec.INT.optionalFieldOf("cast_time", INSTANT).forGetter(Activation::castTime),
            Codec.INT.optionalFieldOf("cast_time", NO_COOLDOWN).forGetter(Activation::cooldown)
    ).apply(instance, Activation::new));

    public enum Type implements StringRepresentable {
        CHANNELED("channeled"),
        TOGGLED("charged");

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