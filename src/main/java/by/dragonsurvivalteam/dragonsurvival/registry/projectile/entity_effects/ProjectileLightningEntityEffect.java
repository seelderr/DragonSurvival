package by.dragonsurvivalteam.dragonsurvival.registry.projectile.entity_effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.Projectile;

public record ProjectileLightningEntityEffect() implements ProjectileEntityEffect {

    public static final ProjectileLightningEntityEffect INSTANCE = new ProjectileLightningEntityEffect();
    public static final MapCodec<ProjectileLightningEntityEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(Projectile projectile, Entity target, int projectileLevel) {
        LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(projectile.level());
        lightningboltentity.moveTo(target.position());
        projectile.level().addFreshEntity(lightningboltentity);
    }

    @Override
    public MapCodec<? extends ProjectileEntityEffect> entityCodec() {
        return CODEC;
    }
}

