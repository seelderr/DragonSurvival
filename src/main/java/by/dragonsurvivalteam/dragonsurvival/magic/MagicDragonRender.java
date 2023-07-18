package by.dragonsurvivalteam.dragonsurvival.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.ScreenUtils;
import software.bernie.geckolib3.core.util.Color;

import java.util.List;

public class MagicDragonRender{
	public static final ResourceLocation BARS = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/widget_bars.png");
	public static final ResourceLocation INVALID_ICON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/disabled.png");

	public static void drawAbilityHover(PoseStack stack, int xPos, int yPos, DragonAbility ability){
		int colorXPos = ability instanceof ActiveDragonAbility ? 0 : ability instanceof InnateDragonAbility ? 20 : 0;
		int colorYPos = ability instanceof ActiveDragonAbility ? 20 : 0;

		FormattedText desc = ability.getDescription();

		if(ability.getInfo().size() > 0){
			desc = FormattedText.composite(desc, Component.empty().append("\n\n"));
		}

		List<FormattedCharSequence> description = Minecraft.getInstance().font.split(desc, 150 - 7);

		if(!description.isEmpty()){
			if(ability.getInfo().size() > 0){
				FormattedText textContents = Component.empty();

				for(Component component : ability.getInfo()){
					textContents = FormattedText.composite(textContents, Component.empty().append("\n"));
					textContents = FormattedText.composite(textContents, component);
				}

				int extraWidth1 = (int)(150 / 1.25);
				List<FormattedCharSequence> text = Minecraft.getInstance().font.split(textContents, extraWidth1 - 5);
				int longest = text.stream().map(s->Minecraft.getInstance().font.width(s) + 20).max(Integer::compareTo).orElse(0);
				int extraWidth = Math.min(longest, extraWidth1);

				ScreenUtils.blitWithBorder(stack, BARS, xPos - (Screen.hasShiftDown() ? extraWidth : 10), yPos + 3, 40, 20, Screen.hasShiftDown() ? extraWidth : 15, Screen.hasShiftDown() ? Math.min(27 + text.size() * 9, 35 + 24 + description.size() * 9 - 10) : 27 + text.size() * 9, 20, 20, 3,  (float)0);
				ScreenUtils.blitWithBorder(stack, BARS, xPos - (Screen.hasShiftDown() ? extraWidth : 10) + 3, yPos + 9, colorXPos, colorYPos, Screen.hasShiftDown() ? extraWidth : 15, 20, 20, 20, 3, 0);

				if(Screen.hasShiftDown()){
					Gui.drawString(stack, Minecraft.getInstance().font, Component.translatable("ds.skill.info"), xPos - extraWidth + 10, yPos + 15, -1);

					for(int k1 = 0; k1 < text.size(); ++k1)
						Minecraft.getInstance().font.draw(stack, text.get(k1), xPos - extraWidth + 5, yPos + 5 + 18 + k1 * 9, -5592406);
				}
			}

			ScreenUtils.blitWithBorder(stack, BARS, xPos - 2, yPos - 4, 40, 20, 150 + 5, 35 + 24 + description.size() * 9, 20, 20, 3, 3, 3, 3, (float)0);
		}

		ScreenUtils.blitWithBorder(stack, BARS, xPos, yPos + 3, colorXPos, colorYPos, 150, 20, 20, 20, 3, 0);
		ScreenUtils.blitWithBorder(stack, BARS, xPos, yPos, 0, 100, 26, 26, 24, 24, 3, 0);

		String skillType = ability instanceof ActiveDragonAbility ? "active" : ability instanceof InnateDragonAbility ? "innate" : ability instanceof PassiveDragonAbility ? "passive" : null;

		if(skillType != null){
			Color c = ability instanceof ActiveDragonAbility ? Color.ofRGB(200, 143, 31) : ability instanceof InnateDragonAbility ? Color.ofRGB(150, 56, 175) : Color.ofRGB(127, 145, 46);
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, Component.translatable("ds.skill.type." + skillType), xPos + 150 / 2, yPos + 30, c.getColor());
		}

		if(ability.getMaxLevel() > 1){
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, Component.empty().append(ability.getLevel() + "/" + ability.getMaxLevel()), xPos + 150 - 18, yPos + 9, -1);
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, ability.getTitle(), xPos + 150 / 2, yPos + 9, -1);
		}else{
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, ability.getTitle(), xPos + 150 / 2 + 10, yPos + 9, -1);
		}

		for(int k1 = 0; k1 < description.size(); ++k1)
			Minecraft.getInstance().font.draw(stack, description.get(k1), (float)(xPos + 5), (float)(yPos + 47 + k1 * 9), -5592406);

		if(ability.getInfo().size() > 0)
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, Component.translatable("ds.skill.info.hold_shift").withStyle(ChatFormatting.DARK_GRAY), xPos + 150 / 2, yPos + 47 + (description.size() - 1) * 9, 0);

		RenderSystem.setShaderTexture(0, ability.getIcon());
		GuiComponent.blit(stack, xPos + 5, yPos + 5, 0, 0, 16, 16, 16, 16);

		if(ability.isDisabled()){
			RenderSystem.enableBlend();
			RenderSystem.setShaderTexture(0, INVALID_ICON);
			GuiComponent.blit(stack, xPos + 5, yPos + 5, 0, 0, 16, 16, 16, 16);
			RenderSystem.disableBlend();
		}
	}
}