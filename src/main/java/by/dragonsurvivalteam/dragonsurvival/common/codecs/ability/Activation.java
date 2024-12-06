package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Activation(
        Type type,
        Optional<LevelBasedValue> initialManaCost,
        Optional<ManaCost> continuousManaCost,
        Optional<LevelBasedValue> castTime,
        Optional<LevelBasedValue> cooldown,
        Optional<Sound> sound
) {
    private static final Codec<Activation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(Activation::type),
            LevelBasedValue.CODEC.optionalFieldOf("initial_mana_cost").forGetter(Activation::initialManaCost),
            ManaCost.CODEC.optionalFieldOf("continuous_mana_cost").forGetter(Activation::continuousManaCost),
            LevelBasedValue.CODEC.optionalFieldOf("cast_time").forGetter(Activation::castTime),
            LevelBasedValue.CODEC.optionalFieldOf("cooldown").forGetter(Activation::cooldown),
            Sound.CODEC.optionalFieldOf("sound").forGetter(Activation::sound)
    ).apply(instance, Activation::new));

    public static Codec<Activation> codec() {
        return CODEC.validate(activation -> {
            switch (activation.type()) {
                case PASSIVE -> {
                    if (activation.castTime().isPresent()) {
                        return DataResult.error(() -> "[cast_time] is not applicable for [" + activation.type() + "] activation");
                    }

                    if (activation.cooldown().isPresent()) {
                        return DataResult.error(() -> "[cooldown] is not applicable for [" + activation.type() + "] activation");
                    }
                }
                case ACTIVE_CHANNELED -> { /* Nothing to do */ }
                case ACTIVE_SIMPLE -> {
                    if (activation.continuousManaCost().isPresent()) {
                        return DataResult.error(() -> "[continuous_mana_cost] is not applicable for [" + activation.type() + "] activation");
                    }

                    if (activation.sound().isPresent() && activation.sound().get().looping().isPresent()) {
                        return DataResult.error(() -> "[sound] -> [looping] is not applicable for [" + activation.type() + "] activation");
                    }
                }
            }

            if (activation.castTime().isEmpty() && activation.sound().isPresent() && activation.sound().get().charging().isPresent()) {
                return DataResult.error(() -> "[sound] -> [charging] is not applicable when no [cast_time] is present");
            }

            return DataResult.success(activation);
        });
    }

    public enum Type implements StringRepresentable {
        PASSIVE("passive"),
        // TODO :: remove the 'ACTIVE_' part?
        ACTIVE_CHANNELED("active_channeled"),
        ACTIVE_SIMPLE("active_simple");

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

    public record Sound(
            Optional<Holder<SoundEvent>> start,
            Optional<Holder<SoundEvent>> charging,
            Optional<Holder<SoundEvent>> looping,
            Optional<Holder<SoundEvent>> end
    ) {
        public static final Codec<Sound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.SOUND_EVENT.holderByNameCodec().optionalFieldOf("start").forGetter(Sound::start),
                BuiltInRegistries.SOUND_EVENT.holderByNameCodec().optionalFieldOf("charging").forGetter(Sound::charging),
                BuiltInRegistries.SOUND_EVENT.holderByNameCodec().optionalFieldOf("looping").forGetter(Sound::looping),
                BuiltInRegistries.SOUND_EVENT.holderByNameCodec().optionalFieldOf("end").forGetter(Sound::end)
        ).apply(instance, Sound::new));
    }

    // TODO :: move 'ManaCost' here? it probably won't be used by anything else
}