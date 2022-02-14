package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashMap;
import java.util.HashSet;

public class SkinCap implements DragonCapability
{
	public boolean renderNewborn;
	public boolean renderYoung;
	public boolean renderAdult;
	
	public static final String defaultSkinValue = "None";
	
	public HashMap<DragonLevel, HashMap<CustomizationLayer, String>> playerSkinLayers = new HashMap();
	public HashMap<DragonLevel, HashMap<CustomizationLayer, Double>> skinLayerHue = new HashMap();
	public HashSet<CustomizationLayer> hueChanged = new HashSet<>();
	
	@Override
	public INBT writeNBT(Capability<DragonStateHandler> capability, Direction side)
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
				tag.putDouble(level.name + "_skin_layer_hue_" + layer.name(), skinLayerHue.getOrDefault(level, new HashMap<>()).getOrDefault(layer, 0.0));
			}
		}
		
		return tag;
	}
	
	@Override
	public void readNBT(Capability<DragonStateHandler> capability, Direction side, INBT base)
	{
		CompoundNBT tag = (CompoundNBT) base;

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
				skinLayerHue.get(level).put(layer, tag.getDouble(level.name + "_skin_layer_hue_" + layer.name()));
			}
		}
	}
	
	@Override
	public void clone(DragonStateHandler oldCap)
	{
		renderNewborn = oldCap.getSkin().renderNewborn;
		renderYoung = oldCap.getSkin().renderYoung;
		renderAdult = oldCap.getSkin().renderAdult;
		playerSkinLayers = oldCap.getSkin().playerSkinLayers;
		skinLayerHue = oldCap.getSkin().skinLayerHue;
	}
}
