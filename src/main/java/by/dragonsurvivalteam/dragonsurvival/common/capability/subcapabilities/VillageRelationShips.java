package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class VillageRelationShips extends SubCap{
	public int crimeLevel;
	public int evilStatusDuration;
	//change to minutes
	public int hunterSpawnDelay = Functions.minutesToTicks(ServerConfig.hunterSpawnDelay / 6);

	public VillageRelationShips(DragonStateHandler handler){
		super(handler);
	}

	@Override
	@Nullable public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag compoundNBT = new CompoundTag();
		compoundNBT.putInt("crimeLevel", crimeLevel);
		compoundNBT.putInt("evilStatusDuration", evilStatusDuration);
		compoundNBT.putInt("hunterSpawnDelay", hunterSpawnDelay);
		return compoundNBT;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag){
		crimeLevel = tag.getInt("crimeLevel");
		evilStatusDuration = tag.getInt("evilStatusDuration");
		hunterSpawnDelay = tag.getInt("hunterSpawnDelay");
	}
}