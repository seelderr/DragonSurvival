package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.Modifier;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.util.DSColors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.PercentageAttribute;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public record ModifierEffect(List<ModifierWithDuration> modifiers, boolean displayTooltipAsSeconds) implements AbilityEntityEffect {
    public static final MapCodec<ModifierEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ModifierWithDuration.CODEC.listOf().fieldOf("modifiers").forGetter(ModifierEffect::modifiers),
            // TODO :: it seems kind of weird to have this as a field in the modifier itself
            //  which then can vary per effect, sth. the user may not know and get confused about - the tooltip time should be consistent across effects
            Codec.BOOL.optionalFieldOf("display_tooltip_as_seconds", false).forGetter(ModifierEffect::displayTooltipAsSeconds)
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
    public boolean shouldAppendSelfTargetingToDescription() {
        return false;
    }

    @Override
    public List<MutableComponent> getDescription(final Player dragon, final DragonAbilityInstance ability) {
        List<MutableComponent> components = new ArrayList<>();

        for (ModifierWithDuration modifierWithDuration : modifiers) {
            float duration = modifierWithDuration.duration().calculate(ability.level()) / 20.f;

            for (Modifier modifier : modifierWithDuration.modifiers()) {
                MutableComponent name = Component.literal("§6■ ").append(Component.translatable(modifier.attribute().value().getDescriptionId()).withColor(DSColors.ORANGE));
                float amount = modifier.amount().calculate(ability.level());
                String number = amount > 0 ? "+" : amount < 0 ? "-" : "";

                if (modifier.attribute().value() instanceof PercentageAttribute) {
                    number += NumberFormat.getPercentInstance().format(amount);
                } else {
                    if(displayTooltipAsSeconds) {
                        number += String.format("%.0f", amount / 20.f) + "s";
                    } else {
                        number += String.format("%.2f", amount);
                    }
                }

                Component value = Component.literal("§6: ").append(Component.literal(number).withStyle(modifier.attribute().value().getStyle(amount > 0)));
                name = name.append(value);

                if (duration > 0) {
                    name = name.append(Component.translatable(LangKey.ABILITY_EFFECT_DURATION, DSColors.blue(duration)));
                }

                components.add(name);
            }
        }

        return components;
    }

    public static List<AbilityEntityEffect> single(final ModifierWithDuration modifier, final boolean displayTooltipAsSeconds) {
        return List.of(new ModifierEffect(List.of(modifier), displayTooltipAsSeconds));
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
