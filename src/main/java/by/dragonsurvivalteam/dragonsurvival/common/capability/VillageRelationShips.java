package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public class VillageRelationShips implements NBTInterface{
	public int crimeLevel;
	public int evilStatusDuration;
	//change to minutes
	public int hunterSpawnDelay = Functions.minutesToTicks(ServerConfig.hunterSpawnDelay / 6);

	@Nullable
	public CompoundTag writeNBT(){
		CompoundTag compoundNBT = new CompoundTag();
		compoundNBT.putInt("Crime level", crimeLevel);
		compoundNBT.putInt("Evil status duration", evilStatusDuration);
		compoundNBT.putInt("Hunter spawn delay", hunterSpawnDelay);
		return compoundNBT;
	}

	public void readNBT(CompoundTag compoundNBT){
		crimeLevel = compoundNBT.getInt("Crime level");
		evilStatusDuration = compoundNBT.getInt("Evil status duration");
		hunterSpawnDelay = compoundNBT.getInt("Hunter spawn delay");
	}
}