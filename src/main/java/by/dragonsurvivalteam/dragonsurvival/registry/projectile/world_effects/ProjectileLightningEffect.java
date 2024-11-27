package by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.Projectile;

public record ProjectileLightningEffect() implements ProjectileWorldEffect {

    public static final ProjectileLightningEffect INSTANCE = new ProjectileLightningEffect();
    public static final MapCodec<ProjectileLightningEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(Projectile projectile, int projectileLevel) {
        LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(projectile.level());
        lightningboltentity.moveTo(projectile.position());
        projectile.level().addFreshEntity(lightningboltentity);
    }

    @Override
    public MapCodec<? extends ProjectileWorldEffect> worldCodec() {
        return CODEC;
    }
}
