package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

// TODO :: add sub entity predicate for easy is ally / team check (and tamable animals) / spectator
public record DragonBreathTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue rangeMultiplier) implements AbilityTargeting {
    public static final MapCodec<DragonBreathTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> AbilityTargeting.codecStart(instance)
            .and(LevelBasedValue.CODEC.fieldOf("range_multiplier").forGetter(DragonBreathTarget::rangeMultiplier)).apply(instance, DragonBreathTarget::new)
    );

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability) {
        target().ifLeft(blockTarget -> {
            AABB breathArea = calculateBreathArea(dragon, DragonStateProvider.getData(dragon).getSize(), rangeMultiplier().calculate(ability.getLevel()));

            BlockPos.betweenClosedStream(breathArea).forEach(position -> {
                if (blockTarget.targetConditions().isEmpty() || blockTarget.targetConditions().get().matches(dragon.serverLevel(), position)) {
                    blockTarget.effect().forEach(target -> target.apply(dragon, ability, position));
                }
            });
        }).ifRight(entityTarget -> {
            AABB breathArea = calculateBreathArea(dragon, DragonStateProvider.getData(dragon).getSize(), rangeMultiplier().calculate(ability.getLevel()));

            dragon.serverLevel().getEntities(EntityTypeTest.forClass(Entity.class), breathArea,
                    entity -> isEntityRelevant(dragon, entityTarget, entity) && entityTarget.targetConditions().map(conditions -> conditions.matches(dragon.serverLevel(), dragon.position(), entity)).orElse(true)
            ).forEach(entity -> entityTarget.effect().forEach(target -> target.apply(dragon, ability, entity)));
        });
    }

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }

    public static AABB calculateBreathArea(final Player player, double size, double rangeMultiplier) {
        Vec3 viewVector = player.getLookAngle().scale(rangeMultiplier * player.getAttributeValue(DSAttributes.DRAGON_BREATH_RANGE));
        double defaultRadius = size * 0.03;

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
