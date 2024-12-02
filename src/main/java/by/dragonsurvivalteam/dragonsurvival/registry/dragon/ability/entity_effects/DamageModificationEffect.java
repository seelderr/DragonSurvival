package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.DamageModification;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;

@AbilityInfo(compatibleWith = {AbilityInfo.Type.PASSIVE, AbilityInfo.Type.ACTIVE_SIMPLE})
public record DamageModificationEffect(List<DamageModification> modifications) implements AbilityEntityEffect {
    public static final MapCodec<DamageModificationEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageModification.CODEC.listOf().fieldOf("damage_modifications").forGetter(DamageModificationEffect::modifications)
    ).apply(instance, DamageModificationEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        modifications().forEach(modification -> modification.apply(dragon, entity, ability));
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
