package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonGrowthHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.awt.Color;

public class ClientGrowthHudHandler{
	public static void renderGrowth(ForgeIngameGui gui, PoseStack mStack, float partialTicks, int width, int height){
		Player playerEntity = Minecraft.getInstance().player;
		if(playerEntity == null || !DragonUtils.isDragon(playerEntity) || playerEntity.isSpectator()){
			return;
		}

		DragonStateHandler handler = DragonStateProvider.getCap(playerEntity).orElse(null);
		ItemStack stack = playerEntity.getMainHandItem();

		if(handler == null || stack.isEmpty()){
			return;
		}

		int increment = DragonGrowthHandler.getIncrement(stack.getItem(), handler.getLevel());

		if(increment != 0 && (handler.getSize() < ConfigHandler.SERVER.maxGrowthSize.get() && increment > 0 || increment < 0 && handler.getSize() >= (DragonLevel.BABY.size + 1))){
			float curSize = (float)handler.getSize();
			float nextSize = (float)(handler.getSize() + increment);
			float progress = 0;
			float nextProgess = 0;

			if(handler.getLevel() == DragonLevel.BABY){
				progress = (curSize - DragonLevel.BABY.size) / (DragonLevel.YOUNG.size - DragonLevel.BABY.size);
				nextProgess = (nextSize - DragonLevel.BABY.size) / (DragonLevel.YOUNG.size - DragonLevel.BABY.size);
			}else if(handler.getLevel() == DragonLevel.YOUNG){
				progress = (curSize - DragonLevel.YOUNG.size) / (DragonLevel.ADULT.size - DragonLevel.YOUNG.size);
				nextProgess = (nextSize - DragonLevel.YOUNG.size) / (DragonLevel.ADULT.size - DragonLevel.YOUNG.size);
			}else if(handler.getLevel() == DragonLevel.ADULT && handler.getSize() < 40){
				progress = (curSize - DragonLevel.ADULT.size) / (40 - DragonLevel.ADULT.size);
				nextProgess = (nextSize - DragonLevel.ADULT.size) / (40 - DragonLevel.ADULT.size);
			}else if(handler.getLevel() == DragonLevel.ADULT && handler.getSize() >= 40){
				progress = (float)((curSize - 40) / (ConfigHandler.SERVER.maxGrowthSize.get() - 40));
				nextProgess = (float)((nextSize - 40) / (ConfigHandler.SERVER.maxGrowthSize.get() - 40));
			}

			progress = Math.min(1.0f, progress);
			nextProgess = Math.min(1.0f, nextProgess);

			int radius = 17;
			int thickness = 5;
			int circleX = (width / 2) - (radius);
			int circleY = height - 90;

			circleX += ConfigHandler.CLIENT.growthXOffset.get();
			circleY += ConfigHandler.CLIENT.growthYOffset.get();

			RenderSystem.setShaderColor(0f, 0f, 0f, 1f);
			Color c = new Color(99, 99, 99);

			RenderSystem.setShaderColor(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
			RenderingUtils.drawTexturedRing(mStack, circleX + radius, circleY + radius, radius - thickness, radius, 0, 0, 0, 128, 6, 1, 0);
			RenderSystem.enableTexture();
			RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);

			if(nextProgess > progress){
				int num = 1;
				double perSide = 1.0 / 6.0;
				if(nextProgess < progress + perSide){
					nextProgess = (float)(progress + perSide);
					num = 2;
				}

				RenderSystem.setShaderTexture(0, new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_" + num + ".png"));
				RenderingUtils.drawTexturedCircle(mStack, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, nextProgess, -0.5);

				RenderSystem.setShaderTexture(0, new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_" + handler.getType().name().toLowerCase() + ".png"));
				RenderingUtils.drawTexturedCircle(mStack, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, progress, -0.5);
			}else if(increment < 0){
				RenderSystem.setShaderTexture(0, new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_3.png"));
				RenderingUtils.drawTexturedCircle(mStack, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, progress, -0.5);

				RenderSystem.setShaderTexture(0, new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_" + handler.getType().name().toLowerCase() + ".png"));
				RenderingUtils.drawTexturedCircle(mStack, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, nextProgess, -0.5);
			}


			RenderSystem.disableTexture();
			RenderSystem.lineWidth(4.0f);
			if(handler.growing){
				RenderSystem.setShaderColor(0F, 0F, 0F, 1F);
			}else{
				RenderSystem.setShaderColor(76 / 255F, 0F, 0F, 1F);
			}
			RenderingUtils.drawSmoothCircle(mStack, circleX + radius, circleY + radius, radius, 6, 1, 0);

			RenderSystem.setShaderColor(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
			RenderingUtils.drawSmoothCircle(mStack, circleX + radius, circleY + radius, radius - thickness, 6, 1, 0);
			RenderSystem.lineWidth(1.0F);

			c = c.brighter();
			RenderSystem.setShaderColor(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
			RenderingUtils.drawTexturedRing(mStack, circleX + radius, circleY + radius, 0, radius - thickness, 0, 0, 0, 0, 6, 1, 0);

			RenderSystem.enableTexture();
			RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);

			RenderSystem.setShaderTexture(0, new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/growth_" + handler.getType().name().toLowerCase() + "_" + (handler.getLevel().ordinal() + 1) + ".png"));
			Screen.blit(mStack, circleX + 6, circleY + 6, 0, 0, 20, 20, 20, 20);
		}
	}
}