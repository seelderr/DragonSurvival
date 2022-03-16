package by.jackraidenph.dragonsurvival.client.skinPartSystem.objects;

import by.jackraidenph.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.jackraidenph.dragonsurvival.common.capability.NBTInterface;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;

public class SkinPreset implements NBTInterface
{
	public HashMap<DragonLevel, SkinAgeGroup> skinAges = new HashMap<>();
	public double sizeMul = 1.0;
	
	public SkinPreset()
	{
		for(DragonLevel level : DragonLevel.values()){
			skinAges.computeIfAbsent(level, SkinAgeGroup::new);
		}
	}
	
	@Override
	public CompoundNBT writeNBT()
	{
		CompoundNBT nbt = new CompoundNBT();
		
		nbt.putDouble("sizeMul", sizeMul);
		
		for(DragonLevel level : DragonLevel.values()){
			nbt.put(level.name, skinAges.getOrDefault(level, new SkinAgeGroup(level)).writeNBT());
		}
		
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundNBT base)
	{
		sizeMul = base.getDouble("sizeMul");
		
		for(DragonLevel level : DragonLevel.values()){
			SkinAgeGroup ageGroup = new SkinAgeGroup(level);
			CompoundNBT nbt = base.getCompound(level.name);
			ageGroup.readNBT(nbt);
			skinAges.put(level, ageGroup);
		}
	}
	
	public static class SkinAgeGroup implements NBTInterface
	{
		public DragonLevel level;
		public HashMap<EnumSkinLayer, LayerSettings> layerSettings = new HashMap<>();
		
		public boolean wings = true;
		public boolean defaultSkin = false;
		
		public SkinAgeGroup(DragonLevel level)
		{
			this.level = level;
			
			for(EnumSkinLayer layer : EnumSkinLayer.values()){
				layerSettings.computeIfAbsent(layer, (s) -> layer.base ? new LayerSettings(layer.name().toLowerCase() + "_" + level.ordinal()) : new LayerSettings());
			}
		}
		
		@Override
		public CompoundNBT writeNBT()
		{
			CompoundNBT nbt = new CompoundNBT();
			
			nbt.putBoolean("wings", wings);
			nbt.putBoolean("defaultSkin", defaultSkin);
			
			for(EnumSkinLayer layer : EnumSkinLayer.values()){
				nbt.put(layer.name(), layerSettings.getOrDefault(layer, new LayerSettings()).writeNBT());
			}
			
			return nbt;
		}
		
		@Override
		public void readNBT(CompoundNBT base)
		{
			wings = base.getBoolean("wings");
			defaultSkin = base.getBoolean("defaultSkin");
			
			for(EnumSkinLayer layer : EnumSkinLayer.values()){
				LayerSettings ageGroup = new LayerSettings();
				CompoundNBT nbt = base.getCompound(layer.name());
				ageGroup.readNBT(nbt);
				layerSettings.put(layer, ageGroup);
			}
		}
	}
}
