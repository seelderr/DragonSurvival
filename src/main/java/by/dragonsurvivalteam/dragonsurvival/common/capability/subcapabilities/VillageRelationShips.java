package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public class VillageRelationShips extends SubCap{
	public int crimeLevel;
	public int evilStatusDuration;
	//change to minutes
	public int hunterSpawnDelay = Functions.minutesToTicks(ServerConfig.hunterSpawnDelay / 6);

	public VillageRelationShips(DragonStateHandler handler){
		super(handler);
	}

	@Nullable
	public CompoundTag writeNBT(){
		CompoundTag compoundNBT = new CompoundTag();
		compoundNBT.putInt("crimeLevel", crimeLevel);
		compoundNBT.putInt("evilStatusDuration", evilStatusDuration);
		compoundNBT.putInt("hunterSpawnDelay", hunterSpawnDelay);
		return compoundNBT;
	}

	public void readNBT(CompoundTag compoundNBT){
		crimeLevel = compoundNBT.getInt("crimeLevel");
		evilStatusDuration = compoundNBT.getInt("evilStatusDuration");
		hunterSpawnDelay = compoundNBT.getInt("hunterSpawnDelay");
	}
}