package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;

@AbilityInfo(compatibleWith = {AbilityInfo.Type.PASSIVE, AbilityInfo.Type.ACTIVE_SIMPLE, AbilityInfo.Type.ACTIVE_CHANNELED})
public record PotionEffect(HolderSet<MobEffect> effects, LevelBasedValue amplifier, LevelBasedValue duration, LevelBasedValue probability) implements AbilityEntityEffect {
    public static final MapCodec<PotionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(BuiltInRegistries.MOB_EFFECT.key()).fieldOf("effects").forGetter(PotionEffect::effects),
            LevelBasedValue.CODEC.fieldOf("amplifier").forGetter(PotionEffect::amplifier),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(PotionEffect::duration),
            LevelBasedValue.CODEC.optionalFieldOf("probability", LevelBasedValue.constant(1)).forGetter(PotionEffect::probability)
    ).apply(instance, PotionEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            int abilityLevel = ability.getLevel();

            effects().forEach(effect -> {
                MobEffectInstance currentInstance = livingEntity.getEffect(effect);

                int duration = (int) duration().calculate(abilityLevel);
                int amplifier = (int) amplifier().calculate(abilityLevel);

                if (currentInstance != null && (currentInstance.getAmplifier() >= amplifier && currentInstance.getDuration() >= duration)) {
                    // Don't do anything if the current effect is at least equally strong and has at least the same duration
                    // For all other cases this new effect will either override the current instance or be added as hidden effect
                    // (Whose duration etc. will be applied once the stronger (and shorter) effect runs out)
                    return;
                }

                if (livingEntity.getRandom().nextDouble() < probability().calculate(abilityLevel)) {
                    livingEntity.addEffect(new MobEffectInstance(effect, duration, amplifier));
                }
            });
        }
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
