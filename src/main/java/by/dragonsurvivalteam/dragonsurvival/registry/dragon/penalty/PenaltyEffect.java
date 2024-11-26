package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public record PenaltyEffect(HolderSet<MobEffect> effects, int amplifier, int duration, Holder<DamageType> damageType, float damage) {
    public static final Codec<PenaltyEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(BuiltInRegistries.MOB_EFFECT.key()).fieldOf("effects").forGetter(PenaltyEffect::effects),
            Codec.INT.fieldOf("amplifier").forGetter(PenaltyEffect::amplifier),
            Codec.INT.fieldOf("duration").forGetter(PenaltyEffect::duration),
            DamageType.CODEC.fieldOf("damage_type").forGetter(PenaltyEffect::damageType),
            Codec.FLOAT.fieldOf("damage").forGetter(PenaltyEffect::damage)
    ).apply(instance, PenaltyEffect::new));

    public void apply(final Player player) {
        effects.forEach(effect -> player.addEffect(new MobEffectInstance(effect, duration, amplifier)));
        player.hurt(new DamageSource(damageType, player), damage);
    }
}
