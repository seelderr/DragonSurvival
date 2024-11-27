package by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record ProjectileDamageEffect(Holder<DamageType> damageType, LevelBasedValue amount) implements ProjectileEntityEffect {
    public static final MapCodec<ProjectileDamageEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(ProjectileDamageEffect::damageType),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(ProjectileDamageEffect::amount)
    ).apply(instance, ProjectileDamageEffect::new));

    @Override
    public void apply(final Projectile projectile, final Entity target, final int level) {
        // TODO :: also apply damage to entities (e.g. items, boats or experience)?
        if (target instanceof LivingEntity) {
            target.hurt(new DamageSource(damageType(), projectile.getOwner()), amount().calculate(level));
        }
    }

    @Override
    public MapCodec<? extends ProjectileEntityEffect> entityCodec() {
        return CODEC;
    }
}
