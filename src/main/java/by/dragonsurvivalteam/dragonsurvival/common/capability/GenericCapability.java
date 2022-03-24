package by.dragonsurvivalteam.dragonsurvival.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.vector.Vector3d;

public class GenericCapability implements NBTInterface{
	public Vector3d lastPos;
	public int lastAfflicted = -1;
	public int chainCount = 0;

	@Override
	public CompoundNBT writeNBT(){
		CompoundNBT compoundNBT = new CompoundNBT();

		if(lastPos != null){
			compoundNBT.put("lastPos", newDoubleList(lastPos.x, lastPos.y, lastPos.z));
		}

		compoundNBT.putInt("lastAfflicted", lastAfflicted);
		return compoundNBT;
	}

	protected ListNBT newDoubleList(double... p_70087_1_){
		ListNBT listnbt = new ListNBT();

		for(double d0 : p_70087_1_){
			listnbt.add(DoubleNBT.valueOf(d0));
		}

		return listnbt;
	}

	@Override
	public void readNBT(CompoundNBT base){
		if(base.contains("lastPos")){
			ListNBT listnbt = base.getList("lastPos", 6);
			lastPos = new Vector3d(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
		}

		lastAfflicted = base.getInt("lastAfflicted");
	}
}