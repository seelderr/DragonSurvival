package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.settings.ClientSettingsScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.awt.*;
import java.util.Locale;
import java.util.function.Consumer;

public class DropDownButton extends ExtendedButton
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	
	public Enum value;
	private Consumer<Enum> setter;
	private boolean toggled;
	
	private static final int maxItems = 4;
	
	public double scroll;
	
	ITextComponent text;
	
	public DropDownButton(Enum curValue, int x, int y, int xSize, int ySize, Consumer<Enum> setter)
	{
		super(x, y, xSize, ySize, null, null);
		this.value = curValue;
		this.setter = setter;
		updateMessage();
	}
	
	public void updateMessage(){
		setMessage(new StringTextComponent(value.name().substring(0, 1).toUpperCase(Locale.ROOT) + value.name().substring(1).toLowerCase(Locale.ROOT)));
	}
	
	@Override
	public ITextComponent getMessage()
	{
		return text;
	}
	
	public void setMessage(ITextComponent pMessage) {
		this.text = pMessage;
	}
	
	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
	{
		if (this.visible)
		{
			Minecraft mc = Minecraft.getInstance();
			this.isHovered = isMouseOver(mouseX, mouseY);
			int k = this.getYImage(this.isHovered());
			GuiUtils.drawContinuousTexturedBox(mStack, WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
			this.renderBg(mStack, mc, mouseX, mouseY);
			
			ITextComponent buttonText = this.getMessage();
			int strWidth = mc.font.width(buttonText);
			int ellipsisWidth = mc.font.width("...");
			
			if (strWidth > width - 6 && strWidth > ellipsisWidth)
				buttonText = new StringTextComponent(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");
			
			drawCenteredString(mStack, mc.font, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, getFGColor());
		}
		
		
		if(Minecraft.getInstance().screen instanceof ClientSettingsScreen){
			ClientSettingsScreen settingsScreen = (ClientSettingsScreen)Minecraft.getInstance().screen;
			this.isHovered = isMouseOver(mouseX, mouseY);
			
			if(!isHovered && toggled){
				toggled = false;
				settingsScreen.hoveredButton = null;
				scroll = 0;
				
			}else if(isHovered){
				if(toggled){
					if(settingsScreen.hoveredButton != this){
						settingsScreen.hoveredButton = this;
					}
				}else{
					if(settingsScreen.hoveredButton == this){
						settingsScreen.hoveredButton = null;
					}
				}
			}
		}
	}
	
	public void renderPost(MatrixStack mStack, int mouseX, int mouseY, float partial){
		if(toggled){
			Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
			GuiUtils.drawContinuousTexturedBox(mStack, x, y + height, 0, 0, width, Math.min(maxItems, value.getDeclaringClass().getEnumConstants().length) * height, 32, 32, 10, 0);
		
			//int maxElement = (int)(Math.min(1, scroll) * value.getDeclaringClass().getEnumConstants().length);
			{
				int maxElement = (int)(scroll / 20);
				int renderNum = 0;
				
				for (int i1 = Math.max(0, maxElement-maxItems); i1 < Math.min(Math.max(maxElement, maxItems), value.getDeclaringClass().getEnumConstants().length); i1++) {
					Enum en = (Enum)value.getDeclaringClass().getEnumConstants()[i1];
					
					int startY = y + (height);
					int level = renderNum * (height);
					
					int color = new Color(0.1F, 0.1F, 0.1F, 1F).getRGB();
					
					if (renderNum % 2 == 0) {
						color = new Color(0.2F, 0.2F, 0.2F, 1F).getRGB();
					}
					
					if (value == en) {
						color = new Color(0.3F, 0.3F, 0.3F, 1F).getRGB();
					}
					
					if (mouseX >= x + 2 && mouseY >= startY + level + 2 - (renderNum > 0 ? 4 : 0) && mouseX < x + width - 2 && mouseY < startY + height + level - 2) {
						color = new Color(0.4F, 0.4F, 0.4F, 1F).getRGB();
					}
					
					AbstractGui.fill(mStack, x + 2, startY + level + 2 - (renderNum > 0 ? 4 : 0), x + width - 2, startY + height + level - 2, color);
					
					StringTextComponent text = new StringTextComponent(en.name().substring(0, 1).toUpperCase(Locale.ROOT) + en.name().substring(1).toLowerCase(Locale.ROOT));
					if (!text.getString().isEmpty()) {
						Minecraft.getInstance().font.drawShadow(mStack, new StringTextComponent(Minecraft.getInstance().font.substrByWidth(text, width - 4).getString()), x + 5, y + 5 + height + (renderNum * height), value == en ? DyeColor.WHITE.getTextColor() : DyeColor.LIGHT_GRAY.getTextColor());
					}
					renderNum++;
				}
				
			}
			int amount = Math.min(maxItems, value.getDeclaringClass().getEnumConstants().length);
			
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuilder();
			
			float maxPosition = (value.getDeclaringClass().getEnumConstants().length) * height;
			float maxScroll = Math.max(0, maxPosition);
			
			scroll = MathHelper.clamp(scroll, 0, maxScroll);
			
			int y0 = y + height + 2;
			int y1 = (y + height + (amount * height)) - 2;
			int x0 = x;
			int x1 = x + width;
			
			int i = x1 - 8;
			int j = i + 6;
			int k1 = (int)maxScroll;
			if (k1 > 0) {
				RenderSystem.disableTexture();
				int l1 = (int)((float)((y1 - y0) * (y1 - y0)) / maxPosition);
				l1 = MathHelper.clamp(l1, 32, y1 - y0 - 8);
				int i2 = (int)scroll * (y1 - y0 - l1) / k1 + y0;
				if (i2 < y0) {
					i2 = y0;
				}
				
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				bufferbuilder.vertex((double)i, (double)y1, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex((double)j, (double)y1, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex((double)j, (double)y0, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex((double)i, (double)y0, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex((double)i, (double)(i2 + l1), 0.0D).uv(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex((double)j, (double)(i2 + l1), 0.0D).uv(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex((double)j, (double)i2, 0.0D).uv(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex((double)i, (double)i2, 0.0D).uv(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex((double)i, (double)(i2 + l1 - 1), 0.0D).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex((double)(j - 1), (double)(i2 + l1 - 1), 0.0D).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex((double)(j - 1), (double)i2, 0.0D).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex((double)i, (double)i2, 0.0D).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
				tessellator.end();
				
				RenderSystem.enableTexture();
			}
		}
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		int maxHeight = y + height;
		
		if(toggled){
			maxHeight += Math.min(maxItems, value.getDeclaringClass().getEnumConstants().length) * height;
		}
		
		return this.active && this.visible && pMouseX >= (double)this.x && pMouseY >= (double)this.y && pMouseX < (double)(this.x + this.width) && pMouseY < maxHeight;
	}
	
	protected boolean clicked(double pMouseX, double pMouseY) {
		return isMouseOver(pMouseX, pMouseY);
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		if(toggled){
			int maxElement = (int)(scroll / 20);
			int renderNum = 0;
			
			for (int i1 = Math.max(0, maxElement-maxItems); i1 < Math.min(Math.max(maxElement, maxItems), value.getDeclaringClass().getEnumConstants().length); i1++) {
				Enum en = (Enum)value.getDeclaringClass().getEnumConstants()[i1];
				
				int startY = y + (height);
				int level = renderNum * (height);
				
				if(pMouseX >= x + 2 && pMouseY >= startY + level + 2 - (renderNum > 0 ? 4 : 0) && pMouseX < x + width - 2 && pMouseY < startY + height + level - 2){
					setter.accept(en);
					value = en;
					toggled = false;
					updateMessage();
					return true;
				}
				
				renderNum++;
			}
		}
		
		
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
	{
		scroll -= pDelta;
		return super.mouseScrolled(pMouseX, pMouseY, pDelta);
	}
	
	@Override
	public void onPress()
	{
		toggled = !toggled;
		scroll = 0;
	}
}
