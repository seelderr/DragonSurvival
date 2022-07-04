package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import by.dragonsurvivalteam.dragonsurvival.client.sounds.SoundRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.EffectInstance2;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonBeacon;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Random;

public class DragonBeaconTileEntity extends BaseBlockTileEntity{
	public Type type = Type.NONE;
	public int tick;

	public final float bobOffs;


	public enum Type{
		PEACE,
		MAGIC,
		FIRE,
		NONE
	}

	public DragonBeaconTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(DSTileEntities.dragonBeacon, pWorldPosition, pBlockState);
		this.bobOffs = new Random().nextFloat() * (float)Math.PI * 2.0F;
	}

	public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, DragonBeaconTileEntity pBlockEntity){
		BlockState below = pLevel.getBlockState(pPos.below());
		BlockState blockState = pState;
		Block beacon = blockState.getBlock();
		if(pBlockEntity.type == Type.NONE){
			if(beacon == DSBlocks.magicDragonBeacon){
				pBlockEntity.type = Type.MAGIC;
			}else if(beacon == DSBlocks.peaceDragonBeacon){
				pBlockEntity.type = Type.PEACE;
			}else if(beacon == DSBlocks.fireDragonBeacon){
				pBlockEntity.type = Type.FIRE;
			}
		}
		if(below.getBlock() == DSBlocks.dragonMemoryBlock && pBlockEntity.type != Type.NONE){
			if(!blockState.getValue(DragonBeacon.LIT)){
				pLevel.setBlockAndUpdate(pPos, blockState.cycle(DragonBeacon.LIT));
				pLevel.playSound(null, pPos, SoundRegistry.activateBeacon, SoundSource.BLOCKS, 1, 1);
			}
			if(!pLevel.isClientSide){
				List<Player> dragons = pLevel.getEntitiesOfClass(Player.class, new AABB(pPos).inflate(50).expandTowards(0, pLevel.getMaxBuildHeight(), 0), DragonUtils::isDragon);
				switch(pBlockEntity.type){
					case PEACE -> dragons.forEach(playerEntity -> {
						ConfigHandler.configList(MobEffect.class, ServerConfig.peaceBeaconEffects).forEach(effect -> {
							if(effect != null){
								playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ServerConfig.minutesOfDragonEffect) + 5));
							}
						});
					});
					case MAGIC -> dragons.forEach(playerEntity -> {
						ConfigHandler.configList(MobEffect.class, ServerConfig.magicBeaconEffects).forEach(effect -> {
							if(effect != null){
								playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ServerConfig.minutesOfDragonEffect) + 5));
							}
						});
					});
					case FIRE -> dragons.forEach(playerEntity -> {
						ConfigHandler.configList(MobEffect.class, ServerConfig.fireBeaconEffects).forEach(effect -> {
							if(effect != null){
								playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ServerConfig.minutesOfDragonEffect) + 5));
							}
						});
					});
				}
			}
		}else{
			if(pState.getValue(DragonBeacon.LIT)){
				pLevel.setBlockAndUpdate(pPos, pState.cycle(DragonBeacon.LIT));
				pLevel.playSound(null, pPos, SoundRegistry.deactivateBeacon, SoundSource.BLOCKS, 1, 1);
			}
		}
	}

	@Override
	public void load(CompoundTag compoundNBT){
		super.load(compoundNBT);
		type = Type.valueOf(compoundNBT.getString("Type"));
	}

	@Override
	public void saveAdditional(CompoundTag compoundNBT){
		compoundNBT.putString("Type", type.name());
	}
}