package by.jackraidenph.dragonsurvival.common.capability.subcapabilities;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.common.capability.NBTInterface;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.HashSet;

public class SkinCap implements NBTInterface
{
	public boolean renderNewborn;
	public boolean renderYoung;
	public boolean renderAdult;
	
	public static final String defaultSkinValue = "None";
	
	public HashMap<DragonLevel, HashMap<CustomizationLayer, String>> playerSkinLayers = new HashMap();
	public HashMap<DragonLevel, HashMap<CustomizationLayer, Integer>> skinLayerHue = new HashMap();
	public HashSet<CustomizationLayer> hueChanged = new HashSet<>();
	
	@Override
	public CompoundNBT writeNBT()
	{
		CompoundNBT tag = new CompoundNBT();
		
		tag.putBoolean("renderNewborn", renderNewborn);
		tag.putBoolean("renderYoung", renderYoung);
		tag.putBoolean("renderAdult", renderAdult);
		
		for(DragonLevel level : DragonLevel.values()) {
			for (CustomizationLayer layer : CustomizationLayer.values()) {
				tag.putString(level.name + "_skin_layer_" + layer.name(), playerSkinLayers.getOrDefault(level, new HashMap<>()).getOrDefault(layer, defaultSkinValue));
			}
		}
		
		for(DragonLevel level : DragonLevel.values()) {
			for (CustomizationLayer layer : CustomizationLayer.values()) {
				tag.putInt(level.name + "_skin_layer_hue_" + layer.name(), skinLayerHue.getOrDefault(level, new HashMap<>()).getOrDefault(layer, 0));
			}
		}
		
		return tag;
	}
	
	@Override
	public void readNBT(CompoundNBT tag)
	{
		renderNewborn = tag.getBoolean("renderNewborn");
		renderYoung = tag.getBoolean("renderYoung");
		renderAdult = tag.getBoolean("renderAdult");
		
		for(DragonLevel level : DragonLevel.values()) {
			for (CustomizationLayer layer : CustomizationLayer.values()) {
				playerSkinLayers.computeIfAbsent(level, (b) -> new HashMap<>());
				playerSkinLayers.get(level).put(layer, tag.getString(level.name + "_skin_layer_" + layer.name()));
			}
		}
		
		for(DragonLevel level : DragonLevel.values()) {
			for (CustomizationLayer layer : CustomizationLayer.values()) {
				skinLayerHue.computeIfAbsent(level, (b) -> new HashMap<>());
				skinLayerHue.get(level).put(layer, tag.getInt(level.name + "_skin_layer_hue_" + layer.name()));
			}
		}
	}
}
