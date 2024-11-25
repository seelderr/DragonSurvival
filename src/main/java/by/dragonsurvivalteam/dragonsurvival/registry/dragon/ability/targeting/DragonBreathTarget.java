package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

// TODO :: add sub entity predicate for easy is ally / team check (and tamable animals) / spectator
public record DragonBreathTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue range) implements Targeting {
    public static final MapCodec<DragonBreathTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(BlockTargeting.CODEC, EntityTargeting.CODEC).fieldOf("target").forGetter(DragonBreathTarget::target),
            LevelBasedValue.CODEC.fieldOf("range").forGetter(DragonBreathTarget::range)
    ).apply(instance, DragonBreathTarget::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability) {
        target().ifLeft(blockTarget -> {
            AABB breathArea = calculateBreathArea(dragon, DragonStateProvider.getData(dragon).getSize(), range().calculate(ability.getLevel()));

            BlockPos.betweenClosedStream(breathArea).forEach(position -> {
                if (blockTarget.targetConditions().isEmpty() || blockTarget.targetConditions().get().matches(level, position)) {
                    blockTarget.effect().apply(level, dragon, ability, position);
                }
            });
        }).ifRight(entityTarget -> {
            AABB breathArea = calculateBreathArea(dragon, DragonStateProvider.getData(dragon).getSize(), range().calculate(ability.getLevel()));

            // TODO :: use Entity.class (would affect items etc.)?
            level.getEntities(EntityTypeTest.forClass(LivingEntity.class), breathArea,
                    entity -> entityTarget.targetConditions().map(conditions -> conditions.matches(level, dragon.position(), entity)).orElse(true)
            ).forEach(entity -> entityTarget.effect().apply(level, dragon, ability, entity));
        });
    }

    @Override
    public MapCodec<? extends Targeting> codec() {
        return CODEC;
    }

    public static AABB calculateBreathArea(final Player player, double size, double range) {
        Vec3 viewVector = player.getLookAngle().scale(range);
        double defaultRadius = size * 0.03; // TODO :: should this be configurable?

        // Set the radius (value will be at least the default radius)
        double xOffset = getOffset(viewVector.x(), defaultRadius);
        double yOffset = Math.abs(viewVector.y());
        double zOffset = getOffset(viewVector.z(), defaultRadius);

        // Check for look angle to avoid extending the range in the direction the player is not facing / looking
        double xMin = (player.getLookAngle().x() < 0 ? xOffset : defaultRadius);
        double yMin = (player.getLookAngle().y() < 0 ? yOffset : 0);
        double zMin = (player.getLookAngle().z() < 0 ? zOffset : defaultRadius);
        Vec3 min = new Vec3(Math.abs(xMin), Math.abs(yMin), Math.abs(zMin));

        double xMax = (player.getLookAngle().x() > 0 ? xOffset : defaultRadius);
        double yMax = (player.getLookAngle().y() > 0 ? yOffset + player.getEyeHeight() : player.getEyeHeight());
        double zMax = (player.getLookAngle().z() > 0 ? zOffset : defaultRadius);
        Vec3 max = new Vec3(Math.abs(xMax), Math.abs(yMax), Math.abs(zMax));

        return new AABB(player.position().subtract(min), player.position().add(max));
    }

    private static double getOffset(double value, double defaultValue) {
        if (value < 0) {
            return Math.min(value, -defaultValue);
        }

        return Math.max(value, defaultValue);
    }
}
