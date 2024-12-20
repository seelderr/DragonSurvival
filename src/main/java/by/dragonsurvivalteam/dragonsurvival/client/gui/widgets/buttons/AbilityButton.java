package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.magic.AbilityTooltipRenderer;
import by.dragonsurvivalteam.dragonsurvival.mixins.client.ScreenAccessor;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncSlotAssignment;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class AbilityButton extends Button {
    public static final ResourceLocation ACTIVE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_main.png");
    public static final ResourceLocation PASSIVE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_other.png");
    public static final ResourceLocation AUTOUPGRADE_ORNAMENTATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_autoupgrade.png");

    private static final int SIZE = 34;
    private static final int ORNAMENTATION_SIZE = 38;

    private DragonAbilityInstance ability;
    private final AbilityScreen screen;
    private int slot = MagicData.NO_SLOT;
    private boolean isHotbar;
    private boolean isDragging;
    private float scale;
    private Vec3 offset = new Vec3(0, 0, 0);
    private boolean isInteractable = true;
    private LevelButton leftLevelButton;
    private LevelButton rightLevelButton;

    public AbilityButton(int x, int y, @Nullable final DragonAbilityInstance ability, final AbilityScreen screen, float scale) {
        // Don't actually change the scale of the button itself based on the scale value; this is because we only rescale the button when it is
        // on the sides of the column, in which case it can't be interacted with anyways. Minecraft's GUI doesn't offer a clean way to adjust
        // the button's bounds dynamically, so this is the best we can do.
        super(x, y, 34, 34, Component.empty(), button -> { /* Nothing to do */ }, DEFAULT_NARRATION);
        this.screen = screen;
        this.ability = ability;
        this.isHotbar = false;
        this.scale = scale;

        if (ability == null || ability.value().upgrade().map(Upgrade::type).orElse(null) != Upgrade.Type.MANUAL) {
            return;
        }

        leftLevelButton = new LevelButton(LevelButton.Type.DOWNGRADE, ability, x - width / 2 + 7, y + 10);
        rightLevelButton = new LevelButton(LevelButton.Type.UPGRADE, ability, x + width / 2 + 18, y + 10);
        ((ScreenAccessor) screen).dragonSurvival$addRenderableWidget(leftLevelButton);
        ((ScreenAccessor) screen).dragonSurvival$addRenderableWidget(rightLevelButton);
    }

    public AbilityButton(int x, int y, @Nullable final DragonAbilityInstance ability, final AbilityScreen screen, boolean isHotbar, int slot) {
        this(x, y, ability, screen);
        this.isHotbar = isHotbar;
        this.slot = slot;

        //noinspection DataFlowIssue -> player is present
        MagicData data = MagicData.getData(Minecraft.getInstance().player);
        this.ability = data.fromSlot(slot);
    }

    public AbilityButton(int x, int y, @Nullable final DragonAbilityInstance ability, final AbilityScreen screen) {
        this(x, y, ability, screen, 1.0f);
    }

    public void setOffset(Vec3 offset) {
        this.offset = offset;
    }

    public Vec3 getOffset() {
        return offset;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isInteractable && super.isMouseOver(mouseX, mouseY);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if(leftLevelButton != null && rightLevelButton != null) {
            leftLevelButton.visible = visible;
            rightLevelButton.visible = visible;
        }
    }

    public void setInteractable(boolean interactable) {
        isInteractable = interactable;
        if(isInteractable) {
            width = SIZE;
            height = SIZE;
            if(leftLevelButton != null && rightLevelButton != null) {
                leftLevelButton.resetDimensions();
                rightLevelButton.resetDimensions();
            }
        } else {
            width = 0;
            height = 0;
            if(leftLevelButton != null && rightLevelButton != null) {
                leftLevelButton.setWidth(0);
                leftLevelButton.setHeight(0);
                rightLevelButton.setWidth(0);
                rightLevelButton.setHeight(0);
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

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (isHotbar) {
            // Currently the easiest way to track assignments (and not end up with duplicate icons)
            // Alternative would be to go through all buttons and remove the ability that matches the swapped ability (in 'onRelease')
            //noinspection DataFlowIssue -> player is present
            ability = MagicData.getData(Minecraft.getInstance().player).fromSlot(slot);
        }

        guiGraphics.pose().pushPose();
        // Scale about the center of the button
        guiGraphics.pose().translate(getX(), getY(), 0);
        guiGraphics.pose().scale(scale, scale, 1);
        guiGraphics.pose().translate(-getX(), -getY(), 0);
        float scaleXDiff = (scale - 1) * SIZE / 2;
        float scaleYDiff = (scale - 1) * SIZE / 2;
        guiGraphics.pose().translate(offset.x - scaleXDiff, offset.y - scaleYDiff, offset.z);
        if (ability == null) {
            guiGraphics.blit(PASSIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE);
            return;
        }

        if (ability.isPassive()) {
            guiGraphics.blit(PASSIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE);
        } else {
            guiGraphics.blit(ACTIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE);
        }

        if (!ability.isManuallyUpgraded()) {
            guiGraphics.blit(AUTOUPGRADE_ORNAMENTATION, getX() - 2, getY() - 2, 0, 0, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE, ORNAMENTATION_SIZE);
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 50);

        if (isDragging) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 100);
            guiGraphics.blit(ability.getIcon(), mouseX - SIZE / 2, mouseY - SIZE / 2, 0, 0, SIZE, SIZE, SIZE, SIZE);
            guiGraphics.pose().popPose();
        }

        if (!isHotbar || !isDragging) {
            guiGraphics.blit(ability.getIcon(), getX(), getY(), 0, 0, SIZE, SIZE, SIZE, SIZE);
        }

        guiGraphics.pose().popPose();

        if (isHovered() && shouldShowDescription()) {
            guiGraphics.pose().pushPose();
            // Render above the other UI elements
            guiGraphics.pose().translate(0, 0, 150);
            AbilityTooltipRenderer.drawAbilityHover(guiGraphics, mouseX, mouseY, ability);
            guiGraphics.pose().popPose();
        }
        guiGraphics.pose().popPose();
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