package by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record ProjectileDamageEffect(Holder<DamageType> damageType, LevelBasedValue amount) implements ProjectileEntityEffect {
    public static final MapCodec<ProjectileDamageEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(ProjectileDamageEffect::damageType),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(ProjectileDamageEffect::amount)
    ).apply(instance, ProjectileDamageEffect::new));

    @Override
    public void apply(final ServerLevel level, final ServerPlayer player, final ProjectileInstance projectile, final Entity entity) {
        // TODO :: also apply damage to entities (e.g. items, boats or experience)?
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.hurt(new DamageSource(damageType(), player), amount().calculate(projectile.getLevel()));
        }
    }

    @Override
    public MapCodec<? extends ProjectileEntityEffect> entityCodec() {
        return CODEC;
    }
}
