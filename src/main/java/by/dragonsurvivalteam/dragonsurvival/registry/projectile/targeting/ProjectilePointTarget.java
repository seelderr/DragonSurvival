package by.dragonsurvivalteam.dragonsurvival.registry.projectile.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public record ProjectilePointTarget(ProjectileTargeting.WorldTargeting target) implements ProjectileTargeting {
    public static final MapCodec<ProjectilePointTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProjectileTargeting.WorldTargeting.CODEC.fieldOf("target").forGetter(ProjectilePointTarget::target)
    ).apply(instance, ProjectilePointTarget::new));

    @Override
    public void apply(ServerLevel level, ServerPlayer player, ProjectileInstance projectile, Vec3 position) {
        if(target.locationConditions().isPresent() && !target.locationConditions().get().matches(level, position.x, position.y, position.z)
                || target.weatherConditions().isPresent() && !target.weatherConditions().get().matches(level)) {
            return;
        }
        target.effect().apply(level, player, projectile, position);
    }

    @Override
    public MapCodec<? extends ProjectileTargeting> codec() {
        return CODEC;
    }
}
