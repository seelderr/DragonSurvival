package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.DamageModification;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.util.DSColors;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
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

public record DamageModificationEffect(List<DamageModification> modifications) implements AbilityEntityEffect {
    public static final MapCodec<DamageModificationEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageModification.CODEC.listOf().fieldOf("modifications").forGetter(DamageModificationEffect::modifications)
    ).apply(instance, DamageModificationEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        modifications.forEach(modification -> modification.apply(dragon, entity, ability));
    }

    @Override
    public void remove(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            modifications.forEach(modification -> modification.remove(livingEntity));
        }
    }

    @Override
    public List<MutableComponent> getDescription(final Player dragon, final DragonAbilityInstance ability) {
        List<MutableComponent> components = new ArrayList<>();

        for (DamageModification damageModification : modifications) {
            double duration = Functions.ticksToSeconds((int) damageModification.duration().calculate(ability.level()));
            MutableComponent name;

            if (damageModification.isFireImmune(ability.level())) {
                name = Component.translatable(LangKey.ABILITY_FIRE_IMMUNITY).append("\n");
            } else {
                name = Component.empty();
            }

            // Vanilla doesn't have translation keys for its damage types, so we'll just have to use a generic translation key for now (unless we want to add our own even for vanilla types just for this tooltip)
            // TODO :: since we seem to intend to use it in 'LangKey.ABILITY_DAMAGE' we should probably add the translations?
            //  in either case 'immunity' might be the wrong word here since in normally its a damage multiplier (reducing or increasing the damage)
            name = name.append(Component.translatable(LangKey.ABILITY_GENERIC_IMMUNITY));

            if (duration > 0) {
                name = name.append(Component.translatable(LangKey.ABILITY_EFFECT_DURATION, DSColors.blue(duration)));
            }

            components.add(name);
        }

        return components;
    }

    @Override
    public boolean shouldAppendSelfTargetingToDescription() {
        return false;
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
