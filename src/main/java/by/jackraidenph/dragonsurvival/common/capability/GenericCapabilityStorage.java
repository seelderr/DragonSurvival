package by.jackraidenph.dragonsurvival.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class GenericCapabilityStorage implements Capability.IStorage<GenericCapability> {
    
    protected ListNBT newDoubleList(double... p_70087_1_) {
        ListNBT listnbt = new ListNBT();
        
        for(double d0 : p_70087_1_) {
            listnbt.add(DoubleNBT.valueOf(d0));
        }
        
        return listnbt;
    }
    
    @Nullable
    public INBT writeNBT(Capability<GenericCapability> capability, GenericCapability instance, Direction side) {
        CompoundNBT compoundNBT = new CompoundNBT();
        if(instance.lastPos != null) {
            compoundNBT.put("lastPos", newDoubleList(instance.lastPos.x, instance.lastPos.y, instance.lastPos.z));
        }
        return compoundNBT;
    }

    public void readNBT(Capability<GenericCapability> capability, GenericCapability instance, Direction side, INBT nbt) {
        CompoundNBT compoundNBT = (CompoundNBT) nbt;
        
        if(compoundNBT.contains("lastPos")){
            ListNBT listnbt = compoundNBT.getList("lastPos", 6);
            instance.lastPos = new Vector3d(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
        }
    }
}