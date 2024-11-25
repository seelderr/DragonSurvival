package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

public record Active(Type type, LevelBasedValue initialManaCost, LevelBasedValue castTime, LevelBasedValue cooldown) {
    public static final int INSTANT = -1;
    public static final int NO_COOLDOWN = 0;

    public static final Codec<Active> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            // TODO :: Probably have to use either to split channeled (with mana cost per tick / tick rate) from the other two?
            Type.CODEC.optionalFieldOf("type", Type.SIMPLE).forGetter(Active::type),
            LevelBasedValue.CODEC.fieldOf("initial_mana_cost").forGetter(Active::initialManaCost), // once the cast went through (either instant or after the charge-up time)
            LevelBasedValue.CODEC.optionalFieldOf("cast_time", LevelBasedValue.constant(INSTANT)).forGetter(Active::castTime),
            LevelBasedValue.CODEC.optionalFieldOf("cooldown", LevelBasedValue.constant(NO_COOLDOWN)).forGetter(Active::cooldown) // after the key is released
    ).apply(instance, Active::new));

    public record Continuous(Passive.Ticked manaHandling) {

    }

    public record Once(Either<Passive.Reserved, Passive.Ticked> manaHandling) {

    }

    // Generally the logic triggers on 'InputEvent.Key' when they key is KEY_PRESSED
    public enum Type implements StringRepresentable {
        // If the cast time has passed the 'InputEvent.Key' will trigger the existing effects while they key is KEY_HELD
        // (Based on their 'Application')
        CHANNELED("channeled"),
        // If the cast time has passed a boolean (in a map?) gets switched when the key is KEY_PRESSED
        // While that boolean is active the effects will try to run based on their 'Application'
        TOGGLED("charged"),
        // If the cast time has passed the effects will trigger once when the key is KEY_PRESSED
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