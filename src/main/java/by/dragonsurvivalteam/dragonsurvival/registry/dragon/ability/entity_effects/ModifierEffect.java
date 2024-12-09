package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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

    public static List<AbilityEntityEffect> single(final ModifierWithDuration modifier) {
        return List.of(new ModifierEffect(List.of(modifier)));
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
