package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.ColorPickerButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.CustomizationObject.Texture;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.LayerSettings;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayerUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.awt.Color;
import java.util.List;
import java.util.function.Supplier;

public class ColorSelectorComponent extends FocusableGui implements IRenderable
{
	public boolean visible;
	
	private ExtendedButton colorPicker;
	private CheckboxButton glowing;
	
	private DragonCustomizationScreen screen;
	private int x, y, xSize, ySize;
	private Supplier<LayerSettings> settings;
	
	public ColorSelectorComponent(DragonCustomizationScreen screen, int x, int y, int xSize, int ySize, EnumSkinLayer layer)
	{
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		
		settings = () -> screen.preset.skinAges.get(screen.level).layerSettings.get(layer);
		
		LayerSettings set = settings.get();
		Texture text = DragonCustomizationHandler.getSkin(FakeClientPlayerUtils.getFakePlayer(0, screen.handler), layer, set.selectedSkin, screen.handler.getType());
		
		glowing = new CheckboxButton(x + 3, y, xSize - 5, 10, new TranslationTextComponent("ds.gui.customization.glowing"), set.glowing){
			final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
			
			@Override
			public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
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
				blit(pMatrixStack, this.x, this.y, this.isHovered() || this.isFocused() ? 10.0F : 0.0F, this.selected() ? 10.0F : 0.0F, 10, this.height, 64/2, 64/2);
				this.renderBg(pMatrixStack, minecraft, pMouseX, pMouseY);
				drawString(pMatrixStack, fontrenderer, this.getMessage(), this.x + 14, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
				pMatrixStack.popPose();
			}
			
			@Override
			public void onPress()
			{
				super.onPress();
				settings.get().glowing = selected();
				screen.handler.getSkin().updateLayers.add(layer);
			}
		};
		
		Color defaultC = Color.decode(text.defaultColor);
		
		if(set.modifiedColor){
			defaultC = Color.getHSBColor(set.hue, set.saturation, set.brightness);
		}
		
		colorPicker = new ColorPickerButton(x + 3, y + 11, xSize - 5, ySize - 11, defaultC, (c) -> {
			float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
			
			settings.get().hue = hsb[0];
			settings.get().saturation = hsb[1];
			settings.get().brightness = hsb[2];
			settings.get().modifiedColor = true;
			
			this.screen.handler.getSkin().updateLayers.add(layer);
			screen.update();
		});
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		return visible && pMouseY >= (double)this.y-3 && pMouseY <= (double)this.y + ySize+3 && pMouseX >= (double)this.x && pMouseX <= (double)this.x + xSize;
	}
	
	@Override
	public List<? extends IGuiEventListener> children()
	{
		return ImmutableList.of(colorPicker, glowing);
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);
		colorPicker.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		glowing.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}
}
