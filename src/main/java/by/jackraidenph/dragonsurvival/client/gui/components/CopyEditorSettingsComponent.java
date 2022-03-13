package by.jackraidenph.dragonsurvival.client.gui.components;

import by.jackraidenph.dragonsurvival.client.gui.DragonAltarGUI;
import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.CopySettingsButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.SkinPreset.SkinAgeGroup;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;
import java.util.List;

public class CopyEditorSettingsComponent  extends FocusableGui implements IRenderable
{
	public boolean visible;
	
	private ExtendedButton confirm, cancel;
	private CheckboxButton newborn, young, adult;
	
	private CopySettingsButton btn;
	private DragonCustomizationScreen screen;
	private int x, y, xSize, ySize;
	
	public CopyEditorSettingsComponent(DragonCustomizationScreen screen, CopySettingsButton btn, int x, int y, int xSize, int ySize)
	{
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		this.btn = btn;
		
		confirm = new ExtendedButton(x + (xSize / 2) - 18, y + ySize - 15, 15, 15, StringTextComponent.EMPTY, null)
		{
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				mStack.pushPose();
				mStack.translate(0, 0, 100);
				setMessage(StringTextComponent.EMPTY);
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(DragonAltarGUI.CONFIRM_BUTTON);
				blit(mStack, x + 1, y, 0, 0, 15, 15, 15, 15);
				mStack.popPose();
				
				if (isHovered) {
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.customization.tooltip.done")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			@Override
			public void onPress()
			{
				SkinAgeGroup preset = screen.preset.skinAges.getOrDefault(screen.level, new SkinAgeGroup(screen.level));
				
				if(newborn.active && newborn.selected()){
					SkinAgeGroup ageGroup = new SkinAgeGroup(DragonLevel.BABY);
					ageGroup.readNBT(preset.writeNBT());
					screen.preset.skinAges.put(DragonLevel.BABY, ageGroup);
				}
				
				if(young.active && young.selected()){
					SkinAgeGroup ageGroup = new SkinAgeGroup(DragonLevel.YOUNG);
					ageGroup.readNBT(preset.writeNBT());
					screen.preset.skinAges.put(DragonLevel.YOUNG, ageGroup);
				}
				
				if(adult.active && adult.selected()){
					SkinAgeGroup ageGroup = new SkinAgeGroup(DragonLevel.ADULT);
					ageGroup.readNBT(preset.writeNBT());
					screen.preset.skinAges.put(DragonLevel.ADULT, ageGroup);
				}
				
				screen.update();
				btn.onPress();
			}
		};
		
		cancel = new ExtendedButton(x + (xSize / 2) + 3, y + ySize - 15, 15, 15, StringTextComponent.EMPTY, null)
		{
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				mStack.pushPose();
				mStack.translate(0, 0, 100);
				setMessage(StringTextComponent.EMPTY);
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(DragonAltarGUI.CANCEL_BUTTON);
				blit(mStack, x, y, 0, 0, 15, 15, 15, 15);
				mStack.popPose();
				
				if (isHovered) {
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.customization.tooltip.cancel")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			
			@Override
			public void onPress()
			{
				btn.onPress();
			}
		};
		
		newborn = new CheckboxButton(x + 3, y + 10, xSize - 5, 10, new TranslationTextComponent("ds.level.newborn"), false){
			final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
			
			@Override
			public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				active = screen.level != DragonLevel.BABY;
				pMatrixStack.pushPose();
				pMatrixStack.translate(0,0,100);
				Minecraft minecraft = Minecraft.getInstance();
				minecraft.getTextureManager().bind(TEXTURE);
				RenderSystem.enableDepthTest();
				FontRenderer fontrenderer = minecraft.font;
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				blit(pMatrixStack, this.x, this.y, this.isHovered() || this.isFocused() ? 10.0F : 0.0F, !active || this.selected() ? 10.0F : 0.0F, 10, this.height, 64/2, 64/2);
				this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);
				drawString(pMatrixStack, fontrenderer, active ? getMessage() : ((TranslationTextComponent)this.getMessage()).withStyle(TextFormatting.DARK_GRAY), this.x + 14, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
				pMatrixStack.popPose();
			}
		};
		
		young = new CheckboxButton(x + 3, y + 22, xSize - 5, 10, new TranslationTextComponent("ds.level.young"), false){
			final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
			
			@Override
			public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				active = screen.level != DragonLevel.YOUNG;
				pMatrixStack.pushPose();
				pMatrixStack.translate(0,0,100);
				Minecraft minecraft = Minecraft.getInstance();
				minecraft.getTextureManager().bind(TEXTURE);
				RenderSystem.enableDepthTest();
				FontRenderer fontrenderer = minecraft.font;
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				blit(pMatrixStack, this.x, this.y, this.isHovered() || this.isFocused() ? 10.0F : 0.0F, !active || this.selected() ? 10.0F : 0.0F, 10, this.height, 64/2, 64/2);
				this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);
				drawString(pMatrixStack, fontrenderer, active ? getMessage() : ((TranslationTextComponent)this.getMessage()).withStyle(TextFormatting.DARK_GRAY), this.x + 14, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
				pMatrixStack.popPose();
			}
		};
		
		adult = new CheckboxButton(x + 3, y + 34, xSize - 5, 10, new TranslationTextComponent("ds.level.adult"), false){
			final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
			
			@Override
			public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				active = screen.level != DragonLevel.ADULT;
				pMatrixStack.pushPose();
				pMatrixStack.translate(0,0,100);
				Minecraft minecraft = Minecraft.getInstance();
				minecraft.getTextureManager().bind(TEXTURE);
				RenderSystem.enableDepthTest();
				FontRenderer fontrenderer = minecraft.font;
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				blit(pMatrixStack, this.x, this.y, this.isHovered() || this.isFocused() ? 10.0F : 0.0F, !active || this.selected() ? 10.0F : 0.0F, 10, this.height, 64/2, 64/2);
				this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);
				drawString(pMatrixStack, fontrenderer, active ? getMessage() : ((TranslationTextComponent)this.getMessage()).withStyle(TextFormatting.DARK_GRAY), this.x + 14, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
				pMatrixStack.popPose();
			}
		};
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		return visible && pMouseY >= (double)this.y-3 && pMouseY <= (double)this.y + ySize+3 && pMouseX >= (double)this.x && pMouseX <= (double)this.x + xSize;
	}
	
	@Override
	public List<? extends IGuiEventListener> children()
	{
		return ImmutableList.of(confirm, cancel, newborn, young, adult);
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);
		confirm.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		cancel.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		newborn.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		young.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		adult.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		AbstractGui.drawCenteredString(pMatrixStack, Minecraft.getInstance().font, new TranslationTextComponent("ds.gui.customization.copy_to"), x + (xSize / 2), y + 1, 14737632);
		
	}
}
