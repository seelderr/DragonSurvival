package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities.SkinCap;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomizationCycleButton extends ArrowButton
{
	public boolean next;
	public CustomizationLayer layer;
	private DragonCustomizationScreen screen;
	
	public CustomizationCycleButton(int p_i232255_1_, int p_i232255_2_, boolean next, CustomizationLayer layer, DragonCustomizationScreen parent)
	{
		super(p_i232255_1_, p_i232255_2_, 15, 17, next, (btn) -> {});
		this.next = next;
		this.layer = layer;
		this.screen = parent;
	}
	
	@Override
	public void onPress()
	{
		ArrayList<String> keys = DragonCustomizationHandler.getKeys(Minecraft.getInstance().player, layer);
		
		if(layer != CustomizationLayer.BASE) {
			keys.add(0, SkinCap.defaultSkinValue);
		}
		
		String currentKey = screen.map.getOrDefault(screen.level, new HashMap<>()).getOrDefault(layer, SkinCap.defaultSkinValue);
		int i = 0;
		
		for (String key : keys) {
			if (key.equals(currentKey) || key.equals("Skin") && currentKey.equals(SkinCap.defaultSkinValue)) {
				break;
			}
			i++;
		}
		
		screen.map.computeIfAbsent(screen.level, (b) -> new HashMap<>());
		
		if(next){
			if(i+1 >= keys.size()){
				screen.map.get(screen.level).put(layer, keys.get(0));
			}else if(i+1 <= keys.size()){
				screen.map.get(screen.level).put(layer, keys.get(i+1));
			}
		}else{
			if(i == 0){
				screen.map.get(screen.level).put(layer, keys.get(keys.size()-1));
			}else if(i-1 >= 0){
				screen.map.get(screen.level).put(layer, keys.get(i-1));
			}
		}
		
		screen.update();
	}
}
