package by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.world_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.ProjectileInstance;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.projectile.targeting.ProjectileTargeting;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public record ProjectileLightningEffect(ProjectileTargeting.WorldTargeting targeting) implements ProjectileWorldEffect {

    public static final MapCodec<ProjectileLightningEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProjectileTargeting.WorldTargeting.CODEC.fieldOf("target").forGetter(ProjectileLightningEffect::targeting)
    ).apply(instance, ProjectileLightningEffect::new));

    @Override
    public void apply(ServerLevel level, ServerPlayer player, ProjectileInstance projectile, Vec3 position) {
        LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(level);
        lightningboltentity.moveTo(new Vec3(position.x, position.y, position.z));
        level.addFreshEntity(lightningboltentity);
    }

    @Override
    public MapCodec<? extends ProjectileWorldEffect> worldCodec() {
        return CODEC;
    }
}
