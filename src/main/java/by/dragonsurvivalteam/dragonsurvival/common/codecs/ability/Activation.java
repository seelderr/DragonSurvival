package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Activation(Type type, Optional<LevelBasedValue> initialManaCost, Optional<LevelBasedValue> castTime, Optional<LevelBasedValue> cooldown) {
    public static final Codec<Activation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(Activation::type),
            LevelBasedValue.CODEC.optionalFieldOf("initial_mana_cost").forGetter(Activation::initialManaCost),
            LevelBasedValue.CODEC.optionalFieldOf("cast_time").forGetter(Activation::castTime),
            LevelBasedValue.CODEC.optionalFieldOf("cooldown").forGetter(Activation::cooldown)
    ).apply(instance, Activation::new));

    public enum Type implements StringRepresentable {
        CHANNELED("channeled"),
        SIMPLE("simple");

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