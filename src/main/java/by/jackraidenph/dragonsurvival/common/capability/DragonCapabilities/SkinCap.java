package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class SkinCap implements DragonCapability
{
	public boolean renderNewborn;
	public boolean renderYoung;
	public boolean renderAdult;
	
	@Override
	public INBT writeNBT(Capability<DragonStateHandler> capability, Direction side)
	{
		CompoundNBT tag = new CompoundNBT();
		
		tag.putBoolean("renderNewborn", renderNewborn);
		tag.putBoolean("renderYoung", renderYoung);
		tag.putBoolean("renderAdult", renderAdult);
		
		return tag;
	}
	
	@Override
	public void readNBT(Capability<DragonStateHandler> capability, Direction side, INBT base)
	{
		CompoundNBT tag = (CompoundNBT) base;

		renderNewborn = tag.getBoolean("renderNewborn");
		renderYoung = tag.getBoolean("renderYoung");
		renderAdult = tag.getBoolean("renderAdult");
	}
	
	@Override
	public void clone(DragonStateHandler oldCap)
	{
		renderNewborn = oldCap.getSkin().renderNewborn;
		renderYoung = oldCap.getSkin().renderYoung;
		renderAdult = oldCap.getSkin().renderAdult;
	}
}
