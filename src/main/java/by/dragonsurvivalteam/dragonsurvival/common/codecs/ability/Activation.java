package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.client.sounds.FollowEntitySound;
import by.dragonsurvivalteam.dragonsurvival.network.sound.SyncAbilityTickingSound;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
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

    public void playStartSound(final Player dragon) {
        sound.flatMap(Sound::start).ifPresent(start -> {
            if(dragon.level().isClientSide()) {
                Vec3 pos = dragon.getEyePosition(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
                SimpleSoundInstance sound = new SimpleSoundInstance(
                        start,
                        SoundSource.PLAYERS,
                        1.0F, 1.0F,
                        SoundInstance.createUnseededRandom(),
                        pos.x, pos.y, pos.z
                );
                Minecraft.getInstance().getSoundManager().playDelayed(sound, 0);
            } else {
                dragon.level().playSound(dragon, dragon.blockPosition(), start, SoundSource.PLAYERS, 1, 1);
            }
        });
    }

    public void playChargingSound(final Player dragon, DragonAbilityInstance instance) {
        sound.flatMap(Sound::charging).ifPresent(charging -> {
            if(dragon.level().isClientSide()) {
                TickableSoundInstance sound = new FollowEntitySound(charging, SoundSource.PLAYERS, dragon);
                instance.setSoundInstance(sound);
            } else {
                PacketDistributor.sendToPlayersTrackingEntity(dragon, new SyncAbilityTickingSound(dragon.getId(), instance.slot(), 0, false));
            }
        });
    }

    public void playLoopingSound(final Player dragon, DragonAbilityInstance instance) {
        sound.flatMap(Sound::looping).ifPresent(looping -> {
            if(dragon.level().isClientSide()) {
                TickableSoundInstance sound = new FollowEntitySound(looping, SoundSource.PLAYERS, dragon);
                instance.setSoundInstance(sound);
            } else {
                PacketDistributor.sendToPlayersTrackingEntity(dragon, new SyncAbilityTickingSound(dragon.getId(), instance.slot(), 1, false));
            }
        });
    }

    public void playEndSound(final Player dragon) {
        sound.flatMap(Sound::end).ifPresent(end -> {
            if(dragon.level().isClientSide()) {
                Vec3 pos = dragon.getEyePosition(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
                SimpleSoundInstance sound = new SimpleSoundInstance(
                        end,
                        SoundSource.PLAYERS,
                        1.0F, 1.0F,
                        SoundInstance.createUnseededRandom(),
                        pos.x, pos.y, pos.z
                );
                Minecraft.getInstance().getSoundManager().play(sound);
            } else {
                dragon.level().playSound(dragon, dragon.blockPosition(), end, SoundSource.PLAYERS, 1, 1);
            }
        });
    }

    public record Sound(
            Optional<SoundEvent> start,
            Optional<SoundEvent> charging,
            Optional<SoundEvent> looping,
            Optional<SoundEvent> end
    ) {
        public static final Codec<Sound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("start").forGetter(Sound::start),
                BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("charging").forGetter(Sound::charging),
                BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("looping").forGetter(Sound::looping),
                BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("end").forGetter(Sound::end)
        ).apply(instance, Sound::new));
    }

    // TODO :: move 'ManaCost' here? it probably won't be used by anything else
}