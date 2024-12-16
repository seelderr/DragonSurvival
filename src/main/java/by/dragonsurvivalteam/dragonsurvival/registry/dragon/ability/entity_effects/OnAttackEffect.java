package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.OnAttackEffectInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.OnAttackEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.ArrayList;
import java.util.List;

public record OnAttackEffect(HolderSet<MobEffect> effects, LevelBasedValue amplifier, LevelBasedValue duration, LevelBasedValue probability) implements AbilityEntityEffect {
    public static final MapCodec<OnAttackEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(BuiltInRegistries.MOB_EFFECT.key()).fieldOf("effects").forGetter(OnAttackEffect::effects),
            LevelBasedValue.CODEC.fieldOf("amplifier").forGetter(OnAttackEffect::amplifier),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(OnAttackEffect::duration),
            LevelBasedValue.CODEC.optionalFieldOf("probability", LevelBasedValue.constant(1)).forGetter(OnAttackEffect::probability)
    ).apply(instance, OnAttackEffect::new));

    @Override
    public void apply(ServerPlayer dragon, DragonAbilityInstance ability, Entity entity) {
        if(dragon != entity) {
            throw new IllegalArgumentException("The entity must be the same as the dragon for OnAttackEffect.");
        }

        OnAttackEffects data = OnAttackEffects.getData(entity);
        data.addEffect(ability.id(), new OnAttackEffectInstance(effects, (int)amplifier.calculate(ability.level()), (int)duration.calculate(ability.level()), probability.calculate(ability.level())));
    }

    @Override
    public void remove(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        if(dragon != entity) {
            throw new IllegalArgumentException("The entity must be the same as the dragon for OnAttackEffect.");
        }

        OnAttackEffects data = OnAttackEffects.getData(entity);
        data.removeEffect(ability.id());
    }

    @Override
    public boolean shouldAppendSelfTargetingToDescription() {
        return false;
    }

    @Override
    public List<MutableComponent> getDescription(final Player dragon, final DragonAbilityInstance ability) {
        List<MutableComponent> components = new ArrayList<>();
        float duration = duration().calculate(ability.level()) / 20.f;
        for (Holder<MobEffect> mobEffect : effects()) {
            MutableComponent name = Component.literal("§6■ ").append(Component.translatable(LangKey.ABILITY_APPLIES).append(Component.translatable(mobEffect.value().getDescriptionId())).withColor(-219136));

            int amplifier = (int) amplifier().calculate(ability.level());
            if (amplifier > 0) {
                name.append(Component.literal(Integer.toString(amplifier)).withColor(-219136));
            }

            name.append(Component.translatable(LangKey.ABILITY_EFFECT_DURATION, duration));

            float probability = probability().calculate(ability.level());
            if (probability < 1) {
                name.append(Component.translatable(LangKey.ABILITY_EFFECT_CHANCE, String.format("%.0f", probability * 100)));
            }

            name.append(Component.translatable(LangKey.ABILITY_ON_HIT));

            components.add(name);
        }

        return components;
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
