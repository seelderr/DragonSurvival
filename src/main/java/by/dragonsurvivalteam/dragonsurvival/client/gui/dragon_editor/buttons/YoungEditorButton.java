package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class YoungEditorButton extends Button{
	private final DragonEditorScreen dragonEditorScreen;

	public YoungEditorButton(DragonEditorScreen dragonEditorScreen){
		super(dragonEditorScreen.width / 2 - 60, dragonEditorScreen.guiTop - 30, 120, 20, new TranslationTextComponent("ds.level.young"), (btn) -> {
			dragonEditorScreen.level = DragonLevel.YOUNG;
			dragonEditorScreen.dragonRender.zoom = dragonEditorScreen.level.size;
			dragonEditorScreen.handler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
			dragonEditorScreen.update();
		});
		this.dragonEditorScreen = dragonEditorScreen;
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		this.active = this.visible = dragonEditorScreen.showUi;
		super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}

	@Override
	public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_){
		int j = isHovered || dragonEditorScreen.level == DragonLevel.YOUNG ? 16777215 : 10526880;
		TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}
}