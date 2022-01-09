package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomizationCycleButton extends Button
{
	public boolean next;
	public CustomizationLayer layer;
	private DragonCustomizationScreen screen;
	
	public CustomizationCycleButton(int p_i232255_1_, int p_i232255_2_, boolean next, CustomizationLayer layer, DragonCustomizationScreen parent)
	{
		super(p_i232255_1_, p_i232255_2_, 15, 15, null, (btn) -> {});
		this.next = next;
		this.layer = layer;
		this.screen = parent;
	}
	
	@Override
	public void onPress()
	{
		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
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
	
	@Override
	public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
	{
		Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);
		
		if(next) {
			if (isHovered()) {
				blit(stack, x, y, 66 / 2, 222 / 2, 11, 17, 128, 128);
			} else {
				blit(stack, x, y, 44 / 2, 222 / 2, 11, 17, 128, 128);
			}
		}else{
			if(isHovered()){
				blit(stack, x, y, 22 / 2, 222 / 2, 11, 17,128, 128);
			}else{
				blit(stack, x, y, 0, 222 / 2, 11, 17, 128, 128);
			}
		}
	}
}
