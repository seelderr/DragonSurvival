package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects.BlockEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.Optional;

public record BlockTarget(Optional<BlockPredicate> targetConditions, BlockEffect effect, LevelBasedValue range) implements Targeting {
    public static final MapCodec<BlockTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPredicate.CODEC.optionalFieldOf("target_conditions").forGetter(BlockTarget::targetConditions),
            BlockEffect.CODEC.fieldOf("effect").forGetter(BlockTarget::effect),
            LevelBasedValue.CODEC.fieldOf("range").forGetter(BlockTarget::range)
    ).apply(instance, BlockTarget::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        Vec3 viewVector = dragon.getViewVector(0);
        BlockHitResult result = level.clip(new ClipContext(viewVector, viewVector.scale(range().calculate(ability.getLevel())), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));

        if (result.getType() == HitResult.Type.MISS) {
            return;
        }

        if (targetConditions().isPresent() && !targetConditions().get().matches(level, result.getBlockPos()) || /* This is always checked by the predicate */ !level.isLoaded(result.getBlockPos())) {
            return;
        }

        effect().apply(level, dragon, ability, result.getBlockPos());
    }

    @Override
    public MapCodec<? extends Targeting> codec() {
        return CODEC;
    }
}
