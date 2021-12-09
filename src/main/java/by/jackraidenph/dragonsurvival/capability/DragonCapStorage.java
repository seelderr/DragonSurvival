package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.util.DragonLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class DragonCapStorage implements Capability.IStorage<DragonStateHandler> {
    
    
    @Override
    public INBT writeNBT(Capability<DragonStateHandler> capability, DragonStateHandler instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("type", instance.getType().toString());
    
        if (instance.isDragon()) {
            DragonStateHandler.DragonMovementData movementData = instance.getMovementData();
            tag.putDouble("bodyYaw", movementData.bodyYaw);
            tag.putDouble("headYaw", movementData.headYaw);
            tag.putDouble("headPitch", movementData.headPitch);
            tag.putBoolean("bite", movementData.bite);
            tag.putBoolean("dig", movementData.dig);
            DragonStateHandler.DragonDebuffData debuffData = instance.getDebuffData();
            tag.putDouble("timeWithoutWater", debuffData.timeWithoutWater);
            tag.putInt("timeInDarkness", debuffData.timeInDarkness);
            tag.putInt("timeInRain", debuffData.timeInRain);
            tag.putBoolean("isHiding", instance.isHiding());
            tag.putFloat("size", instance.getSize());
            tag.putBoolean("hasWings", instance.hasWings());
            tag.putInt("lavaAirSupply", instance.getLavaAirSupply());
    
            tag.putFloat("caveSize", instance.caveSize);
            tag.putFloat("seaSize", instance.seaSize);
            tag.putFloat("forestSize", instance.forestSize);
    
            tag.putBoolean("caveWings", instance.caveWings);
            tag.putBoolean("seaWings", instance.seaWings);
            tag.putBoolean("forestWings", instance.forestWings);
            
            tag.put("clawInv", instance.getClawInventory().writeNBT(capability, side));
            tag.put("emotes", instance.getEmotes().writeNBT(capability, side));
            tag.put("magic", instance.getMagic().writeNBT(capability, side));
        }
        return tag;
    }

    @Override
    public void readNBT(Capability<DragonStateHandler> capability, DragonStateHandler instance, Direction side, INBT base) {
        CompoundNBT tag = (CompoundNBT) base;
        if (tag.getString("type").equals(""))
        	instance.setType(DragonType.NONE);
        else
        	instance.setType(DragonType.valueOf(tag.getString("type")));
        
        if (instance.isDragon()) {
            instance.setMovementData(tag.getDouble("bodyYaw"), tag.getDouble("headYaw"), tag.getDouble("headPitch"), tag.getBoolean("bite"));
            instance.getMovementData().dig = tag.getBoolean("dig");
            instance.setDebuffData(tag.getInt("timeWithoutWater"), tag.getInt("timeInDarkness"), tag.getInt("timeInRain"));
            instance.setIsHiding(tag.getBoolean("isHiding"));
            instance.setSize(tag.getFloat("size"));
            
            instance.caveSize = tag.getFloat("caveSize");
            instance.seaSize = tag.getFloat("seaSize");
            instance.forestSize = tag.getFloat("forestSize");
    
            instance.caveWings = tag.getBoolean("caveWings");
            instance.seaWings = tag.getBoolean("seaWings");
            instance.forestWings = tag.getBoolean("forestWings");
            
            if(tag.contains("clawInv")) instance.getClawInventory().readNBT(capability, side, tag.get("clawInv"));
            if(tag.contains("emotes"))  instance.getEmotes().readNBT(capability, side, tag.get("emotes"));
            if(tag.contains("magic"))  instance.getMagic().readNBT(capability, side, tag.get("magic"));
    
            if (instance.getSize() == 0)
                instance.setSize(DragonLevel.BABY.size);
            
            instance.setHasWings(tag.getBoolean("hasWings"));
            instance.setLavaAirSupply(tag.getInt("lavaAirSupply"));
        }
    }
}
