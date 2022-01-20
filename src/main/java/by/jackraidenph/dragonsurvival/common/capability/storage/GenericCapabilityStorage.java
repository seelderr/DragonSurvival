package by.jackraidenph.dragonsurvival.common.capability.storage;

import by.jackraidenph.dragonsurvival.common.capability.caps.GenericCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class GenericCapabilityStorage {
    
    protected ListTag newDoubleList(double... p_70087_1_) {
        ListTag listnbt = new ListTag();
        
        for(double d0 : p_70087_1_) {
            listnbt.add(DoubleTag.valueOf(d0));
        }
        
        return listnbt;
    }
    
    @Nullable
    public Tag writeNBT(Capability<GenericCapability> capability, GenericCapability instance, Direction side) {
        CompoundTag compoundNBT = new CompoundTag();
        if(instance.lastPos != null) {
            compoundNBT.put("lastPos", newDoubleList(instance.lastPos.x, instance.lastPos.y, instance.lastPos.z));
        }
    
        compoundNBT.putInt("lastAfflicted", instance.lastAfflicted);
        return compoundNBT;
    }

    public void readNBT(Capability<GenericCapability> capability, GenericCapability instance, Direction side, Tag nbt) {
        CompoundTag compoundNBT = (CompoundTag) nbt;
        
        if(compoundNBT.contains("lastPos")){
            ListTag listnbt = compoundNBT.getList("lastPos", 6);
            instance.lastPos = new Vec3(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
        }
        
        instance.lastAfflicted = compoundNBT.getInt("lastAfflicted");
    }
}