package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public record ModifierEffect(List<ModifierWithDuration> modifiers) implements AbilityEntityEffect {
    public static final MapCodec<ModifierEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ModifierWithDuration.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierEffect::modifiers)
    ).apply(instance, ModifierEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            modifiers.forEach(modifier -> modifier.apply(dragon, ability, livingEntity));
        }
    }

    @Override
    public void remove(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            modifiers.forEach(modifier -> modifier.remove(livingEntity));
        }
    }

    @Override
    public List<MutableComponent> getDescription(final Player dragon, final DragonAbilityInstance ability) {
        List<MutableComponent> components = new ArrayList<>();
        for (ModifierWithDuration modifierWithDuration : modifiers()) {
            float duration = modifierWithDuration.duration().calculate(ability.level()) / 20.f;
            for (Modifier modifier : modifierWithDuration.modifiers()) {
                MutableComponent name = Component.literal("§6■ ").append(Component.translatable(modifier.attribute().value().getDescriptionId()).withColor(-219136));
                float amount = modifier.amount().calculate(ability.level());
                String number;
                if (amount > 0) {
                    number = "+" + amount;
                } else {
                    number = String.valueOf(amount);
                }
                Component value = Component.literal("§6: ").append(Component.literal(number).withStyle(modifier.attribute().value().getStyle(amount > 0)));
                name = name.append(value);

                if (duration > 0) {
                    name = name.append(Component.translatable(LangKey.ABILITY_EFFECT_DURATION, duration));
                }

                components.add(name);
            }
        }

        return components;
    }

    public static List<AbilityEntityEffect> single(final ModifierWithDuration modifier) {
        return List.of(new ModifierEffect(List.of(modifier)));
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
