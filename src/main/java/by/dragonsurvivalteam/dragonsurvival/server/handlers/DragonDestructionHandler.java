package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import static by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler.DRAGON_DESTRUCTIBLE_BLOCKS;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDestructionEnabled;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class DragonDestructionHandler {

    private static int crushTickCounter = 0;
    private static boolean isBreakingMultipleBlocks = false;
    public static float boundingBoxSizeRatioForCrushing = 4.0f;

    private static void checkAndDestroyCollidingBlocks(DragonStateHandler dragonStateHandler, PlayerTickEvent event, AABB boundingBox) {
        if(!ServerConfig.allowLargeBlockDestruction) {
            return;
        }

        if(ServerConfig.largeBlockDestructionSize > dragonStateHandler.getSize()) {
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
                        if(ServerConfig.useBlacklistForDestructibleBlocks) {
                            if(!DRAGON_DESTRUCTIBLE_BLOCKS.contains(blockstate.getBlock())) {
                                if(random.nextFloat() > ServerConfig.largeBlockDestructionRemovePercentage) {
                                    player.level().destroyBlock(blockpos, false);
                                }
                                else {
                                    player.level().removeBlock(blockpos, false);
                                }
                            }
                        }
                        else {
                            if(DRAGON_DESTRUCTIBLE_BLOCKS.contains(blockstate.getBlock())) {
                                if(random.nextFloat() > ServerConfig.largeBlockDestructionRemovePercentage) {
                                    player.level().destroyBlock(blockpos, false);
                                }
                                else {
                                    player.level().removeBlock(blockpos, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void checkAndDamageCrushedEntities(DragonStateHandler dragonStateHandler, ServerPlayer player, AABB boundingBox) {
        if(!ServerConfig.allowCrushing) {
            return;
        }

        if(ServerConfig.crushingSize > dragonStateHandler.getSize()) {
            return;
        }

        if(--crushTickCounter > 0) {
            return;
        }

        // Get only the bounding box of the player's feet (estimate by using only the bottom 1/3 of the bounding box)
        AABB feetBoundingBox = new AABB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY - (boundingBox.maxY - boundingBox.minY) / 3.0, boundingBox.maxZ);

        for(var entity : player.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, player, feetBoundingBox)){
            // If the entity being crushed is too big, don't damage it.
            if(entity.getBoundingBox().getSize() > boundingBox.getSize() / boundingBoxSizeRatioForCrushing) {
                continue;
            }

            entity.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.CRUSHED), player), (float)(dragonStateHandler.getSize() * ServerConfig.crushingDamageScalar));
            crushTickCounter = ServerConfig.crushingTickDelay;
        }
    }

    @SubscribeEvent
    public static void destroyBlocksInRadius(BlockEvent.BreakEvent event)
    {
        if(isBreakingMultipleBlocks) {
            return;
        }

        if(!ServerConfig.allowLargeScaling) {
            return;
        }

        if(ServerConfig.largeBlockBreakRadiusScalar <= 0.0f) {
            return;
        }

        if(!(event.getPlayer() instanceof ServerPlayer player)){
            return;
        }

        if(player.isCrouching()) {
            return;
        }

        DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
            if(dragonStateHandler.isDragon()) {
                if(dragonStateHandler.getSize() < ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
                    return;
                }

                isBreakingMultipleBlocks = true;

                event.setCanceled(true);

                // Break the blocks in a radius around the broken block
                int radius = (int) Math.floor((dragonStateHandler.getSize() - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / 60.f * ServerConfig.largeBlockBreakRadiusScalar);
                BlockPos pos = event.getPos();
                for(int x = -radius; x <= radius; x++) {
                    for(int y = -radius; y <= radius; y++) {
                        for(int z = -radius; z <= radius; z++) {
                            BlockPos newPos = pos.offset(x, y, z);
                            player.gameMode.destroyBlock(newPos);
                        }
                    }
                }

                isBreakingMultipleBlocks = false;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void toggleDestructionEnabled(ClientTickEvent.Pre event) {
        if(!ServerConfig.allowLargeBlockDestruction && !ServerConfig.allowCrushing) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
                if (playerStateHandler.isDragon()) {
                    if(Keybind.DISABLE_DESTRUCTION.consumeClick()) {
                        playerStateHandler.setDestructionEnabled(!playerStateHandler.getDestructionEnabled());
                        PacketDistributor.sendToServer(new SyncDestructionEnabled.Data(player.getId(), playerStateHandler.getDestructionEnabled()));

                        player.displayClientMessage(Component.translatable(playerStateHandler.getDestructionEnabled() ? "ds.destruction.toggled_on" : "ds.destruction.toggled_off"), true);
                    }
                }
            });
        }
    }


    @SubscribeEvent
    public static void checkAndDestroyCollidingBlocksAndCrushedEntities(PlayerTickEvent.Post event) {
        if(!ServerConfig.allowLargeBlockDestruction && !ServerConfig.allowCrushing) {
            return;
        }

        if(!(event.getEntity() instanceof ServerPlayer player)){
            return;
        }

        DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
            if(dragonStateHandler.isDragon()) {

                if(!dragonStateHandler.getDestructionEnabled()) {
                    return;
                }

                if(player.isCrouching()) {
                    return;
                }

                Vec2 deltaXZ = new Vec2((float)player.getDeltaMovement().x, (float)player.getDeltaMovement().z);
                if(deltaXZ.length() < 0.05f && Math.abs(player.getDeltaMovement().y) < 0.25f) {
                    return;
                }

                AABB boundingBox = player.getBoundingBox();
                AABB blockCollisionBoundingBox = boundingBox.inflate(1.25 + (dragonStateHandler.getSize() - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE * 0.15f);
                AABB crushingBoundingBox = blockCollisionBoundingBox;

                checkAndDestroyCollidingBlocks(dragonStateHandler, event, blockCollisionBoundingBox);
                checkAndDamageCrushedEntities(dragonStateHandler, player, crushingBoundingBox);
            }
        });
    }
}
