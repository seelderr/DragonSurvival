package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.EntityEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

public record EntityTarget(Optional<EntityPredicate> targetConditions, EntityEffect effect, LevelBasedValue range) implements Targeting {
    public static final MapCodec<EntityTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(EntityTarget::targetConditions),
            EntityEffect.CODEC.fieldOf("effect").forGetter(EntityTarget::effect),
            LevelBasedValue.CODEC.fieldOf("range").forGetter(EntityTarget::range)
    ).apply(instance, EntityTarget::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        HitResult result = ProjectileUtil.getHitResultOnViewVector(dragon, entity -> targetConditions().map(conditions -> conditions.matches(level, dragon.position(), entity)).orElse(true), range().calculate(ability.getLevel()));

        if (result instanceof EntityHitResult entityHitResult) {
            effect().apply(level, dragon, ability, entityHitResult.getEntity());
        }
    }

    @Override
    public MapCodec<? extends Targeting> codec() {
        return CODEC;
    }
}
