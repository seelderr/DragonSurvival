package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class BlockInteractionHandler {
    @SubscribeEvent
    public static void createAltar(PlayerInteractEvent.RightClickBlock rightClickBlock){
        if(!ServerConfig.altarCraftable){
            return;
        }

        ItemStack itemStack = rightClickBlock.getItemStack();
        if(itemStack.is(DSItems.ELDER_DRAGON_BONE)){
            if(!rightClickBlock.getEntity().isSpectator()){

                final Level world = rightClickBlock.getLevel();
                final BlockPos blockPos = rightClickBlock.getPos();
                BlockState blockState = world.getBlockState(blockPos);
                final Block block = blockState.getBlock();

                boolean replace = false;
                rightClickBlock.getEntity().isSpectator();
                rightClickBlock.getEntity().isCreative();
                BlockPlaceContext direction = new BlockPlaceContext(rightClickBlock.getLevel(), rightClickBlock.getEntity(), rightClickBlock.getHand(), rightClickBlock.getItemStack(), new BlockHitResult(new Vec3(0, 0, 0), rightClickBlock.getEntity().getDirection(), blockPos, false));
                if(block == Blocks.STONE){
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_STONE.get().getStateForPlacement(direction));
                    replace = true;
                }else if(block == Blocks.MOSSY_COBBLESTONE){
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_MOSSY_COBBLESTONE.get().getStateForPlacement(direction));
                    replace = true;
                }else if(block == Blocks.SANDSTONE){
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_SANDSTONE.get().getStateForPlacement(direction));
                    replace = true;
                }else if(block == Blocks.RED_SANDSTONE){
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_RED_SANDSTONE.get().getStateForPlacement(direction));
                    replace = true;
                }else if(ResourceHelper.getKey(block).getPath().contains(ResourceHelper.getKey(Blocks.OAK_LOG).getPath())){
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_OAK_LOG.get().getStateForPlacement(direction));
                    replace = true;
                }else if(ResourceHelper.getKey(block).getPath().contains(ResourceHelper.getKey(Blocks.BIRCH_LOG).getPath())){
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_BIRCH_LOG.get().getStateForPlacement(direction));
                    replace = true;
                }else if(block == Blocks.PURPUR_BLOCK){
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_PURPUR_BLOCK.get().getStateForPlacement(direction));
                    replace = true;
                }else if(block == Blocks.NETHER_BRICKS){
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_NETHER_BRICKS.get().getStateForPlacement(direction));
                    replace = true;
                }else if(block == Blocks.BLACKSTONE){
                    rightClickBlock.getEntity().getDirection();
                    world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_BLACKSTONE.get().getStateForPlacement(direction));
                    replace = true;
                }

                if(replace){
                    if(!rightClickBlock.getEntity().isCreative()){
                        itemStack.shrink(1);
                    }
                    rightClickBlock.setCanceled(true);
                    world.playSound(rightClickBlock.getEntity(), blockPos, SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1, 1);
                    rightClickBlock.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
