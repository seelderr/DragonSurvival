package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.DamageReduction;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;

@AbilityInfo(compatibleWith = {AbilityInfo.Type.PASSIVE, AbilityInfo.Type.ACTIVE_SIMPLE})
public record DamageReductionEffect(List<DamageReduction> damageReductions) implements AbilityEntityEffect {
    public static final MapCodec<DamageReductionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageReduction.DIRECT_CODEC.listOf().fieldOf("damage_reductions").forGetter(DamageReductionEffect::damageReductions)
    ).apply(instance, DamageReductionEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        int abilityLevel = ability.getLevel();
        damageReductions().forEach(reduction -> reduction.apply(entity, abilityLevel));
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
