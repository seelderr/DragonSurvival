package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.util.DragonLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
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
            
            tag.putBoolean("renderSkills", instance.renderAbilityHotbar());
            
            tag.putBoolean("clawsMenu", instance.clawsMenuOpen);
            tag.put("clawsInventory", saveClawInventory(instance.clawsInventory));
            
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("mana", instance.getCurrentMana());
            nbt.putInt("selectedAbilitySlot", instance.getSelectedAbilitySlot());
            nbt.put("abilitySlots", instance.saveAbilities());
            tag.put("abilityData", nbt);
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
            instance.setDebuffData(tag.getInt("timeWithoutWater"), tag.getInt("timeInDarkness"), tag.getInt("timeInRain"));
            instance.setIsHiding(tag.getBoolean("isHiding"));
            instance.setSize(tag.getFloat("size"));
            
            instance.caveSize = tag.getFloat("caveSize");
            instance.seaSize = tag.getFloat("seaSize");
            instance.forestSize = tag.getFloat("forestSize");
    
            instance.caveWings = tag.getBoolean("caveWings");
            instance.seaWings = tag.getBoolean("seaWings");
            instance.forestWings = tag.getBoolean("forestWings");
            
            instance.setRenderAbilities(tag.getBoolean("renderSkills"));
            
            instance.clawsMenuOpen = tag.getBoolean("clawsMenu");
            
            CompoundNBT clawInv = tag.getCompound("clawsInventory");
            instance.clawsInventory = readClawInventory(clawInv);;
    
    
            if (instance.getSize() == 0)
                instance.setSize(DragonLevel.BABY.size);
            
            instance.setHasWings(tag.getBoolean("hasWings"));
            instance.setLavaAirSupply(tag.getInt("lavaAirSupply"));
    
            if(tag.contains("abilityData")) {
                CompoundNBT ability = tag.getCompound("abilityData");
    
                if (ability != null) {
                    instance.setSelectedAbilitySlot(ability.getInt("selectedAbilitySlot"));
                    instance.setCurrentMana(ability.getInt("mana"));
                    instance.loadAbilities(ability);
                }
            }
        }
    }
    
    public static Inventory readClawInventory(CompoundNBT clawInv)
    {
        Inventory inventory = new Inventory(4);
        
        for(int i = 0; i < 4; i++){
            if(clawInv.contains(Integer.toString(i))){
                ItemStack stack = ItemStack.of(clawInv.getCompound(Integer.toString(i)));
        
                if(!stack.isEmpty()){
                    inventory.setItem(i, stack);
                }
            }
        }
        
        return inventory;
    }
    
    public static CompoundNBT saveClawInventory(Inventory inv)
    {
        CompoundNBT nbt = new CompoundNBT();
    
        for(int i = 0; i < 4; i++){
            ItemStack stack = inv.getItem(i);
        
            if(!stack.isEmpty()){
                nbt.put(Integer.toString(i), stack.save(new CompoundNBT()));
            }
        }
        
        return nbt;
    }
}
