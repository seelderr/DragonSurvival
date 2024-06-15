package by.dragonsurvivalteam.dragonsurvival.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.util.Color;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class MagicDragonRender{
	public static final ResourceLocation BARS = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/widget_bars.png");
	public static final ResourceLocation INVALID_ICON = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/disabled.png");

	public static void drawAbilityHover(@NotNull final GuiGraphics guiGraphics, int x, int y, final DragonAbility ability) {
		int colorXPos = ability instanceof ActiveDragonAbility ? 0 : ability instanceof InnateDragonAbility ? 20 : 0;
		int colorYPos = ability instanceof ActiveDragonAbility ? 20 : 0;

		FormattedText desc = ability.getDescription();

		if (!ability.getInfo().isEmpty()) {
			desc = FormattedText.composite(desc, Component.empty().append("\n\n"));
		}

		List<FormattedCharSequence> description = Minecraft.getInstance().font.split(desc, 150 - 7);

		if(!description.isEmpty()){
			if(!ability.getInfo().isEmpty()){
				FormattedText textContents = Component.empty();

				for(Component component : ability.getInfo()){
					textContents = FormattedText.composite(textContents, Component.empty().append("\n"));
					textContents = FormattedText.composite(textContents, component);
				}

				int extraWidth1 = (int)(150 / 1.25);
				List<FormattedCharSequence> text = Minecraft.getInstance().font.split(textContents, extraWidth1 - 5);
				int longest = text.stream().map(s->Minecraft.getInstance().font.width(s) + 20).max(Integer::compareTo).orElse(0);
				int extraWidth = Math.min(longest, extraWidth1);

				guiGraphics.blitWithBorder(BARS, x - (Screen.hasShiftDown() ? extraWidth : 10), y + 3, 40, 20, Screen.hasShiftDown() ? extraWidth : 15, Screen.hasShiftDown() ? Math.min(27 + text.size() * 9, 35 + 24 + description.size() * 9 - 10) : 27 + text.size() * 9, 20, 20, 3);
				guiGraphics.blitWithBorder(BARS, x - (Screen.hasShiftDown() ? extraWidth : 10) + 3, y + 9, colorXPos, colorYPos, Screen.hasShiftDown() ? extraWidth : 15, 20, 20, 20, 3);

				if (Screen.hasShiftDown()) {
					guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("ds.skill.info"), x - extraWidth + 10, y + 15, -1);

					for (int k1 = 0; k1 < text.size(); ++k1) {
						guiGraphics.drawString(Minecraft.getInstance().font, text.get(k1),x - extraWidth + 5, y + 5 + 18 + k1 * 9, -5592406);
					}
				}
			}

			guiGraphics.blitWithBorder(BARS, x - 2, y - 4, 40, 20, 150 + 5, 35 + 24 + description.size() * 9, 20, 20, 3, 3, 3, 3);
		}

		guiGraphics.blitWithBorder(BARS, x, y + 3, colorXPos, colorYPos, 150, 20, 20, 20, 3);
		guiGraphics.blitWithBorder(BARS, x, y, 0, 100, 26, 26, 24, 24, 3);

		String skillType = ability instanceof ActiveDragonAbility ? "active" : ability instanceof InnateDragonAbility ? "innate" : ability instanceof PassiveDragonAbility ? "passive" : null;

		if(skillType != null){
			Color c = ability instanceof ActiveDragonAbility ? Color.ofRGB(200, 143, 31) : ability instanceof InnateDragonAbility ? Color.ofRGB(150, 56, 175) : Color.ofRGB(127, 145, 46);
			guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("ds.skill.type." + skillType), x + 150 / 2, y + 30, c.getColor());
		}

		if(ability.getMaxLevel() > 1){
			guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.empty().append(ability.getLevel() + "/" + ability.getMaxLevel()), x + 150 - 18, y + 9, -1);
			guiGraphics.drawCenteredString(Minecraft.getInstance().font, ability.getTitle(), x + 150 / 2, y + 9, -1);
		}else{
			guiGraphics.drawCenteredString(Minecraft.getInstance().font, ability.getTitle(), x + 150 / 2 + 10, y + 9, -1);
		}

		for(int k1 = 0; k1 < description.size(); ++k1) {
			guiGraphics.drawString(Minecraft.getInstance().font, description.get(k1), x + 5, y + 47 + k1 * 9, -5592406);
		}

		if (!ability.getInfo().isEmpty()) {
			guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("ds.skill.info.hold_shift").withStyle(ChatFormatting.DARK_GRAY), x + 150 / 2, y + 47 + (description.size() - 1) * 9, 0);
		}

		guiGraphics.blit(ability.getIcon(), x + 5, y + 5, 0, 0, 16, 16, 16, 16);

		if (ability.isDisabled()) {
			RenderSystem.enableBlend();
			guiGraphics.blit(INVALID_ICON, x + 5, y + 5, 0, 0, 16, 16, 16, 16);
			RenderSystem.disableBlend();
		}
	}
}