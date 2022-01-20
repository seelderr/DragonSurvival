package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;

public class SkinCap implements DragonCapability
{
	public boolean renderNewborn;
	public boolean renderYoung;
	public boolean renderAdult;
	
	public static final String defaultSkinValue = "None";
	
	public HashMap<DragonLevel, HashMap<CustomizationLayer, String>> playerSkinLayers = new HashMap();
	
	@Override
	public Tag writeNBT()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putBoolean("renderNewborn", renderNewborn);
		tag.putBoolean("renderYoung", renderYoung);
		tag.putBoolean("renderAdult", renderAdult);
		
		for(DragonLevel level : DragonLevel.values()) {
			for (CustomizationLayer layer : CustomizationLayer.values()) {
				tag.putString(level.name + "_skin_layer_" + layer.name(), playerSkinLayers.getOrDefault(level, new HashMap<>()).getOrDefault(layer, defaultSkinValue));
			}
		}
		
		return tag;
	}
	
	@Override
	public void readNBT(Tag base)
	{
		CompoundTag tag = (CompoundTag) base;

		renderNewborn = tag.getBoolean("renderNewborn");
		renderYoung = tag.getBoolean("renderYoung");
		renderAdult = tag.getBoolean("renderAdult");
		
		for(DragonLevel level : DragonLevel.values()) {
			for (CustomizationLayer layer : CustomizationLayer.values()) {
				playerSkinLayers.computeIfAbsent(level, (b) -> new HashMap<>());
				playerSkinLayers.get(level).put(layer, tag.getString(level.name + "_skin_layer_" + layer.name()));
			}
		}
	}
	
}
