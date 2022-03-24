package by.dragonsurvivalteam.dragonsurvival.client.gui.components;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;
import java.util.List;

public class DragonEditorConfirmComponent extends FocusableGui implements IRenderable{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_altar_warning.png");
	public boolean visible;
	private final DragonEditorScreen screen;
	private final Widget btn1;
	private final Widget btn2;
	private final int x;
	private final int y;
	private final int xSize;
	private final int ySize;


	public DragonEditorConfirmComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize){
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;

		btn1 = new ExtendedButton(x + 19, y + 133, 41, 21, DialogTexts.GUI_YES, null){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				drawCenteredString(mStack, Minecraft.getInstance().font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, getFGColor());

				if(isHovered){
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.tooltip.done")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}

			@Override
			public void onPress(){
				screen.confirm();
			}
		};

		btn2 = new ExtendedButton(x + 66, y + 133, 41, 21, DialogTexts.GUI_NO, null){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				drawCenteredString(mStack, Minecraft.getInstance().font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, getFGColor());

				if(isHovered){
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.tooltip.cancel")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}


			@Override
			public void onPress(){
				screen.confirmation = false;
			}
		};
	}

	@Override
	public List<? extends IGuiEventListener> children(){
		return ImmutableList.of(btn1, btn2);
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		this.fillGradient(pMatrixStack, 0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), -1072689136, -804253680);

		Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
		String key = "ds.gui.dragon_editor.confirm." + (!ConfigHandler.SERVER.saveAllAbilities.get() && !ConfigHandler.SERVER.saveGrowthStage.get() ? "all" : (ConfigHandler.SERVER.saveAllAbilities.get() && !ConfigHandler.SERVER.saveGrowthStage.get() ? "ability" : !ConfigHandler.SERVER.saveAllAbilities.get() && ConfigHandler.SERVER.saveGrowthStage.get() ? "growth" : ""));
		String text = new TranslationTextComponent(key).getString();
		blit(pMatrixStack, x, y, 0, 0, xSize, ySize);
		TextRenderUtil.drawCenteredScaledTextSplit(pMatrixStack, x + xSize / 2, y + 42, 1f, text, DyeColor.WHITE.getTextColor(), xSize - 10, 800);


		btn1.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		btn2.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}