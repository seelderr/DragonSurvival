package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import by.dragonsurvivalteam.dragonsurvival.client.sounds.SoundRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.EffectInstance2;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonBeacon;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class DragonBeaconTileEntity extends BaseBlockTileEntity implements ITickableTileEntity{
	public Type type = Type.NONE;
	public int tick;

	public enum Type{
		PEACE,
		MAGIC,
		FIRE,
		NONE
	}

	public DragonBeaconTileEntity(){
		super(DSTileEntities.dragonBeacon);
	}

	@Override
	public void tick(){
		BlockState below = level.getBlockState(getBlockPos().below());
		BlockState blockState = getBlockState();
		Block beacon = blockState.getBlock();
		if(type == Type.NONE){
			if(beacon == DSBlocks.magicDragonBeacon){
				type = Type.MAGIC;
			}else if(beacon == DSBlocks.peaceDragonBeacon){
				type = Type.PEACE;
			}else if(beacon == DSBlocks.fireDragonBeacon){
				type = Type.FIRE;
			}
		}
		if(below.getBlock() == DSBlocks.dragonMemoryBlock && type != Type.NONE){
			if(!blockState.getValue(DragonBeacon.LIT)){
				level.setBlockAndUpdate(getBlockPos(), blockState.cycle(DragonBeacon.LIT));
				level.playSound(null, getBlockPos(), SoundRegistry.activateBeacon, SoundCategory.BLOCKS, 1, 1);
			}
			if(!level.isClientSide){
				List<PlayerEntity> dragons = level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(getBlockPos()).inflate(50).expandTowards(0, level.getMaxBuildHeight(), 0), DragonUtils::isDragon);
				switch(type){
					case PEACE:
						dragons.forEach(playerEntity -> {
							ConfigHandler.COMMON.peaceBeaconEffects.get().forEach(s -> {
								Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s));
								if(effect != null){
									playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ConfigHandler.COMMON.minutesOfDragonEffect.get()) + 5));
								}
							});
						});
						break;
					case MAGIC:
						dragons.forEach(playerEntity -> {
							ConfigHandler.COMMON.magicBeaconEffects.get().forEach(s -> {
								Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s));
								if(effect != null){
									playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ConfigHandler.COMMON.minutesOfDragonEffect.get()) + 5));
								}
							});
						});
						break;
					case FIRE:
						dragons.forEach(playerEntity -> {
							ConfigHandler.COMMON.fireBeaconEffects.get().forEach(s -> {
								Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s));
								if(effect != null){
									playerEntity.addEffect(new EffectInstance2(effect, Functions.secondsToTicks(ConfigHandler.COMMON.minutesOfDragonEffect.get()) + 5));
								}
							});
						});
						break;
				}
			}
		}else{
			BlockState thisState = getBlockState();
			if(thisState.getValue(DragonBeacon.LIT)){
				level.setBlockAndUpdate(getBlockPos(), thisState.cycle(DragonBeacon.LIT));
				level.playSound(null, getBlockPos(), SoundRegistry.deactivateBeacon, SoundCategory.BLOCKS, 1, 1);
			}
		}
		tick++;
	}

	@Override
	public void load(BlockState p_230337_1_, CompoundNBT compoundNBT){
		super.load(p_230337_1_, compoundNBT);
		type = Type.valueOf(compoundNBT.getString("Type"));
	}

	@Override
	public CompoundNBT save(CompoundNBT compoundNBT){
		compoundNBT.putString("Type", type.name());
		return super.save(compoundNBT);
	}
}