package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public record SingleTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue range) implements Targeting {
    public static final MapCodec<SingleTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(BlockTargeting.CODEC, EntityTargeting.CODEC).fieldOf("target").forGetter(SingleTarget::target),
            LevelBasedValue.CODEC.fieldOf("range").forGetter(SingleTarget::range)
    ).apply(instance, SingleTarget::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        target().ifLeft(blockTarget -> {
            Vec3 viewVector = dragon.getViewVector(0);
            BlockHitResult result = level.clip(new ClipContext(viewVector, viewVector.scale(range().calculate(ability.getLevel())), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));

            if (result.getType() == HitResult.Type.MISS) {
                return;
            }

            if (blockTarget.targetConditions().isPresent() && !blockTarget.targetConditions().get().matches(level, result.getBlockPos()) || /* This is always checked by the predicate */ !level.isLoaded(result.getBlockPos())) {
                return;
            }

            blockTarget.effect().apply(level, dragon, ability, result.getBlockPos());
        }).ifRight(entityTarget -> {
            HitResult result = ProjectileUtil.getHitResultOnViewVector(dragon, entity -> entityTarget.targetConditions().map(conditions -> conditions.matches(level, dragon.position(), entity)).orElse(true), range().calculate(ability.getLevel()));

            if (result instanceof EntityHitResult entityHitResult) {
                entityTarget.effect().apply(level, dragon, ability, entityHitResult.getEntity());
            }
        });
    }

    @Override
    public MapCodec<? extends Targeting> codec() {
        return CODEC;
    }
}
