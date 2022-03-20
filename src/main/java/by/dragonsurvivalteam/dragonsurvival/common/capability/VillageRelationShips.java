package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public class VillageRelationShips implements NBTInterface{
	public int crimeLevel;
	public int evilStatusDuration;
	//change to minutes
	public int hunterSpawnDelay = Functions.minutesToTicks(ConfigHandler.COMMON.hunterSpawnDelay.get() / 6);

	@Nullable
	public CompoundNBT writeNBT(){
		CompoundNBT compoundNBT = new CompoundNBT();
		compoundNBT.putInt("Crime level", crimeLevel);
		compoundNBT.putInt("Evil status duration", evilStatusDuration);
		compoundNBT.putInt("Hunter spawn delay", hunterSpawnDelay);
		return compoundNBT;
	}

	public void readNBT(CompoundNBT compoundNBT){
		crimeLevel = compoundNBT.getInt("Crime level");
		evilStatusDuration = compoundNBT.getInt("Evil status duration");
		hunterSpawnDelay = compoundNBT.getInt("Hunter spawn delay");
	}
}