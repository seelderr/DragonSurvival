package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record PotionEffect(HolderSet<MobEffect> effects, LevelBasedValue amplifier, LevelBasedValue duration, LevelBasedValue probability) implements EntityEffect {
    public static final MapCodec<PotionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(BuiltInRegistries.MOB_EFFECT.key()).fieldOf("effects").forGetter(PotionEffect::effects),
            LevelBasedValue.CODEC.fieldOf("amplifier").forGetter(PotionEffect::amplifier),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(PotionEffect::duration),
            LevelBasedValue.CODEC.optionalFieldOf("probability", LevelBasedValue.constant(1)).forGetter(PotionEffect::probability)
    ).apply(instance, PotionEffect::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            int abilityLevel = ability.getLevel();

            effects().forEach(effect -> {
                if (livingEntity.getRandom().nextDouble() < probability().calculate(abilityLevel)) {
                    livingEntity.addEffect(new MobEffectInstance(effect, (int) duration().calculate(abilityLevel), (int) amplifier().calculate(abilityLevel)));
                }
            });
        }
    }

    @Override
    public MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}
