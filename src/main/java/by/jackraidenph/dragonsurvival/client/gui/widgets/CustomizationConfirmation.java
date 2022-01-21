package by.jackraidenph.dragonsurvival.client.gui.widgets;

import by.jackraidenph.dragonsurvival.client.gui.DragonAltarGUI;
import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.TooltipButton;
import by.jackraidenph.dragonsurvival.client.util.TextRenderUtil;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.client.gui.GuiUtils;

import java.util.List;

public class CustomizationConfirmation extends AbstractContainerEventHandler implements Widget
{
	private DragonCustomizationScreen screen;
	private AbstractWidget btn1, btn2;
	private int x, y, xSize, ySize;
	
	public CustomizationConfirmation(DragonCustomizationScreen screen, int x, int y, int xSize, int ySize)
	{
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		
		btn1 = new TooltipButton(x + (xSize / 2 - 30), y + ySize - 30, 20, 20, null, null, Minecraft.getInstance().font.split(new TranslatableComponent("ds.gui.customization.tooltip.done"), 200))
		{
			@Override
			public void render(PoseStack pPoseStack , int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.visible = active = screen.confirmation;
				super.render(pPoseStack , pMouseX, pMouseY, pPartialTicks);
			}
			
			@Override
			public void renderButton(PoseStack  mStack, int mouseX, int mouseY, float partial)
			{
				mStack.pushPose();
				mStack.translate(0, 0, 100);
				setMessage(new TextComponent(""));
				super.renderButton(mStack, mouseX, mouseY, partial);
				setMessage(new TextComponent("_confirm_"));
				RenderSystem.setShaderTexture(0, DragonAltarGUI.CONFIRM_BUTTON);
				blit(mStack, x + 1, y, 0, 0, 20, 20, 20, 20);
				mStack.popPose();
			}
			
			@Override
			public void onPress()
			{
				screen.confirm();
			}
		};
		
		btn2 = new TooltipButton(x + (xSize / 2 + 30), y + ySize - 30, 20, 20, null, null, Minecraft.getInstance().font.split(new TranslatableComponent("ds.gui.customization.tooltip.cancel"), 200))
		{
			@Override
			public void render(PoseStack  pPoseStack , int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.visible = active = screen.confirmation;
				super.render(pPoseStack , pMouseX, pMouseY, pPartialTicks);
			}
			
			@Override
			public void renderButton(PoseStack  mStack, int mouseX, int mouseY, float partial)
			{
				mStack.pushPose();
				mStack.translate(0, 0, 100);
				setMessage(new TextComponent(""));
				super.renderButton(mStack, mouseX, mouseY, partial);
				setMessage(new TextComponent("_confirm_"));
				RenderSystem.setShaderTexture(0, DragonAltarGUI.CANCEL_BUTTON);
				blit(mStack, x, y, 0, 0, 20, 20, 20, 20);
				mStack.popPose();
			}
			
			
			@Override
			public void onPress()
			{
				screen.confirmation = false;
			}
		};

	}
	
	@Override
	public List<? extends GuiEventListener> children()
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
	public void render(PoseStack  pPoseStack , int pMouseX, int pMouseY, float pPartialTicks)
	{
		if(screen.confirmation){
			RenderSystem.setShaderTexture(0, confirmationTexture);
			String key = "ds.gui.customization.confirm." +
			             (!ConfigHandler.SERVER.saveAllAbilities.get() && !ConfigHandler.SERVER.saveGrowthStage.get() ? "all"
		               : (ConfigHandler.SERVER.saveAllAbilities.get() && !ConfigHandler.SERVER.saveGrowthStage.get() ? "ability"
		               : !ConfigHandler.SERVER.saveAllAbilities.get() && ConfigHandler.SERVER.saveGrowthStage.get() ? "growth" : ""));
			String text = new TranslatableComponent(key).getString();
			GuiUtils.drawContinuousTexturedBox(pPoseStack, x, y, 0, 0, xSize, ySize, 160, 32, 4, 100);
			TextRenderUtil.drawCenteredScaledTextSplit(pPoseStack , x + xSize / 2, y + 5, 1.25f, text, DyeColor.WHITE.getTextColor(), xSize - 10, 101);
			
			
			btn1.render(pPoseStack , pMouseX, pMouseY, pPartialTicks);
			btn2.render(pPoseStack , pMouseX, pMouseY, pPartialTicks);
		}
	}
}
