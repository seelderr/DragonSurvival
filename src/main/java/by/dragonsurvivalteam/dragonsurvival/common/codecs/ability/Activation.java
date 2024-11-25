package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Activation(Type type, Optional<Data> data) {
    public static final Codec<Activation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(Activation::type),
            // Can we use 'Codec.either()' to map this two different codecs depending on the type...?
            // Since 'CHANNELED' should not have the option to define 'toggled'
            Data.CODEC.optionalFieldOf("data").forGetter(Activation::data)
    ).apply(instance, Activation::new));

    public enum Type implements StringRepresentable {
        CHANNELED("channeled"),
        CHARGED("charged"); // If the Data::time is 0 then it is INSTANT

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

    public record Data(int time, boolean toggled) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("time").forGetter(Data::time),
                Codec.BOOL.optionalFieldOf("toggled", false).forGetter(Data::toggled)
        ).apply(instance, Data::new));
    }
}