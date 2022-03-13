package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.CustomizationRegistry;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.jackraidenph.dragonsurvival.client.util.TextRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.DyeColor;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CustomizationSlotButton extends Button
{
	public int num;
	private DragonCustomizationScreen screen;
	
	public CustomizationSlotButton(int p_i232255_1_, int p_i232255_2_, int num, DragonCustomizationScreen parent)
	{
		super(p_i232255_1_, p_i232255_2_, 12, 12, null, (btn) -> {});
		this.num = num;
		this.screen = parent;
	}
	
	@Override
	public void onPress()
	{
		CustomizationRegistry.savedCustomizations.skinPresets.computeIfAbsent(screen.type, (t) -> new HashMap<>());
		CustomizationRegistry.savedCustomizations.skinPresets.get(screen.type).put(screen.currentSelected, screen.preset);
		
		screen.currentSelected = num - 1;
		screen.update();
		screen.handler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
	}
	
	@Override
	public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
	{
		if(screen.currentSelected == (num - 1)){
			AbstractGui.fill(stack, x, y, x + this.width, y + this.height, new Color(1, 1, 1, isHovered ? 0.95F : 0.75F).getRGB());
			AbstractGui.fill(stack, x+1, y+1, x + this.width-1, y + this.height-1, new Color(0.05F, 0.05F, 0.05F, isHovered ? 0.95F : 0.75F).getRGB());
		}
		TextRenderUtil.drawScaledText(stack, x + 2.5f, y + 1f, 1.5F, Integer.toString(num), DyeColor.WHITE.getTextColor());
	}
}
