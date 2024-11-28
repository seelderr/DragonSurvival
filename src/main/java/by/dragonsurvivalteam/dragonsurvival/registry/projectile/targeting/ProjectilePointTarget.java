package by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public record ProjectilePointTarget(ProjectileTargeting.WorldTargeting target) implements ProjectileTargeting {
    public static final MapCodec<ProjectilePointTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProjectileTargeting.WorldTargeting.CODEC.fieldOf("target").forGetter(ProjectilePointTarget::target)
    ).apply(instance, ProjectilePointTarget::new));

    @Override
    public void apply(Projectile projectile, int projectileLevel) {
        Vec3 position = projectile.position();
        ServerLevel level = (ServerLevel) projectile.level();
        if(level.getGameTime() % target.tickRate() != 0) {
            return;
        }

        if(target.locationConditions().isPresent() && !target.locationConditions().get().matches(level, position.x, position.y, position.z)
                || target.weatherConditions().isPresent() && !target.weatherConditions().get().matches(level)
                || target.randomConditions().isPresent() && !target.randomConditions().get().matches(level, projectileLevel)) {
            return;
        }

        target.effects().forEach(effect -> effect.apply(projectile, projectileLevel));
    }

    @Override
    public MapCodec<? extends ProjectileTargeting> codec() {
        return CODEC;
    }
}
