package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.DragonEditorRegistry;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.DyeColor;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class DragonEditorSlotButton extends Button{
	public int num;
	private final DragonEditorScreen screen;

	public DragonEditorSlotButton(int p_i232255_1_, int p_i232255_2_, int num, DragonEditorScreen parent){
		super(p_i232255_1_, p_i232255_2_, 12, 12, null, (btn) -> {});
		this.num = num;
		this.screen = parent;
	}

	@Override
	public void onPress(){
		DragonEditorRegistry.savedCustomizations.skinPresets.computeIfAbsent(screen.type, (t) -> new HashMap<>());
		DragonEditorRegistry.savedCustomizations.skinPresets.get(screen.type).put(screen.currentSelected, screen.preset);

		screen.currentSelected = num - 1;
		screen.update();
		screen.handler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
	}

	@Override
	public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_){
		if(screen.currentSelected == (num - 1)){
			AbstractGui.fill(stack, x, y, x + this.width, y + this.height, new Color(1, 1, 1, isHovered ? 0.95F : 0.75F).getRGB());
			AbstractGui.fill(stack, x + 1, y + 1, x + this.width - 1, y + this.height - 1, new Color(0.05F, 0.05F, 0.05F, isHovered ? 0.95F : 0.75F).getRGB());
		}
		TextRenderUtil.drawScaledText(stack, x + 2.5f, y + 1f, 1.5F, Integer.toString(num), DyeColor.WHITE.getTextColor());
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		this.active = this.visible = screen.showUi;
		super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}