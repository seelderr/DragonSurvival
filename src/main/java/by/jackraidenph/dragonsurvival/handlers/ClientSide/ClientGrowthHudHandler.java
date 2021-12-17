package by.jackraidenph.dragonsurvival.handlers.ClientSide;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.gui.DragonScreen;
import by.jackraidenph.dragonsurvival.handlers.DragonGrowthHandler;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Mod.EventBusSubscriber( Dist.CLIENT)
public class ClientGrowthHudHandler
{
	@SubscribeEvent
	public static void renderAbilityHud(RenderGameOverlayEvent.Post event) {
		PlayerEntity playerEntity = Minecraft.getInstance().player;
		
		if (playerEntity == null || !DragonStateProvider.isDragon(playerEntity) || playerEntity.isSpectator())
			return;
		
		DragonStateHandler handler = DragonStateProvider.getCap(playerEntity).orElse(null);
		ItemStack stack = playerEntity.getMainHandItem();
		
		if(handler == null || stack == null || stack.isEmpty()) return;
		
		if (event.getType() == ElementType.HOTBAR) {
			GL11.glPushMatrix();
			
			TextureManager textureManager = Minecraft.getInstance().getTextureManager();
			MainWindow window = Minecraft.getInstance().getWindow();
			
			int increment = DragonGrowthHandler.getIncrement(stack.getItem(), handler.getLevel());
			
			if(increment != 0 && (handler.getSize() < ConfigHandler.SERVER.maxGrowthSize.get() && increment > 0 || increment < 0 && handler.getSize() > 0)){
				float curSize = (float)handler.getSize();
				float nextSize = (float)(handler.getSize() + increment);
				float progress = 0;
				float nextProgess = 0;
				
				if (handler.getLevel() == DragonLevel.BABY) {
					progress = (float)((curSize - DragonLevel.BABY.size) / (DragonLevel.YOUNG.size - DragonLevel.BABY.size));
					nextProgess = (float)((nextSize - DragonLevel.BABY.size) / (DragonLevel.YOUNG.size - DragonLevel.BABY.size));
					
				} else if (handler.getLevel() == DragonLevel.YOUNG) {
					progress = (float)((curSize - DragonLevel.YOUNG.size) / (DragonLevel.ADULT.size - DragonLevel.YOUNG.size));
					nextProgess = (float)((nextSize - DragonLevel.YOUNG.size) / (DragonLevel.ADULT.size - DragonLevel.YOUNG.size));
					
				} else if (handler.getLevel() == DragonLevel.ADULT && handler.getSize() < 40) {
					progress = (float)((curSize - DragonLevel.ADULT.size) / (40 - DragonLevel.ADULT.size));
					nextProgess = (float)((nextSize - DragonLevel.ADULT.size) / (40 - DragonLevel.ADULT.size));
					
				} else if (handler.getLevel() == DragonLevel.ADULT && handler.getSize() >= 40) {
					progress = (float)((curSize - 40) / (ConfigHandler.SERVER.maxGrowthSize.get() - 40));
					nextProgess = (float)((nextSize - 40) / (ConfigHandler.SERVER.maxGrowthSize.get() - 40));
					
				}
				
				progress = Math.min(1.0f, progress);
				nextProgess = Math.min(1.0f, nextProgess);
				
				int radius = 17;
				int thickness = 5;
				int circleX = (window.getGuiScaledWidth() / 2) - (radius);
				int circleY = window.getGuiScaledHeight() - 90;
				
				circleX += ConfigHandler.CLIENT.growthXOffset.get();
				circleY += ConfigHandler.CLIENT.growthYOffset.get();
				
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				Color c = new Color(99, 99, 99);
				GL11.glColor4d(c.getRed()  / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, 1.0);
				DragonScreen.drawTexturedRing(circleX + radius, circleY + radius, radius - thickness, radius, 0, 0, 0, 128, 6, 1, 0);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glColor4d(1F, 1F, 1F, 1.0);
				
				if(nextProgess > progress) {
					int num = 1;
					double perSide = 1.0 / 6.0;
					if(nextProgess < progress + perSide){
						nextProgess = (float)(progress + perSide);
						num = 2;
					}
					
					textureManager.bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_" + num + ".png"));
					DragonScreen.drawTexturedCircle(circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, nextProgess, -0.5);
					
					textureManager.bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_" + handler.getType().name().toLowerCase() + ".png"));
					DragonScreen.drawTexturedCircle(circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, progress, -0.5);
				}else if(increment < 0){
					textureManager.bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_3.png"));
					DragonScreen.drawTexturedCircle(circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, progress, -0.5);
					
					textureManager.bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_" + handler.getType().name().toLowerCase() + ".png"));
					DragonScreen.drawTexturedCircle(circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, nextProgess, -0.5);
				}
				
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glLineWidth(4.0F);
				if(handler.growing){
					GL11.glColor4f(0F, 0F, 0F, 1F);
				}else{
					GL11.glColor4f(76 / 255F, 0F, 0F, 1F);
				}
				DragonScreen.drawSmoothCircle(circleX + radius, circleY + radius, radius, 6, 1, 0);
				
				GL11.glColor4d(c.getRed()  / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, 1.0);
				DragonScreen.drawSmoothCircle(circleX + radius, circleY + radius, radius - thickness, 6, 1, 0);
				GL11.glLineWidth(1.0F);
				
				c = c.brighter();
				GL11.glColor4d(c.getRed()  / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, 1.0);
				DragonScreen.drawTexturedRing(circleX + radius, circleY + radius, 0, radius - thickness, 0, 0,0,0, 6, 1, 0);
				
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glColor4d(1F, 1F, 1F, 1.0);
				
				textureManager.bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/growth_" + handler.getType().name().toLowerCase() + "_" + (handler.getLevel().ordinal() + 1) + ".png"));
				Screen.blit(event.getMatrixStack(), circleX + 6, circleY + 6, 0, 0, 20, 20, 20, 20);
			}
			
			GL11.glPopMatrix();
		}
	}
}
