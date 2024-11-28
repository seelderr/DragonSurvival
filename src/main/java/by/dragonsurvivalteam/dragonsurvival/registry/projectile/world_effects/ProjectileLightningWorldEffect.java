package by.dragonsurvivalteam.dragonsurvival.registry.projectile.world_effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.Projectile;

public record ProjectileLightningWorldEffect() implements ProjectileWorldEffect {

    public static final ProjectileLightningWorldEffect INSTANCE = new ProjectileLightningWorldEffect();
    public static final MapCodec<ProjectileLightningWorldEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(Projectile projectile, int projectileLevel) {
        LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(projectile.level());
        lightningboltentity.moveTo(projectile.position());
        if(projectile.getOwner() instanceof ServerPlayer serverPlayer) {
            lightningboltentity.setCause(serverPlayer);
        }
        projectile.level().addFreshEntity(lightningboltentity);
    }

    @Override
    public MapCodec<? extends ProjectileWorldEffect> worldCodec() {
        return CODEC;
    }
}
