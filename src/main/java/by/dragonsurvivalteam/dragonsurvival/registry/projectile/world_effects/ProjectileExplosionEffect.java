package by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.Level;

public record ProjectileExplosionEffect(Holder<DamageType> damageType, LevelBasedValue explosionPower, boolean fire, boolean breakBlocks, boolean canDamageSelf) implements ProjectileWorldEffect {
    public static final MapCodec<ProjectileExplosionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(ProjectileExplosionEffect::damageType),
            LevelBasedValue.CODEC.fieldOf("explosion_power").forGetter(ProjectileExplosionEffect::explosionPower),
            Codec.BOOL.fieldOf("fire").forGetter(ProjectileExplosionEffect::fire),
            Codec.BOOL.fieldOf("break_blocks").forGetter(ProjectileExplosionEffect::breakBlocks),
            Codec.BOOL.fieldOf("can_damage_self").forGetter(ProjectileExplosionEffect::canDamageSelf)
    ).apply(instance, ProjectileExplosionEffect::new));

    public void apply(final Projectile projectile, final int projectileLevel) {
        DamageSource source;
        if (projectile.getOwner() == null) {
            source = new DamageSource(damageType, projectile, projectile);
        } else {
            source = new DamageSource(damageType, projectile, projectile.getOwner());
        }

        projectile.level().explode(
                canDamageSelf ? projectile : projectile.getOwner(),
                source,
                null,
                projectile.position().x(), projectile.position().y(), projectile.position().z(),
                explosionPower.calculate(projectileLevel),
                fire,
                breakBlocks ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
    }

    public MapCodec<? extends ProjectileWorldEffect> worldCodec() {
        return CODEC;
    }
}
