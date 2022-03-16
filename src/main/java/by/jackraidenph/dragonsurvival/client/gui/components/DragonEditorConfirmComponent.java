package by.jackraidenph.dragonsurvival.client.gui.components;

import by.jackraidenph.dragonsurvival.client.gui.DragonAltarGUI;
import by.jackraidenph.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.jackraidenph.dragonsurvival.client.util.TextRenderUtil;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;
import java.util.List;

public class DragonEditorConfirmComponent extends FocusableGui implements IRenderable
{
	private DragonEditorScreen screen;
	private Widget btn1, btn2;
	private int x, y, xSize, ySize;
	public boolean visible;
	
	public DragonEditorConfirmComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize)
	{
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		
		btn1 = new ExtendedButton(x + (xSize / 2 - 30) - 20, y + ySize - 30, 20, 20, StringTextComponent.EMPTY, null)
		{
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				mStack.pushPose();
				mStack.translate(0, 0, 100);
				setMessage(StringTextComponent.EMPTY);
				super.renderButton(mStack, mouseX, mouseY, partial);
				setMessage(new StringTextComponent("_confirm_"));
				Minecraft.getInstance().getTextureManager().bind(DragonAltarGUI.CONFIRM_BUTTON);
				blit(mStack, x + 1, y, 0, 0, 20, 20, 20, 20);
				mStack.popPose();
				
				if (isHovered) {
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.tooltip.done")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			@Override
			public void onPress()
			{
				screen.confirm();
			}
		};
		
		btn2 = new ExtendedButton(x + (xSize / 2 + 30), y + ySize - 30, 20, 20, StringTextComponent.EMPTY, null)
		{
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				mStack.pushPose();
				mStack.translate(0, 0, 100);
				setMessage(StringTextComponent.EMPTY);
				super.renderButton(mStack, mouseX, mouseY, partial);
				setMessage(new StringTextComponent("_confirm_"));
				Minecraft.getInstance().getTextureManager().bind(DragonAltarGUI.CANCEL_BUTTON);
				blit(mStack, x, y, 0, 0, 20, 20, 20, 20);
				mStack.popPose();
				
				if (isHovered) {
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.tooltip.cancel")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			
			@Override
			public void onPress()
			{
				screen.confirmation = false;
			}
		};

	}
	
	@Override
	public List<? extends IGuiEventListener> children()
	{
		return ImmutableList.of(btn1, btn2);
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	private static ResourceLocation confirmationTexture = new ResourceLocation("textures/gui/toasts.png");
	
	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Minecraft.getInstance().getTextureManager().bind(confirmationTexture);
		String key = "ds.gui.dragon_editor.confirm." +
		             (!ConfigHandler.SERVER.saveAllAbilities.get() && !ConfigHandler.SERVER.saveGrowthStage.get() ? "all"
	               : (ConfigHandler.SERVER.saveAllAbilities.get() && !ConfigHandler.SERVER.saveGrowthStage.get() ? "ability"
	               : !ConfigHandler.SERVER.saveAllAbilities.get() && ConfigHandler.SERVER.saveGrowthStage.get() ? "growth" : ""));
		String text = new TranslationTextComponent(key).getString();
		GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, xSize, ySize, 160, 32, 4, 101);
		TextRenderUtil.drawCenteredScaledTextSplit(pMatrixStack, x + xSize / 2, y + 5, 1.25f, text, DyeColor.WHITE.getTextColor(), xSize - 10, 101);
		
		
		btn1.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		btn2.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}
