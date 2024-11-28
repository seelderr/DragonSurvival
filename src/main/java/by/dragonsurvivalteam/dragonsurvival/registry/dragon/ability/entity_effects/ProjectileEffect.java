package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericArrowEntity;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericBallEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileData;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

@AbilityInfo(compatibleWith = {AbilityInfo.Type.ACTIVE_SIMPLE, AbilityInfo.Type.ACTIVE_CHANNELED})
public record ProjectileEffect(
        Holder<ProjectileData> projectileData,
        LevelBasedValue numberOfProjectiles,
        LevelBasedValue projectileSpread,
        LevelBasedValue speed
) implements AbilityEntityEffect {
    public static final MapCodec<ProjectileEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProjectileData.CODEC.fieldOf("projectile_data").forGetter(ProjectileEffect::projectileData),
            LevelBasedValue.CODEC.fieldOf("number_of_projectiles").forGetter(ProjectileEffect::numberOfProjectiles),
            LevelBasedValue.CODEC.optionalFieldOf("projectile_spread", LevelBasedValue.constant(0)).forGetter(ProjectileEffect::projectileSpread),
            LevelBasedValue.CODEC.fieldOf("speed").forGetter(ProjectileEffect::speed)
    ).apply(instance, ProjectileEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        ProjectileData projectileData = projectileData().value();
        Either<ProjectileData.GenericBallData, ProjectileData.GenericArrowData> specificData = projectileData.specificProjectileData();
        if(specificData.right().isPresent()) {
            ProjectileData.GenericArrowData arrowData = specificData.right().get();

            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                // Copied from AbstractArrow.java constructor
                Vec3 launchPos = new Vec3(dragon.getX(), dragon.getEyeY() - 0.1F, dragon.getZ());
                GenericArrowEntity arrow = new GenericArrowEntity(
                        arrowData.resource().location(ability.getLevel()),
                        projectileData.canHitPredicate(),
                        projectileData.tickingEffects(),
                        projectileData.commonHitEffects(),
                        projectileData.entityHitEffects(),
                        projectileData.blockHitEffects(),
                        dragon.serverLevel(),
                        ability.getLevel(),
                        (int)arrowData.piercingLevel().calculate(ability.getLevel())
                );
                arrow.setPos(launchPos);
                arrow.setOwner(dragon);
                arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                arrow.shootFromRotation(dragon, dragon.getXRot(), dragon.getYRot(), 0.0F, speed.calculate(ability.getLevel()), i * projectileSpread.calculate(ability.getLevel()));
                dragon.level().addFreshEntity(arrow);
            }
        } else if(specificData.left().isPresent()) {
            ProjectileData.GenericBallData ballData = specificData.left().get();

            for (int i = 0; i < numberOfProjectiles.calculate(ability.getLevel()); i++) {
                Vec3 eyePos = dragon.getEyePosition();
                Vec3 lookAngle = dragon.getLookAngle();

                Vec3 projPos;
                if (dragon.getAbilities().flying) {
                    projPos = lookAngle.scale(2.0F).add(eyePos);
                } else {
                    projPos = lookAngle.scale(1.0F).add(eyePos);
                }

                GenericBallEntity projectile = new GenericBallEntity(
                        ballData.ballResources(),
                        ballData.trailParticle(),
                        dragon.serverLevel(),
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
                projectile.shootFromRotation(dragon, dragon.getXRot(), dragon.getYRot(), 1.0F, speed.calculate(ability.getLevel()), i * projectileSpread.calculate(ability.getLevel()));
                dragon.level().addFreshEntity(projectile);
            }
        }
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
