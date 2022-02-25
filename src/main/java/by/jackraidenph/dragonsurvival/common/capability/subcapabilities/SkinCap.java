package by.jackraidenph.dragonsurvival.common.capability.subcapabilities;

import by.jackraidenph.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.SkinPreset;
import by.jackraidenph.dragonsurvival.common.capability.NBTInterface;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashSet;

public class SkinCap implements NBTInterface
{
	public boolean renderNewborn;
	public boolean renderYoung;
	public boolean renderAdult;
	
	public SkinPreset skinPreset = new SkinPreset();
	public HashSet<EnumSkinLayer> updateLayers = new HashSet<>();
	
	public static final String defaultSkinValue = "None";
	
	@Override
	public CompoundNBT writeNBT()
	{
		CompoundNBT tag = new CompoundNBT();
		
		tag.putBoolean("renderNewborn", renderNewborn);
		tag.putBoolean("renderYoung", renderYoung);
		tag.putBoolean("renderAdult", renderAdult);
		
		tag.put("skinPreset", skinPreset.writeNBT());
		
		return tag;
	}
	
	@Override
	public void readNBT(CompoundNBT tag)
	{
		renderNewborn = tag.getBoolean("renderNewborn");
		renderYoung = tag.getBoolean("renderYoung");
		renderAdult = tag.getBoolean("renderAdult");
		
		CompoundNBT skinNbt = tag.getCompound("skinPreset");
		skinPreset = new SkinPreset();
		skinPreset.readNBT(skinNbt);
	}
}
