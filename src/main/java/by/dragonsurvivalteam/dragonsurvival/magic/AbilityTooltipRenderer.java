package by.dragonsurvivalteam.dragonsurvival.magic;

import by.dragonsurvivalteam.dragonsurvival.client.gui.AbilityTooltipPositioner;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.util.DSColors;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;
import software.bernie.geckolib.util.Color;

import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class AbilityTooltipRenderer {
    @Translation(type = Translation.Type.MISC, comments = "Active Ability")
    private static final String ACTIVE = Translation.Type.ABILITY.wrap("general.active");

    @Translation(type = Translation.Type.MISC, comments = "Passive Ability")
    private static final String PASSIVE = Translation.Type.ABILITY.wrap("general.passive");

    @Translation(type = Translation.Type.MISC, comments = "Innate Ability")
    private static final String INNATE = Translation.Type.ABILITY.wrap("general.innate");

    @Translation(type = Translation.Type.MISC, comments = "Info")
    private static final String INFO = Translation.Type.ABILITY.wrap("general.info");

    @Translation(type = Translation.Type.MISC, comments = "Hold ‘Shift’ for info")
    private static final String INFO_SHIFT = Translation.Type.ABILITY.wrap("general.info_shift");

    public static final ResourceLocation INVALID_ICON = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/disabled.png");

    private static final ResourceLocation BARS = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/widget_bars.png");

    public static void drawAbilityHover(@NotNull final GuiGraphics guiGraphics, int x, int y, final DragonAbilityInstance ability) {
        int colorXPos = 0;
        int colorYPos = !ability.isPassive() ? 20 : 0;

        FormattedText rawDescription = Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(ability.location().getNamespace(), ability.location().getPath()));

        List<Component> info = ability.getInfo(Minecraft.getInstance().player);
        if(!info.isEmpty()) {
            rawDescription = FormattedText.composite(rawDescription, Component.empty().append("\n\n"));
        }

        if(ability.upgradeType().isPresent() && ability.upgradeType().get() == Upgrade.Type.PASSIVE_LEVEL && ability.level() != ability.getMaxLevel()) {
            int nextUpgradeLevel = (int) ability.value().upgrade().get().experienceOrLevelCost().calculate(ability.level() + 1);
            rawDescription = FormattedText.composite(rawDescription, Component.translatable(LangKey.ABILITY_LEVEL_AUTO_UPGRADE, nextUpgradeLevel).withColor(Color.GREEN.getColor()));
            rawDescription = FormattedText.composite(rawDescription, Component.empty().append("\n\n"));
        } else if(ability.upgradeType().isPresent() && ability.upgradeType().get() == Upgrade.Type.PASSIVE_GROWTH && ability.level() != ability.getMaxLevel()) {
            int nextUpgradeSize = (int) ability.value().upgrade().get().experienceOrLevelCost().calculate(ability.level() + 1);
            rawDescription = FormattedText.composite(rawDescription, Component.translatable(LangKey.ABILITY_GROWTH_AUTO_UPGRADE, nextUpgradeSize).withColor(Color.GREEN.getColor()));
            rawDescription = FormattedText.composite(rawDescription, Component.empty().append("\n\n"));
        }

        List<FormattedCharSequence> description = Minecraft.getInstance().font.split(rawDescription, 150 - 7);

        FormattedText textContents = Component.empty();
        for (Component component : info) {
            textContents = FormattedText.composite(textContents, Component.empty().append("\n"));
            textContents = FormattedText.composite(textContents, component);
        }

        int extraWidth1 = (int) (150 / 1.25);
        List<FormattedCharSequence> text = Minecraft.getInstance().font.split(textContents, extraWidth1 - 5);
        int longest = text.stream().map(s -> Minecraft.getInstance().font.width(s) + 20).max(Integer::compareTo).orElse(0);
        int extraWidth = Math.min(longest, extraWidth1);

        int backgroundWidth = 150 + 5;
        int backgroundHeight = 35 + 24 + description.size() * 9;
        int sideWidth = Screen.hasShiftDown() ? extraWidth : 15;
        int sideHeight = Screen.hasShiftDown() ? 27 + text.size() * 9 : backgroundHeight - 10;
        ClientTooltipPositioner positioner = new AbilityTooltipPositioner(Screen.hasShiftDown() ? sideWidth : 0);
        Vector2ic position = positioner.positionTooltip(guiGraphics.guiWidth(), guiGraphics.guiHeight(), x, y, backgroundWidth, Math.max(sideHeight, backgroundHeight));
        int trueX = position.x();
        int trueY = position.y();

        if(!info.isEmpty()) {
            // Backing for info tab
            guiGraphics.blitWithBorder(BARS, trueX - (Screen.hasShiftDown() ? extraWidth : 10), trueY + 3, 40, 20, sideWidth, sideHeight, 20, 20, 3);
            // Top bar for info tab
            guiGraphics.blitWithBorder(BARS, trueX - (Screen.hasShiftDown() ? extraWidth : 10) + 3, trueY + 9, colorXPos, colorYPos, Screen.hasShiftDown() ? extraWidth : 15, 20, 20, 20, 3);

            if (Screen.hasShiftDown()) {
                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable(INFO), trueX - extraWidth + 10, trueY + 15, -1);

                for (int k1 = 0; k1 < text.size(); ++k1) {
                    guiGraphics.drawString(Minecraft.getInstance().font, text.get(k1), trueX - extraWidth + 5, trueY + 5 + 18 + k1 * 9, DSColors.LIGHT_GRAY);
                }
            }
        }

        // Background
        guiGraphics.blitWithBorder(BARS, trueX - 2, trueY - 4, 40, 20, backgroundWidth, backgroundHeight, 20, 20, 3, 3, 3, 3);
        // Top bar
        guiGraphics.blitWithBorder(BARS, trueX, trueY + 3, colorXPos, colorYPos, 150, 20, 20, 20, 3);
        // Backing square for ability icon
        guiGraphics.blitWithBorder(BARS, trueX, trueY, 0, 100, 26, 26, 24, 24, 3);

        String translationKey;
        if(ability.isPassive()) {
            translationKey = PASSIVE;
        } else {
            translationKey = ACTIVE;
        }

        Color tooltipBackgroundColor;
        if(!ability.isPassive()) {
            tooltipBackgroundColor = Color.ofRGB(200, 143, 31);
        } else {
            tooltipBackgroundColor = Color.ofRGB(127, 145, 46);
        }

        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable(translationKey), trueX + 150 / 2, trueY + 30, tooltipBackgroundColor.getColor());

        if (ability.getMaxLevel() > DragonAbilityInstance.MIN_LEVEL) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.empty().append(ability.level() + "/" + ability.getMaxLevel()), trueX + 150 - 18, trueY + 9, -1);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, ability.getName(), trueX + 150 / 2, trueY + 9, -1);
        } else {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, ability.getName(), trueX + 150 / 2 + 10, trueY + 9, -1);
        }

        for (int k1 = 0; k1 < description.size(); ++k1) {
            guiGraphics.drawString(Minecraft.getInstance().font, description.get(k1), trueX + 5, trueY + 47 + k1 * 9, -5592406);
        }

        if (!info.isEmpty()) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable(INFO_SHIFT).withStyle(ChatFormatting.DARK_GRAY), trueX + 150 / 2, trueY + 47 + (description.size() - 1) * 9, 0);
        }

        guiGraphics.blit(ability.getIcon(), trueX + 5, trueY + 5, 0, 0, 16, 16, 16, 16);
    }
}