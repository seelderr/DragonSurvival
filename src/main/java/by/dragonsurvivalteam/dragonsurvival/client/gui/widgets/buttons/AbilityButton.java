package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ClickHoverButton;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.magic.AbilityTooltipRenderer;
import by.dragonsurvivalteam.dragonsurvival.mixins.ScreenAccessor;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncSlotAssignment;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class AbilityButton extends Button {
    public static final ResourceLocation ACTIVE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_main.png");
    public static final ResourceLocation PASSIVE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_other.png");
    public static final ResourceLocation AUTOUPGRADE_ORNAMENTATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_autoupgrade.png");
    public static final ResourceLocation ARROW_LEFT_UPGRADE_CLICK = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/arrow_left_upgrade_click.png");
    public static final ResourceLocation ARROW_LEFT_UPGRADE_HOVER = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/arrow_left_upgrade_hover.png");
    public static final ResourceLocation ARROW_LEFT_UPGRADE_MAIN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/arrow_left_upgrade_main.png");
    public static final ResourceLocation ARROW_RIGHT_UPGRADE_CLICK = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/arrow_right_upgrade_click.png");
    public static final ResourceLocation ARROW_RIGHT_UPGRADE_HOVER = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/arrow_right_upgrade_hover.png");
    public static final ResourceLocation ARROW_RIGHT_UPGRADE_MAIN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/arrow_right_upgrade_main.png");

    private DragonAbilityInstance ability;
    private final AbilityScreen screen;
    private int slot = MagicData.NO_SLOT;
    private boolean isHotbar;
    private boolean isDragging;

    public AbilityButton(int x, int y, @Nullable final DragonAbilityInstance ability, final AbilityScreen screen, boolean isHotbar, int slot) {
        this(x, y, ability, screen);
        this.isHotbar = isHotbar;
        this.slot = slot;

        //noinspection DataFlowIssue -> player is present
        MagicData data = MagicData.getData(Minecraft.getInstance().player);
        this.ability = data.fromSlot(slot);
    }

    public AbilityButton(int x, int y, @Nullable final DragonAbilityInstance ability, final AbilityScreen screen) {
        super(x, y, 34, 34, Component.empty(), action -> { /* Nothing to do */ }, DEFAULT_NARRATION);
        this.screen = screen;
        this.ability = ability;
        this.isHotbar = false;

        if(ability != null) {
            if(ability.value().upgrade().isPresent() && ability.value().upgrade().get().type() == Upgrade.Type.MANUAL){
                ((ScreenAccessor)screen).dragonSurvival$addRenderableWidget(new ClickHoverButton(
                        x - width / 2 + 7, y + 10, 9, 14, 0, 0,  16, 16, Component.empty(), action -> {
                    //noinspection DataFlowIssue -> player is present
                    MagicData data = MagicData.getData(Minecraft.getInstance().player);
                    data.downgradeAbility(ability.key());
                }, ARROW_LEFT_UPGRADE_CLICK, ARROW_LEFT_UPGRADE_HOVER, ARROW_LEFT_UPGRADE_MAIN));

                ((ScreenAccessor)screen).dragonSurvival$addRenderableWidget(new ClickHoverButton(
                        x + width / 2 + 18, y + 10, 9, 14, 0, 0, 16, 16, Component.empty(), action -> {
                    //noinspection DataFlowIssue -> player is present
                    MagicData data = MagicData.getData(Minecraft.getInstance().player);
                    data.upgradeAbility(Minecraft.getInstance().player, ability.key());
                }, ARROW_RIGHT_UPGRADE_CLICK, ARROW_RIGHT_UPGRADE_HOVER, ARROW_RIGHT_UPGRADE_MAIN)
                {
                    @Override
                    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                        if(isHovered()) {
                            MagicData data = MagicData.getData(Minecraft.getInstance().player);
                            if(ability.value().upgrade().isPresent() && ability.level() != ability.value().upgrade().get().maximumLevel()) {
                                screen.expHoverAmount = (int) data.getUpgradeCost(ability.key());
                            } else {
                                screen.expHoverAmount = 0;
                            }
                        } else {
                            screen.expHoverAmount = 0;
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY);

        if (ability == null) {
            return;
        }

        if (!ability.isPassive()) {
            isDragging = true;
        }
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        super.onRelease(pMouseX, pMouseY);
        isDragging = false;

        if (ability == null) {
            return;
        }

        if (!ability.isPassive()) {
            //noinspection DataFlowIssue -> player is present
            MagicData data = MagicData.getData(Minecraft.getInstance().player);

            boolean wasSwappedToASlot = false;
            for (Renderable renderable : screen.renderables) {
                if (renderable instanceof AbilityButton button && button.slot != MagicData.NO_SLOT) {
                    if (button.isMouseOver(pMouseX, pMouseY)) {
                        PacketDistributor.sendToServer(new SyncSlotAssignment(ability.key(), button.slot));
                        data.moveAbilityToSlot(ability.key(), button.slot);
                        wasSwappedToASlot = true;
                        break;
                    }
                }
            }

            if (isHotbar && !wasSwappedToASlot) {
                PacketDistributor.sendToServer(new SyncSlotAssignment(ability.key(), MagicData.NO_SLOT));
                data.moveAbilityToSlot(ability.key(), MagicData.NO_SLOT);
            }
        }
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (isHotbar) {
            // Currently the easiest way to track assignments (and not end up with duplicate icons)
            // Alternative would be to go through all buttons and remove the ability that matches the swapped ability (in 'onRelease')
            //noinspection DataFlowIssue -> player is present
            ability = MagicData.getData(Minecraft.getInstance().player).fromSlot(slot);
        }

        if (ability == null) {
            guiGraphics.blit(PASSIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, 38, 38, 38, 38);
            return;
        }

        if (ability.isPassive()) {
            guiGraphics.blit(PASSIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, 38, 38, 38, 38);
        } else {
            guiGraphics.blit(ACTIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, 38, 38, 38, 38);
        }

        if (ability.ability().value().upgrade().map(Upgrade::type).orElse(null) == Upgrade.Type.PASSIVE) {
            guiGraphics.blit(AUTOUPGRADE_ORNAMENTATION, getX() - 2, getY() - 2, 0, 0, 38, 38, 38, 38);
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 50);

        if (isDragging) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 100);
            guiGraphics.blit(ability.getIcon(), mouseX - 17, mouseY - 17, 0, 0, 34, 34, 34, 34);
            guiGraphics.pose().popPose();
        }

        if (!isHotbar || !isDragging) {
            guiGraphics.blit(ability.getIcon(), getX(), getY(), 0, 0, 34, 34, 34, 34);
        }

        guiGraphics.pose().popPose();

        if (isHovered() && shouldShowDescription()) {
            FormattedText nameAndDescriptionRaw = ability.getName();

            if (!ability.getInfo(Minecraft.getInstance().player).isEmpty()) {
                nameAndDescriptionRaw = FormattedText.composite(nameAndDescriptionRaw, Component.literal("\n\n"));
            }

            List<FormattedCharSequence> nameAndDescription = Minecraft.getInstance().font.split(nameAndDescriptionRaw, 143);
            int yPos = getY() - nameAndDescription.size() * 7;

            guiGraphics.pose().pushPose();
            // Render above the other UI elements
            guiGraphics.pose().translate(0, 0, 150);
            AbilityTooltipRenderer.drawAbilityHover(guiGraphics, getX() + width, yPos - 30, ability);
            guiGraphics.pose().popPose();
        }
    }

    /** If the player is dragging any button the buttons shouldn't show their description */
    private boolean shouldShowDescription() {
        if (isDragging) {
            return false;
        }

        if (!ability.isPassive()) {
            for (Renderable renderable : screen.renderables) {
                if (renderable instanceof AbilityButton button && button.ability != null) {
                    if (button != this && !button.ability.isPassive() && button.isDragging) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}