package by.jackraidenph.dragonsurvival.common.capability.caps;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public class VillageRelationShips {
    
    public int crimeLevel;
    public int evilStatusDuration;
    //change to minutes
    public int hunterSpawnDelay = Functions.minutesToTicks(ConfigHandler.COMMON.hunterSpawnDelay.get() / 6);
    
    @Nullable
    public CompoundTag writeNBT() {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putInt("Crime level", crimeLevel);
        compoundNBT.putInt("Evil status duration", evilStatusDuration);
        compoundNBT.putInt("Hunter spawn delay", hunterSpawnDelay);
        return compoundNBT;
    }
    
    public void readNBT(CompoundTag nbt) {
        CompoundTag compoundNBT = (CompoundTag) nbt;
        crimeLevel = compoundNBT.getInt("Crime level");
        evilStatusDuration = compoundNBT.getInt("Evil status duration");
        hunterSpawnDelay = compoundNBT.getInt("Hunter spawn delay");
    }
}
