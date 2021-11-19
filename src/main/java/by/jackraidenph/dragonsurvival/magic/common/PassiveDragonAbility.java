package by.jackraidenph.dragonsurvival.magic.common;

import net.minecraft.nbt.CompoundNBT;

public class PassiveDragonAbility extends DragonAbility {
	
	private boolean active = true;
	
	public PassiveDragonAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	public void toggle(){
		active = !active;
	}
	
	public int getLevelCost(){
		return 1 + (int)(0.75 * getLevel());
	}
	
	@Override
	public PassiveDragonAbility createInstance()
	{
		return new PassiveDragonAbility(id, icon, minLevel, maxLevel);
	}
	
	public CompoundNBT saveNBT(){
		CompoundNBT nbt = super.saveNBT();
		nbt.putBoolean("status", active);
		return nbt;
	}
	
	public void loadNBT(CompoundNBT nbt){
		super.loadNBT(nbt);
		active = nbt.getBoolean("status");
	}
}
