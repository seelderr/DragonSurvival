package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSBlockTags;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** See {@link by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonDestructionHandler} for client-specific handling */
@EventBusSubscriber
public class DragonDestructionHandler {
    private static int crushTickCounter = 0;
    private static boolean isBreakingMultipleBlocks = false;
    public static float boundingBoxSizeRatioForCrushing = 4.0f;

    private static void checkAndDestroyCollidingBlocks(DragonStateHandler dragonStateHandler, PlayerTickEvent event, AABB boundingBox) {
        if (!ServerConfig.allowBlockDestruction) {
            return;
        }

        if (ServerConfig.largeBlockDestructionSize > dragonStateHandler.getSize()) {
            return;
        }

        // Copied from checkWalls in EnderDragon.java
        int i = Mth.floor(boundingBox.minX);
        int j = Mth.floor(boundingBox.minY);
        int k = Mth.floor(boundingBox.minZ);
        int l = Mth.ceil(boundingBox.maxX);
        int i1 = Mth.ceil(boundingBox.maxY);
        int j1 = Mth.ceil(boundingBox.maxZ);

        Player player = event.getEntity();
        RandomSource random = new XoroshiroRandomSource(player.level().getGameTime());

        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    BlockState blockstate = player.level().getBlockState(blockpos);

                    if (!blockstate.isAir()) {
                        boolean isInTag = blockstate.is(DSBlockTags.GIANT_DRAGON_DESTRUCTIBLE);

                        if (!isInTag && ServerConfig.destructibleBlocksIsBlacklist) {
                            if (random.nextFloat() > ServerConfig.blockDestructionRemoval) {
                                player.level().destroyBlock(blockpos, false);
                            } else {
                                player.level().removeBlock(blockpos, false);
                            }
                        } else if (isInTag) {
                            if (random.nextFloat() > ServerConfig.blockDestructionRemoval) {
                                player.level().destroyBlock(blockpos, false);
                            } else {
                                player.level().removeBlock(blockpos, false);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void checkAndDamageCrushedEntities(DragonStateHandler dragonStateHandler, ServerPlayer player, AABB boundingBox) {
        if (!ServerConfig.allowCrushing) {
            return;
        }

        if (ServerConfig.crushingSize > dragonStateHandler.getSize()) {
            return;
        }

        if (--crushTickCounter > 0) {
            return;
        }

        // Get only the bounding box of the player's feet (estimate by using only the bottom 1/3rd of the bounding box)
        AABB feetBoundingBox = new AABB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY - (boundingBox.maxY - boundingBox.minY) / 3.0, boundingBox.maxZ);

        for (LivingEntity entity : player.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, player, feetBoundingBox)) {
            // If the entity being crushed is too big, don't damage it
            if (entity.getBoundingBox().getSize() > boundingBox.getSize() / boundingBoxSizeRatioForCrushing) {
                continue;
            }

            entity.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.CRUSHED), player), (float) (dragonStateHandler.getSize() * ServerConfig.crushingDamageScalar));
            crushTickCounter = ServerConfig.crushingTickDelay;
        }
    }

    @SubscribeEvent
    public static void destroyBlocksInRadius(BlockEvent.BreakEvent event) {
        if (isBreakingMultipleBlocks || ServerConfig.largeBlockBreakRadiusScalar <= 0) {
            return;
        }

        if (!(event.getPlayer() instanceof ServerPlayer player) || player.isCrouching()) {
            return;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.isDragon() || data.getSize() < DragonStage.MAX_HANDLED_SIZE) {
            return;
        }

        event.setCanceled(true);

        isBreakingMultipleBlocks = true;
        int radius = (int) Math.floor((data.getSize() - DragonStage.MAX_HANDLED_SIZE) / 60 * ServerConfig.largeBlockBreakRadiusScalar);
        BlockPos.betweenClosedStream(AABB.ofSize(event.getPos().getCenter(), radius, radius, radius)).forEach(player.gameMode::destroyBlock);
        isBreakingMultipleBlocks = false;
    }

    @SubscribeEvent
    public static void checkAndDestroyCollidingBlocksAndCrushedEntities(PlayerTickEvent.Post event) {
        if (!ServerConfig.allowBlockDestruction && !ServerConfig.allowCrushing) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.isDragon() || !data.getDestructionEnabled() || player.isCrouching()) {
            return;
        }

        Vec2 horizontalDeltaMovement = new Vec2((float) player.getDeltaMovement().x, (float) player.getDeltaMovement().z);

        if (horizontalDeltaMovement.length() < 0.05f && Math.abs(player.getDeltaMovement().y) < 0.25f) {
            return;
        }

        AABB boundingBox = player.getBoundingBox();
        AABB blockCollisionBoundingBox = boundingBox.inflate(1.25 + (data.getSize() - DragonStage.MAX_HANDLED_SIZE) / DragonStage.MAX_HANDLED_SIZE * 0.15f);

        checkAndDestroyCollidingBlocks(data, event, blockCollisionBoundingBox);
        checkAndDamageCrushedEntities(data, player, blockCollisionBoundingBox);
    }
}
