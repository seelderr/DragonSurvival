package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public record MobEffectPenalty(HolderSet<MobEffect> effects, int amplifier, int duration) implements PenaltyEffect {
    public static final Codec<MobEffectPenalty> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(BuiltInRegistries.MOB_EFFECT.key()).fieldOf("effects").forGetter(MobEffectPenalty::effects),
            Codec.INT.fieldOf("amplifier").forGetter(MobEffectPenalty::amplifier),
            Codec.INT.fieldOf("duration").forGetter(MobEffectPenalty::duration)
    ).apply(instance, MobEffectPenalty::new));

    public void apply(final Player player) {
        effects.forEach(effect -> player.addEffect(new MobEffectInstance(effect, duration, amplifier)));
    }

    @Override
    public MapCodec<? extends PenaltyEffect> codec() {
        return null;
    }
}
