package by.jackraidenph.dragonsurvival.server.tileentity;

import by.jackraidenph.dragonsurvival.client.sounds.SoundRegistry;
import by.jackraidenph.dragonsurvival.common.EffectInstance2;
import by.jackraidenph.dragonsurvival.common.blocks.DSBlocks;
import by.jackraidenph.dragonsurvival.common.blocks.DragonBeacon;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class DragonBeaconTileEntity extends BaseBlockTileEntity{
    public Type type = Type.NONE;
    public int tick;
    public enum Type {
        PEACE,
        MAGIC,
        FIRE,
        NONE
    }
    
    public DragonBeaconTileEntity(BlockPos pWorldPosition, BlockState pBlockState)
    {
        super(DSTileEntities.dragonBeacon, pWorldPosition, pBlockState);
    }
    
    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, DragonBeaconTileEntity pBlockEntity) {
        BlockState below = pLevel.getBlockState(pPos.below());
        BlockState blockState = pState;
        Block beacon = blockState.getBlock();
        if (pBlockEntity.type == Type.NONE) {
            if (beacon == DSBlocks.magicDragonBeacon)
                pBlockEntity.type = Type.MAGIC;
            else if (beacon == DSBlocks.peaceDragonBeacon)
                pBlockEntity. type = Type.PEACE;
            else if (beacon == DSBlocks.fireDragonBeacon)
                pBlockEntity.type = Type.FIRE;
        }
        if (below.getBlock() == DSBlocks.dragonMemoryBlock && pBlockEntity.type != Type.NONE) {
            if (!blockState.getValue(DragonBeacon.LIT)) {
                pLevel.setBlockAndUpdate(pPos, blockState.cycle(DragonBeacon.LIT));
                pLevel.playSound(null, pPos, SoundRegistry.activateBeacon, SoundSource.BLOCKS, 1, 1);
            }
            if (!pLevel.isClientSide) {
                List<Player> dragons = pLevel.getEntitiesOfClass(Player.class, new AABB(pPos).inflate(50).expandTowards(0, pLevel.getMaxBuildHeight(), 0), DragonStateProvider::isDragon);
                switch (pBlockEntity.type) {
                    case PEACE:
                        dragons.forEach(playerEntity -> {
                            ConfigHandler.COMMON.peaceBeaconEffects.get().forEach(s -> {
                                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(s));
                                if (effect != null)
                                    playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ConfigHandler.COMMON.minutesOfDragonEffect.get()) + 5));
                            });
                        });
                        break;
                    case MAGIC:
                        dragons.forEach(playerEntity -> {
                            ConfigHandler.COMMON.magicBeaconEffects.get().forEach(s -> {
                                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(s));
                                if (effect != null)
                                    playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ConfigHandler.COMMON.minutesOfDragonEffect.get()) + 5));
                            });
                        });
                        break;
                    case FIRE:
                        dragons.forEach(playerEntity -> {
                            ConfigHandler.COMMON.fireBeaconEffects.get().forEach(s -> {
                                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(s));
                                if (effect != null)
                                    playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ConfigHandler.COMMON.minutesOfDragonEffect.get()) + 5));
                            });
                        });
                        break;
                }
            }
        } else {
            BlockState thisState = pState;
            if (thisState.getValue(DragonBeacon.LIT)) {
                pLevel.setBlockAndUpdate(pPos, thisState.cycle(DragonBeacon.LIT));
                pLevel.playSound(null, pPos, SoundRegistry.deactivateBeacon, SoundSource.BLOCKS, 1, 1);
            }
        }
        pBlockEntity.tick++;
    }

    @Override
    public void saveAdditional(CompoundTag compoundNBT) {
        compoundNBT.putString("Type", type.name());
    }

    @Override
    public void load(CompoundTag compoundNBT) {
        super.load(compoundNBT);
        type = Type.valueOf(compoundNBT.getString("Type"));
    }
}
