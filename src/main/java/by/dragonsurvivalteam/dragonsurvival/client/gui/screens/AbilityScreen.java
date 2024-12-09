package by.dragonsurvivalteam.dragonsurvival.client.gui.screens;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.*;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.ExperienceUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

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

    @Translation(type = Translation.Type.MISC, comments = "■ §dInnate skills§r are a dragon's quirks, and represent the benefits and drawbacks of each dragon type.")
    private static final String HELP_INNATE = Translation.Type.GUI.wrap("help.innate_abilities");

    private static final ResourceLocation BACKGROUND_MAIN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/background_main.png");
    private static final int INNER_BACKGROUND_WIDTH = 185;
    private static final int BACKGROUND_BEZEL_WIDTH = 10;
    private static final ResourceLocation EXP_EMPTY = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/exp_empty.png");
    private static final ResourceLocation EXP_FULL = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/exp_full.png");

    /**
     * Currently used to determine how much % of the green experience bar is filled <br>
     * This value is set as the current highest level requirement of the auto-leveling abilities <br>
     * (Meaning of those whose level change depending on how much experience the player has
     */
    private static final float HIGHEST_LEVEL = 55f;

    private static int ABILITIES_PER_COLUMN = 4;

    public Screen sourceScreen;
    //public ArrayList<ActiveDragonAbility> unlockableAbilities = new ArrayList<>();

    private int guiLeft;
    private int guiTop;
    private Holder<DragonType> type;

    public AbilityScreen(Screen sourceScreen) {
        super(Component.empty().append("AbilityScreen")); // FIXME :: what is this component used for
        this.sourceScreen = sourceScreen;
    }

    public List<? extends GuiEventListener> widgetList() {
        return children();
    }

    @Override
    public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (minecraft == null) {
            return;
        }

        this.renderBlurredBackground(partialTick);

        int startX = guiLeft + 25;
        int startY = guiTop - 28;

        guiGraphics.blit(BACKGROUND_MAIN, startX, startY, 0, 0, 256, 256);

        if (type != null) {
            //noinspection DataFlowIssue -> player should not be null

            // Draw XP bars
            float progress = Mth.clamp((float) minecraft.player.experienceLevel / HIGHEST_LEVEL, 0, 1);
            float leftExpBarProgress = Math.min(1f, Math.min(0.5f, progress) * 2);
            float rightExpBarProgress = Math.min(1f, Math.min(0.5f, progress - 0.5f) * 2);

            int barYPos = startY + 10;
            int leftBarX = startX + 10;
            int rightBarX = startX + 122;

            guiGraphics.blit(EXP_EMPTY, leftBarX, barYPos, 0, 0, 73, 6, 73, 6);
            guiGraphics.blit(EXP_EMPTY, rightBarX, barYPos, 0, 0, 73, 6, 73, 6);
            guiGraphics.blit(EXP_FULL, leftBarX, barYPos, 0, 0, (int) (73 * leftExpBarProgress), 6, 73, 6);

            if (progress > 0.5) {
                guiGraphics.blit(EXP_FULL, rightBarX, barYPos, 0, 0, (int) (73 * rightExpBarProgress), 6, 73, 6);
            }

            int experienceLevels = 0;

            for (GuiEventListener button : children()) {
                if (!(button instanceof AbstractWidget widget) || !widget.isHovered()) {
                    continue;
                }

                if (button instanceof IncreaseLevelButton increaseLevelButton) {
                    experienceLevels = increaseLevelButton.upgradeCost;
                    break;
                } else if (button instanceof DecreaseLevelButton decreaseLevelButton) {
                    experienceLevels = decreaseLevelButton.gainedLevels;
                    break;
                }
            }

            if (experienceLevels != 0) {
                // Indicate how much % experience would be lost or gained (by comparing the experience points the levels are worth)
                float change = Mth.clamp((float) ExperienceUtils.getTotalExperience(experienceLevels) / (float) ExperienceUtils.getTotalExperience(minecraft.player), 0, 1);
                float leftOverlayBar = Math.min(1, Math.min(0.5f, change) * 2);
                float rightOverlayBar = Math.min(1, Math.min(0.5f, change - 0.5f) * 2);

                // Render the purple bar which indicates the amount that will be taken from the current experience
                guiGraphics.blit(MagicHUD.WIDGET_TEXTURES, startX + 23 / 2, startY + 28, 0, (float) 174 / 2, (int) (105 * leftOverlayBar), 3, 128, 128);

                if (rightOverlayBar > 0.5) {
                    guiGraphics.blit(MagicHUD.WIDGET_TEXTURES, startX + 254 / 2, startY + 28, 0, (float) 174 / 2, (int) (105 * rightOverlayBar), 3, 128, 128);
                }
            }

            Component currentLevel = Component.literal(Integer.toString(minecraft.player.experienceLevel)).withStyle(ChatFormatting.DARK_GRAY);

            int expLevelXPos = ((rightBarX + leftBarX) / 2 + 38 - minecraft.font.width(currentLevel) / 2);
            int expLevelYPos = barYPos - 1;
            guiGraphics.drawString(minecraft.font, currentLevel, expLevelXPos, expLevelYPos, 0, false);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // FIXME
        /*renderables.forEach(renderable -> {
            if (renderable instanceof AbilityButton babilityButtonn) {
                if (babilityButtonn.skillType == 0 && babilityButtonn.dragging && babilityButtonn.ability != null) {
                    RenderSystem.setShaderTexture(0, babilityButtonn.ability.getIcon());
                    guiGraphics.blit(babilityButtonn.ability.getIcon(), mouseX, mouseY, 0, 0, 32, 32, 32, 32);
                }
            }
        });*/
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

        /*addRenderableWidget(new SkillProgressButton(guiLeft + 10 + (int) (219 / 2F), startY + 8 - 30, 4, this));

        for (int i = 1; i <= 4; i++) {
            addRenderableWidget(new SkillProgressButton(guiLeft + 10 + (int) (219 / 2F) - i * 23, startY + 8 - 30, 4 - i, this));
            addRenderableWidget(new SkillProgressButton(guiLeft + 10 + (int) (219 / 2F) + i * 23, startY + 8 - 30, 4 + i, this));
        }*/

        // FIXME
        MagicData data = MagicData.getData(minecraft.player);
        List<DragonAbilityInstance> actives = data.getActiveAbilities();
        List<DragonAbilityInstance> passives = data.getPassiveAbilities();
        for(int i = 0; i < ABILITIES_PER_COLUMN; i++) {
            addRenderableWidget(new AbilityButton((int) (guiLeft + BACKGROUND_BEZEL_WIDTH + (INNER_BACKGROUND_WIDTH / 3.7f)), guiTop + i * 40, actives.get(i), this));
        }
       /* DragonStateProvider.getOptional(Minecraft.getInstance().player).ifPresent(cap -> {
            for (int num = 0; num < MagicCap.activeAbilitySlots; num++) {
                addRenderableWidget(new AbilityButton((int) (guiLeft + (90 + 20) / 2.0), guiTop + 40 - 25 + num * 35, 0, num, this));
            }

            for (int num = 0; num < MagicCap.passiveAbilitySlots; num++) {
                addRenderableWidget(new AbilityButton(guiLeft + (int) ((215 + 10) / 2F), guiTop + 40 - 25 + num * 35, 1, num, this));
                addRenderableWidget(new IncreaseLevelButton(guiLeft + (int) (219 / 2F) + 35, guiTop + 40 - 17 + num * 35, num));
                addRenderableWidget(new DecreaseLevelButton(guiLeft + (int) (219 / 2F) - 13, guiTop + 40 - 17 + num * 35, num));
            }

            for (int num = 0; num < MagicCap.innateAbilitySlots; num++) {
                addRenderableWidget(new AbilityButton(guiLeft + (int) (340 / 2F), guiTop + 40 - 25 + num * 35, 2, num, this));
            }
        });*/

        addRenderableWidget(new HelpButton(startX + 218 / 2 + 3 + 10 - 55, startY + 263 / 2 + 28, 9, 9, HELP_ACTIVE, 0));
        addRenderableWidget(new HelpButton(startX + 218 / 2 + 3 + 10 + 2, startY + 263 / 2 + 28, 9, 9, HELP_PASSIVE, 0));
        addRenderableWidget(new HelpButton(startX + 218 / 2 + 3 + 10 + 60, startY + 263 / 2 + 28, 9, 9, HELP_INNATE, 0));
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