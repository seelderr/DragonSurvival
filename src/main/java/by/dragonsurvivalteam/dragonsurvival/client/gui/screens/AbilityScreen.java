package by.dragonsurvivalteam.dragonsurvival.client.gui.screens;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.AbilityButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.LevelButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.TabButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ClickHoverButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.util.ExperienceUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class AbilityScreen extends Screen {
    @Translation(type = Translation.Type.MISC, comments = {
            "■ §6Active skills§r are used in combat.",
            "- §9Skill power§r scales off your current experience level. The higher your EXP level, the stronger your active skills.",
            "- §9Experience or mana§r points are used to cast spells.",
            "- §9Controls§r - check in-game Minecraft control settings! You can drag and drop skill icons around."
    })
    private static final String HELP_ACTIVE = Translation.Type.GUI.wrap("help.active_abilities");

    @Translation(type = Translation.Type.MISC, comments = {
            "■ §aPassive skills§r are upgraded by spending experience levels.",
            "- §9Mana§r - do not forget use the Source of Magic and Dragons Treats for an infinite supply of mana!",
            "- §9More information§r can be found on our Wiki and in our Discord. Check the Curseforge mod page."
    })
    private static final String HELP_PASSIVE = Translation.Type.GUI.wrap("help.passive_abilities");

    @Translation(type = Translation.Type.MISC, comments = {
            "■ §6Active skills§r are used in combat.",
            "- §9Skill power§r scales off your current experience level. The higher your EXP level, the stronger your active skills.",
            "- §9Experience or mana§r points are used to cast spells.",
            "- §9Controls§r - check in-game Minecraft control settings! You can drag and drop skill icons around.",
            "",
            "■ §aPassive skills§r are upgraded by spending experience levels.",
            "- §9Mana§r - do not forget use the Source of Magic and Dragons Treats for an infinite supply of mana!",
            "- §9More information§r can be found on our Wiki and in our Discord. Check the Curseforge mod page."
    })
    private static final String HELP_PASSIVE_ACTIVE = Translation.Type.GUI.wrap("help.passive_active_abilities");

    @Translation(type = Translation.Type.MISC, comments = {
            "■ §dAbility assignment§r - drag and drop §6Active skills§r to the §9hotbar§r.",
            "- The §9hotbar§r is used to quickly access your active skills."
    })
    private static final String HELP_ABILITY_ASSIGNMENT = Translation.Type.GUI.wrap("help.ability_assignment");

    @Translation(type = Translation.Type.MISC, comments = "■ §dInnate skills§r are a dragon's quirks, and represent the benefits and drawbacks of each dragon type.")
    private static final String HELP_INNATE = Translation.Type.GUI.wrap("help.innate_abilities");

    private static final ResourceLocation BACKGROUND_MAIN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/background_main.png");
    private static final ResourceLocation BACKGROUND_SIDE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/background_side.png");
    private static final int INNER_BACKGROUND_WIDTH = 185;
    private static final int BACKGROUND_BEZEL_WIDTH = 10;
    private static final ResourceLocation EXP_EMPTY = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/exp_empty.png");
    private static final ResourceLocation EXP_FULL = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/exp_full.png");
    private static final ResourceLocation LEFT_PANEL_ARROW_CLICK = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/addition_arrow_left_click.png");
    private static final ResourceLocation LEFT_PANEL_ARROW_HOVER = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/addition_arrow_left_hover.png");
    private static final ResourceLocation LEFT_PANEL_ARROW_MAIN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/addition_arrow_left_main.png");
    private static final ResourceLocation RIGHT_PANEL_ARROW_CLICK = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/addition_arrow_right_click.png");
    private static final ResourceLocation RIGHT_PANEL_ARROW_HOVER = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/addition_arrow_right_hover.png");
    private static final ResourceLocation RIGHT_PANEL_ARROW_MAIN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/addition_arrow_right_main.png");

    private static int ABILITIES_PER_COLUMN = 4;

    public Screen sourceScreen;
    public LevelButton hoveredLevelButton;

    private Holder<DragonType> type;
    private int guiLeft;
    private int guiTop;

    private boolean leftWindowOpen;
    private final List<AbstractWidget> leftWindowWidgets = new ArrayList<>();

    private boolean rightWindowOpen;
    private final List<AbstractWidget> rightWindowWidgets = new ArrayList<>();

    public AbilityScreen(Screen sourceScreen) {
        super(Component.empty().append("AbilityScreen")); // FIXME :: what is this component used for
        this.sourceScreen = sourceScreen;
    }

    public List<? extends GuiEventListener> widgetList() {
        return children();
    }

    @Override
    public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (minecraft == null || minecraft.player == null) {
            return;
        }

        this.renderBlurredBackground(partialTick);

        int startX = guiLeft + 25;
        int startY = guiTop - 28;

        if (leftWindowOpen) {
            guiGraphics.blit(BACKGROUND_SIDE, startX - 50, startY, 0, 0, 48, 203);
        }

        guiGraphics.blit(BACKGROUND_MAIN, startX, startY, 0, 0, 256, 256);

        if (rightWindowOpen) {
            guiGraphics.blit(BACKGROUND_SIDE, startX + 207, startY, 0, 0, 48, 203);
        }

        if (type != null) {
            // Draw XP bars
            float leftExpBarProgress = Math.min(1f, Math.min(0.5f, minecraft.player.experienceProgress) * 2);

            int barYPos = startY + 10;
            int leftBarX = startX + 10;
            int rightBarX = startX + 122;

            guiGraphics.blit(EXP_EMPTY, leftBarX, barYPos, 0, 0, 73, 6, 73, 6);
            guiGraphics.blit(EXP_EMPTY, rightBarX, barYPos, 0, 0, 73, 6, 73, 6);
            guiGraphics.blit(EXP_FULL, leftBarX, barYPos, 0, 0, (int) (73 * leftExpBarProgress), 6, 73, 6);

            if (minecraft.player.experienceProgress > 0.5) {
                float rightExpBarProgress = Math.min(1f, Math.min(0.5f, minecraft.player.experienceProgress - 0.5f) * 2);
                guiGraphics.blit(EXP_FULL, rightBarX, barYPos, 0, 0, (int) (73 * rightExpBarProgress), 6, 73, 6);
            }

            int experienceModification = hoveredLevelButton != null ? hoveredLevelButton.getExperienceModification() : 0;
            int newExperience = ExperienceUtils.getTotalExperience(minecraft.player) + experienceModification;
            int newLevel = Math.max(0, ExperienceUtils.getLevel(newExperience));

            if (experienceModification != 0) {
                // Used to show the new experience progress of the new level
                // The level difference itself is shown through the rendered level number
                float hoverProgress = (float) (newExperience - ExperienceUtils.getTotalExperience(newLevel)) / ExperienceUtils.getExperienceForLevel(newLevel + 1);
                float leftExpBarHoverProgress = Math.min(0.5f, hoverProgress) * 2;
                float rightExpBarHoverProgress = Math.min(0.5f, hoverProgress - leftExpBarHoverProgress / 2) * 2;

                if (experienceModification < 0) {
                    guiGraphics.setColor(1, 0, 0, 1);
                } else {
                    guiGraphics.setColor(0.6f, 0.2f, 0.85f, 1);
                }

                drawExperienceBar(guiGraphics, barYPos, leftBarX, leftExpBarHoverProgress);

                if (rightExpBarHoverProgress > 0) {
                    drawExperienceBar(guiGraphics, barYPos, rightBarX, rightExpBarHoverProgress);
                }

                guiGraphics.setColor(1, 1, 1, 1);
            }

            int greenFontColor = 0x57882F;
            int redFontColor = 0xE4472F; // TODO :: use darker color
            // TODO :: add neutral color for no change
            int color = experienceModification < 0 ? redFontColor : greenFontColor;

            Component expectedLevel = Component.literal(String.valueOf(newLevel)).withColor(color);

            int expLevelXPos = ((rightBarX + leftBarX) / 2 + 38 - minecraft.font.width(expectedLevel) / 2) - 1;
            int expLevelYPos = barYPos - 1;
            guiGraphics.drawString(minecraft.font, expectedLevel, expLevelXPos, expLevelYPos, 0, false);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawExperienceBar(final GuiGraphics guiGraphics, int y, int initialX, float hoverProgress) {
        guiGraphics.blit(EXP_FULL, initialX, y, 0, 0, (int) (73 * hoverProgress), 6, 73, 6);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        // Don't render the vanilla background, it darkens the UI in an undesirable way
    }

    @Override
    public void init() {
        int xSize = 256;
        int ySize = 256;

        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize / 2) / 2;

        int startX = guiLeft + 15;
        int startY = guiTop + 2;

        //Inventory
        addRenderableWidget(new TabButton(startX + 5 + 10, startY - 26 - 30, TabButton.Type.INVENTORY_TAB, this));
        addRenderableWidget(new TabButton(startX + 34 + 10, startY - 28 - 30, TabButton.Type.ABILITY_TAB, this));
        addRenderableWidget(new TabButton(startX + 62 + 10, startY - 26 - 30, TabButton.Type.GITHUB_REMINDER_TAB, this));
        addRenderableWidget(new TabButton(startX + 91 + 10, startY - 26 - 30, TabButton.Type.SKINS_TAB, this));

        // FIXME
        //noinspection DataFlowIssue -> player is present
        MagicData data = MagicData.getData(minecraft.player);
        List<DragonAbilityInstance> actives = data.getActiveAbilities();
        List<DragonAbilityInstance> passives = data.getPassiveAbilities();

        for (int i = 0; i < ABILITIES_PER_COLUMN; i++) {
            DragonAbilityInstance instance = actives.size() > i ? actives.get(i) : null;
            addRenderableWidget(new AbilityButton((int) (guiLeft + BACKGROUND_BEZEL_WIDTH + (INNER_BACKGROUND_WIDTH / 3.7f)), guiTop + i * 40, instance, this));
        }

        for (int i = 0; i < ABILITIES_PER_COLUMN; i++) {
            DragonAbilityInstance instance = passives.size() > i ? passives.get(i) : null;
            addRenderableWidget(new AbilityButton((int) (guiLeft + BACKGROUND_BEZEL_WIDTH + (INNER_BACKGROUND_WIDTH / 1.23f)), guiTop + i * 40, instance, this));
        }

        // Left panel (hotbar)
        for(int i = 0; i < ABILITIES_PER_COLUMN; i++) {
            AbstractWidget widget = new AbilityButton(guiLeft - 18, guiTop + i * 40, data.fromSlot(i), this, true, i);
            addRenderableWidget(widget);
            leftWindowWidgets.add(widget);
            widget.visible = leftWindowOpen;
        }

        AbstractWidget leftHelpButton = new HelpButton(guiLeft - 7, startY - 25, 13, 13, HELP_ABILITY_ASSIGNMENT);
        addRenderableWidget(leftHelpButton);
        leftWindowWidgets.add(leftHelpButton);
        leftHelpButton.visible = leftWindowOpen;

        List<DragonAbilityInstance> innate = data.getInnateAbilities();
        // Right panel (innate ablities)
        for(int i = 0; i < ABILITIES_PER_COLUMN; i++) {
            DragonAbilityInstance instance = innate.size() > i ? innate.get(i) : null;
            AbstractWidget widget = new AbilityButton(guiLeft + 239, guiTop + i * 40, instance, this);
            addRenderableWidget(widget);
            rightWindowWidgets.add(widget);
            widget.visible = rightWindowOpen;
        }

        AbstractWidget rightHelpButton = new HelpButton(guiLeft + 250, startY - 25, 13, 13, HELP_INNATE);
        addRenderableWidget(rightHelpButton);
        rightWindowWidgets.add(rightHelpButton);
        rightHelpButton.visible = rightWindowOpen;

        addRenderableWidget(new ClickHoverButton(guiLeft + 17, guiTop + 69, 10, 17, 0, 1, 18, 18, Component.empty(), button -> {
            leftWindowOpen = !leftWindowOpen;
            for(AbstractWidget widget : leftWindowWidgets) {
                widget.visible = leftWindowOpen;
            }
        }, LEFT_PANEL_ARROW_CLICK, LEFT_PANEL_ARROW_HOVER, LEFT_PANEL_ARROW_MAIN));

        addRenderableWidget(new ClickHoverButton(guiLeft + 228, guiTop + 69, 10, 17, 0, 1, 18, 18, Component.empty(), button -> {
            rightWindowOpen = !rightWindowOpen;
            for(AbstractWidget widget : rightWindowWidgets) {
                widget.visible = rightWindowOpen;
            }
        }, RIGHT_PANEL_ARROW_CLICK, RIGHT_PANEL_ARROW_HOVER, RIGHT_PANEL_ARROW_MAIN));

        addRenderableWidget(new HelpButton(guiLeft + BACKGROUND_BEZEL_WIDTH + (INNER_BACKGROUND_WIDTH / 2) + 19, startY + 263 / 2 + 24, 13, 13, HELP_PASSIVE_ACTIVE));
    }


    @Override
    public void tick() {
        // FIXME
        //noinspection DataFlowIssue -> players should be present
        DragonStateHandler data = DragonStateProvider.getData(minecraft.player);
        //unlockableAbilities.clear();
        type = data.getType();

        /*for (ActiveDragonAbility ability : data.getMagicData().getActiveAbilities()) {
            int level = DragonAbilities.getAbility(minecraft.player, ability.getClass()).map(ActiveDragonAbility::getLevel).orElse(ability.level);

            for (int i = level; i < ability.getMaxLevel(); i++) {
                try {
                    ActiveDragonAbility instance = ability.getClass().getDeclaredConstructor().newInstance();
                    instance.setLevel(i + 1);
                    unlockableAbilities.add(instance);
                } catch (ReflectiveOperationException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }*/

        // Show abilities with the lowest required experience level first
       // unlockableAbilities.sort(Comparator.comparingInt(ActiveDragonAbility::getCurrentRequiredLevel));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}