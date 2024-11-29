package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

@AbilityInfo(compatibleWith = {AbilityInfo.Type.PASSIVE, AbilityInfo.Type.ACTIVE_SIMPLE})
public record ModifierEffect(List<ModifierWithDuration> modifiers) implements AbilityEntityEffect {
    public static final MapCodec<ModifierEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ModifierWithDuration.DIRECT_CODEC.listOf().fieldOf("modifiers").forGetter(ModifierEffect::modifiers)
    ).apply(instance, ModifierEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            String dragonType = DragonStateProvider.getOptional(entity).map(DragonStateHandler::getTypeNameLowerCase).orElse(null);
            int abilityLevel = ability.getLevel();

            modifiers().forEach(modifier -> modifier.applyModifiers(livingEntity, dragonType, abilityLevel));
        }
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
