package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.function.Predicate;

public record LookingAtTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue range) implements AbilityTargeting {
    public static final MapCodec<LookingAtTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> AbilityTargeting.codecStart(instance)
            .and(LevelBasedValue.CODEC.fieldOf("range").forGetter(LookingAtTarget::range)).apply(instance, LookingAtTarget::new)
    );

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability) {
        target().ifLeft(blockTarget -> {
            BlockHitResult result = getBlockHitResult(dragon, ability);

            if (result.getType() == HitResult.Type.MISS) {
                return;
            }

            if (blockTarget.targetConditions().isPresent() && !blockTarget.targetConditions().get().matches(dragon.serverLevel(), result.getBlockPos()) || /* This is always checked by the predicate */ !dragon.serverLevel().isLoaded(result.getBlockPos())) {
                return;
            }

            blockTarget.effect().forEach(target -> target.apply(dragon, ability, result.getBlockPos()));
        }).ifRight(entityTarget -> {
            Predicate<Entity> filter = entity -> isEntityRelevant(dragon, entityTarget, entity) && entityTarget.targetConditions().map(conditions -> conditions.matches(dragon.serverLevel(), dragon.position(), entity)).orElse(true);
            HitResult result = getEntityHitResult(dragon, filter, ability);

            if (result instanceof EntityHitResult entityHitResult) {
                entityTarget.effect().forEach(target -> target.apply(dragon, ability, entityHitResult.getEntity()));
            }
        });
    }

    public BlockHitResult getBlockHitResult(Player dragon, final DragonAbilityInstance ability) {
        Vec3 viewVector = dragon.getViewVector(0);
        return dragon.level().clip(new ClipContext(viewVector, viewVector.scale(range().calculate(ability.level())), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
    }

    public HitResult getEntityHitResult(Player dragon, Predicate<Entity> filter, final DragonAbilityInstance ability) {
        return ProjectileUtil.getHitResultOnViewVector(dragon, filter, range().calculate(ability.level()));
    }

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }
}
