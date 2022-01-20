package by.jackraidenph.dragonsurvival.common.capability.storage;

import by.jackraidenph.dragonsurvival.common.capability.caps.VillageRelationShips;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class VillageRelationshipsStorage {
    @Nullable
    public Tag writeNBT(Capability<VillageRelationShips> capability, VillageRelationShips instance, Direction side) {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putInt("Crime level", instance.crimeLevel);
        compoundNBT.putInt("Evil status duration", instance.evilStatusDuration);
        compoundNBT.putInt("Hunter spawn delay", instance.hunterSpawnDelay);
        return compoundNBT;
    }

    public void readNBT(Capability<VillageRelationShips> capability, VillageRelationShips instance, Direction side, Tag nbt) {
        CompoundTag compoundNBT = (CompoundTag) nbt;
        instance.crimeLevel = compoundNBT.getInt("Crime level");
        instance.evilStatusDuration = compoundNBT.getInt("Evil status duration");
        instance.hunterSpawnDelay = compoundNBT.getInt("Hunter spawn delay");
    }
}