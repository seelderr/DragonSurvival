package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.MagicCap;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ChannelingCastAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ChargeCastAbility;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

import java.awt.*;

public class ClientMagicHUDHandler{
	public static final ResourceLocation widgetTextures = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/widgets.png");
	public static final ResourceLocation castBars = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/cast_bars.png");
	public static final ResourceLocation widgets = new ResourceLocation("textures/gui/widgets.png");

	@ConfigRange( min = -1000, max = 1000 )
	@ConfigOption( side = ConfigSide.CLIENT, category = {"ui",
	                                                     "magic"}, key = "casterBarXPos", comment = "Offset the x position of the cast bar in relation to its normal position" )
	public static Integer castbarXOffset = 0;

	@ConfigRange( min = -1000, max = 1000 )
	@ConfigOption( side = ConfigSide.CLIENT, category = {"ui",
	                                                     "magic"}, key = "casterBarYPos", comment = "Offset the y position of the cast bar in relation to its normal position" )
	public static Integer castbarYOffset = 0;

	@ConfigRange( min = -1000, max = 1000 )
	@ConfigOption( side = ConfigSide.CLIENT, category = {"ui",
	                                                     "magic"}, key = "skillbarXOffset", comment = "Offset the x position of the magic skill bar in relation to its normal position" )
	public static Integer skillbarXOffset = 0;

	@ConfigRange( min = -1000, max = 1000 )
	@ConfigOption( side = ConfigSide.CLIENT, category = {"ui",
	                                                     "magic"}, key = "skillbarYOffset", comment = "Offset the y position of the magic skill bar in relation to its normal position" )
	public static Integer skillbarYOffset = 0;

	@ConfigRange( min = -1000, max = 1000 )
	@ConfigOption( side = ConfigSide.CLIENT, category = {"ui",
	                                                     "magic"}, key = "manabarXOffset", comment = "Offset the x position of the mana bar in relation to its normal position" )
	public static Integer manabarXOffset = 0;

	@ConfigRange( min = -1000, max = 1000 )
	@ConfigOption( side = ConfigSide.CLIENT, category = {"ui",
	                                                     "magic"}, key = "manabarYOffset", comment = "Offset the y position of the mana bar in relation to its normal position" )
	public static Integer manabarYOffset = 0;

	public static void cancelExpBar(ForgeGui gui, GuiGraphics guiGraphics, float partialTicks, int width, int height){ // TODO :: Should have a different name
		Player playerEntity = Minecraft.getInstance().player;
		if(Minecraft.getInstance().options.hideGui || !gui.shouldDrawSurvivalElements() || !Minecraft.getInstance().gameMode.hasExperience())
			return;
		int x = width / 2 - 91;

		if(!ServerConfig.consumeEXPAsMana || !DragonUtils.isDragon(playerEntity)){
			VanillaGuiOverlay.EXPERIENCE_BAR.type().overlay().render(gui, guiGraphics, partialTicks, width, height);
			return;
		}

		DragonStateProvider.getCap(playerEntity).ifPresent(cap -> {
			ActiveDragonAbility ability = cap.getMagicData().getAbilityFromSlot(cap.getMagicData().getSelectedAbilitySlot());

			if (ability == null || ability.canConsumeMana(playerEntity)) {
				gui.renderExperienceBar(guiGraphics, x);
				return;
			}

			Window window = Minecraft.getInstance().getWindow();

			int screenWidth = window.getGuiScaledWidth();
			int screenHeight = window.getGuiScaledHeight();

			int i = Minecraft.getInstance().player.getXpNeededForNextLevel();
			if(i > 0){
				int j = 182;
				int k = (int)(Minecraft.getInstance().player.experienceProgress * 183.0F);
				int l = screenHeight - 32 + 3;
				guiGraphics.blit(widgetTextures, x, l, 0, 0, 164, 182, 5, 256, 256);
				if(k > 0)
					guiGraphics.blit(widgetTextures, x, l, 0, 0, 169, k, 5, 256, 256);
			}

			if(Minecraft.getInstance().player.experienceLevel > 0){
				String s = "" + Minecraft.getInstance().player.experienceLevel;
				int i1 = (screenWidth - Minecraft.getInstance().font.width(s)) / 2;
				int j1 = screenHeight - 31 - 4;
				guiGraphics.drawString(Minecraft.getInstance().font, s, (i1 + 1), j1, 0);
				guiGraphics.drawString(Minecraft.getInstance().font, s, (i1 - 1), j1, 0);
				guiGraphics.drawString(Minecraft.getInstance().font, s, i1, (j1 + 1), 0);
				guiGraphics.drawString(Minecraft.getInstance().font, s, i1, (j1 - 1), 0);
				guiGraphics.drawString(Minecraft.getInstance().font, s, i1, j1, new Color(243, 48, 59).getRGB());
			}
		});
	}

	private static int errorTicks;
	private static MutableComponent errorMessage;

	public static void castingError(MutableComponent component){
		if(ClientCastingHandler.hasCast) return;
		errorTicks = Functions.secondsToTicks(5);
		errorMessage = component;
	}

