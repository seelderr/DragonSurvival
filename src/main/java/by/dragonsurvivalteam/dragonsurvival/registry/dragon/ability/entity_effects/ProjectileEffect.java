package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericArrowEntity;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericBallEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileData;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record ProjectileEffect(
        Holder<ProjectileData> projectileData,
        LevelBasedValue numberOfProjectiles,
        LevelBasedValue projectileSpread,
        LevelBasedValue speed) implements AbilityEntityEffect {

    public static final MapCodec<ProjectileEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProjectileData.CODEC.fieldOf("projectile_data").forGetter(ProjectileEffect::projectileData),
            LevelBasedValue.CODEC.fieldOf("number_of_projectiles").forGetter(ProjectileEffect::numberOfProjectiles),
            LevelBasedValue.CODEC.optionalFieldOf("projectile_spread", LevelBasedValue.constant(0)).forGetter(ProjectileEffect::projectileSpread),
            LevelBasedValue.CODEC.fieldOf("speed").forGetter(ProjectileEffect::speed)
    ).apply(instance, ProjectileEffect::new));


    @Override
    public void apply(ServerLevel level, Player player, DragonAbilityInstance ability, Entity entity) {
        ProjectileData projectileData = projectileData().value();
        Either<ProjectileData.GenericArrowData, ProjectileData.GenericBallData> specificData = projectileData.specificProjectileData();
        if(specificData.left().isPresent()) {
            ProjectileData.GenericArrowData arrowData = specificData.left().get();
            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                // Copied from AbstractArrow.java constructor
                Vec3 launchPos = new Vec3(player.getX(), player.getEyeY() - 0.1F, player.getZ());
                GenericArrowEntity arrow = new GenericArrowEntity(
                        projectileData.location(),
                        projectileData.canHitPredicate(),
                        projectileData.tickingEffects(),
                        projectileData.commonHitEffects(),
                        projectileData.entityHitEffects(),
                        projectileData.blockHitEffects(),
                        level,
                        ability.getLevel(),
                        (int)arrowData.piercingLevel().calculate(ability.getLevel())
                );
                arrow.setPos(launchPos);
                arrow.setOwner(player);
                arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed.calculate(ability.getLevel()), i * projectileSpread.calculate(ability.getLevel()));
                player.level().addFreshEntity(arrow);
            }
        } else if(specificData.right().isPresent()) {
            ProjectileData.GenericBallData ballData = specificData.right().get();

            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                Vec3 eyePos = player.getEyePosition();
                Vec3 lookAngle = player.getLookAngle();

                Vec3 projPos;
                if (player.getAbilities().flying) {
                    projPos = lookAngle.scale(2.0F).add(eyePos);
                } else {
                    projPos = lookAngle.scale(1.0F).add(eyePos);
                }

                GenericBallEntity projectile = new GenericBallEntity(
                        projectileData.location(),
                        ballData.trailParticle(),
                        level,
                        EntityDimensions.scalable(ballData.xSize().calculate(ability.getLevel()), ballData.ySize().calculate(ability.getLevel())),
                        projectileData.canHitPredicate(),
                        projectileData.tickingEffects(),
                        projectileData.commonHitEffects(),
                        projectileData.entityHitEffects(),
                        projectileData.blockHitEffects(),
                        ballData.onDestroyEffects(),
                        ability.getLevel(),
                        (int)ballData.maxLingeringTicks().calculate(ability.getLevel()),
                        (int)ballData.maxMoveDistance().calculate(ability.getLevel()),
                        (int)ballData.maxLifespan().calculate(ability.getLevel())
                );

                projectile.setPos(projPos);
                projectile.accelerationPower = 0;
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 1.0F, speed.calculate(ability.getLevel()), i * projectileSpread.calculate(ability.getLevel()));
                player.level().addFreshEntity(projectile);
            }
        }
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