	public static void renderAbilityHud(final GuiGraphics guiGraphics, int width, int height){
		if (Minecraft.getInstance().options.hideGui) {
			return;
		}

		Player player = Minecraft.getInstance().player;

		if (player == null || player.isSpectator()) {
			return;
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);

		if (!handler.isDragon()) {
			return;
		}

		int sizeX = 20;
		int sizeY = 20;

		int i1 = width - sizeX * MagicCap.activeAbilitySlots - 20;
		int posX = i1;
		int posY = height - sizeY;

		posX += skillbarXOffset;
		posY += skillbarYOffset;

		if (handler.getMagicData().shouldRenderAbilities()) {
			guiGraphics.blit(widgets, posX, posY - 2, 0, 0, 0, 41, 22, 256, 256);
			guiGraphics.blit(widgets, posX + 41, posY - 2, 0, 141, 0, 41, 22, 256, 256);

			for (int x = 0; x < MagicCap.activeAbilitySlots; x++) {
				ActiveDragonAbility ability = handler.getMagicData().getAbilityFromSlot(x);

				if (ability != null && ability.getIcon() != null) {
					guiGraphics.blit(ability.getIcon(), posX + x * sizeX + 3, posY + 1, 0, 0, 16, 16, 16, 16);

					if (ability.getSkillCooldown() > 0 && ability.getCurrentCooldown() > 0 && ability.getSkillCooldown() != ability.getCurrentCooldown()) {
						float f = Mth.clamp((float) ability.getCurrentCooldown() / (float) ability.getSkillCooldown(), 0, 1);
						int boxX = posX + x * sizeX + 3;
						int boxY = posY + 1;
						int offset = 16 - (16 - (int) (f * 16));
						int color = new Color(0.15F, 0.15F, 0.15F, 0.75F).getRGB();
						int fColor = errorTicks > 0 ? new Color(1F, 0F, 0F, 0.75F).getRGB() : color;
						guiGraphics.fill(boxX, boxY, boxX + 16, boxY + offset, fColor);
					}
				}
			}

			guiGraphics.blit(widgets, posX + sizeX * handler.getMagicData().getSelectedAbilitySlot() - 1, posY - 3, 2, 0, 22, 24, 24, 256, 256);

			int maxMana = ManaHandler.getMaxMana(player);
			int curMana = ManaHandler.getCurrentMana(player);

			int manaX = i1;
			int manaY = height - sizeY;

			manaX += manabarXOffset;
			manaY += manabarYOffset;

			for (int i = 0; i < 1 + Math.ceil(maxMana / 10.0); i++)
				for (int x = 0; x < 10; x++) {
					int manaSlot = i * 10 + x;
					if (manaSlot < maxMana) {
						boolean goodCondi = ManaHandler.isPlayerInGoodConditions(player);
						int condiXPos = DragonUtils.isDragonType(handler, DragonTypes.SEA) ? 0 : DragonUtils.isDragonType(handler, DragonTypes.FOREST) ? 18 : 36;
						int xPos = curMana <= manaSlot ? goodCondi ? condiXPos + 72 : 54 : DragonUtils.isDragonType(handler, DragonTypes.SEA) ? 0 : DragonUtils.isDragonType(handler, DragonTypes.FOREST) ? 18 : 36;
						float rescale = 2.15F;
						guiGraphics.blit(widgetTextures, manaX + x * (int) (18 / rescale), manaY - 12 - i * ((int) (18 / rescale) + 1), xPos / rescale, 204 / rescale, (int) (18 / rescale), (int) (18 / rescale), (int) (256 / rescale), (int) (256 / rescale));
					}
				}
		}

		ActiveDragonAbility ability = handler.getMagicData().getAbilityFromSlot(handler.getMagicData().getSelectedAbilitySlot());

		int currentCastTime = -1, skillCastTime = -1;

		if (ability instanceof ChargeCastAbility ability1) {
			currentCastTime = ability1.getCastTime();
			skillCastTime = ability1.getSkillCastingTime();
		} else if (ability instanceof ChannelingCastAbility ability1) {
			currentCastTime = ability1.getChargeTime();
			skillCastTime = ability1.getSkillChargeTime();
		}

		if (handler.getMagicData().isCasting) {
			if (currentCastTime > 0 && skillCastTime != -1) {
				guiGraphics.pose().pushPose();
				guiGraphics.pose().scale(0.5F, 0.5F, 0);

				int yPos1 = DragonUtils.isDragonType(handler, DragonTypes.CAVE) ? 0 : DragonUtils.isDragonType(handler, DragonTypes.FOREST) ? 47 : 94;
				int yPos2 = DragonUtils.isDragonType(handler, DragonTypes.CAVE) ? 142 : DragonUtils.isDragonType(handler, DragonTypes.FOREST) ? 147 : 152;

				float perc = Math.min((float) currentCastTime / (float) skillCastTime, 1);

				int startX = width / 2 - 49 + castbarXOffset;
				int startY = height - 96 + castbarYOffset;

				guiGraphics.pose().translate(startX, startY, 0);

				guiGraphics.blit(castBars, startX, startY, 0, yPos1, 196, 47, 256, 256);
				guiGraphics.blit(castBars, startX + 2, startY + 41, 0, yPos2, (int) (191 * perc), 4, 256, 256);

				guiGraphics.blit(ability.getIcon(), startX + 78, startY + 3, 0, 0, 36, 36, 36, 36);

				guiGraphics.pose().popPose();
			}
		}

		if (errorTicks > 0) {
			guiGraphics.drawString(Minecraft.getInstance().font, errorMessage.getVisualOrderText(), (int) (width / 2f - Minecraft.getInstance().font.width(errorMessage) / 2f), height - 70, 0);
			errorTicks--;

			if (errorTicks <= 0) {
				errorMessage = Component.empty();
			}
		}
	}
}