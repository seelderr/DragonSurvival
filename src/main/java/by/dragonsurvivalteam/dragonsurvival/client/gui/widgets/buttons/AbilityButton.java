package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.magic.AbilityTooltipRenderer;
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

import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class AbilityButton extends Button {
    public static final ResourceLocation ACTIVE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_main.png");
    public static final ResourceLocation PASSIVE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_other.png");
    public static final ResourceLocation AUTOUPGRADE_ORNAMENTATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_autoupgrade.png");

    public DragonAbilityInstance ability;
    private final Screen screen;
    private boolean canBeRemoved;
    private int slot = -1;

    public AbilityButton(int x, int y, DragonAbilityInstance ability, Screen screen, boolean canBeRemoved, int slot) {
        this(x, y, ability, screen);
        this.canBeRemoved = canBeRemoved;
        this.slot = slot;
    }

    public AbilityButton(int x, int y, DragonAbilityInstance ability, Screen screen) {
        super(x, y, 34, 34, Component.empty(), action -> { /* Nothing to do */ }, DEFAULT_NARRATION);
        this.screen = screen;
        this.ability = ability;
        this.canBeRemoved = false;
    }

    public boolean dragging = false;

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY);

        if (ability == null) return;

        if (!ability.isPassive()) {
            dragging = true;
        }
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        super.onRelease(pMouseX, pMouseY);

        if (ability == null) return;

        if (!ability.isPassive()) {
            dragging = false;
            DragonAbilityInstance abilitySwappedTo = null;
            //noinspection DataFlowIssue -> player is present
            MagicData data = MagicData.getData(Minecraft.getInstance().player);

            for (Renderable renderable : screen.renderables) {
                if (renderable instanceof AbilityButton button) {
                    if (button.slot != MagicData.NO_SLOT) {
                        if (button.isMouseOver(pMouseX, pMouseY)) {
                            PacketDistributor.sendToServer(new SyncSlotAssignment(ability.key(), button.slot));
                            data.moveAbilityToSlot(ability.key(), button.slot);
                            abilitySwappedTo = button.ability;
                            break;
                        }
                    }
                }
            }

            if (canBeRemoved) {
                if (abilitySwappedTo == null) {
                    PacketDistributor.sendToServer(new SyncSlotAssignment(ability.key(), -1));
                    data.moveAbilityToSlot(ability.key(), -1);
                }
            }
        }
    }

    public void setAbility(DragonAbilityInstance ability) {
        this.ability = ability;
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(canBeRemoved) {
            ability = MagicData.getData(Minecraft.getInstance().player).fromSlot(slot);
        }

        if(ability == null) {
            guiGraphics.blit(PASSIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, 38, 38, 38, 38);
            return;
        }

        boolean isAnyAbilityButtonDragging = false;

        if (!ability.isPassive()) {
            for (Renderable s : screen.renderables) {
                if (s instanceof AbilityButton btn) {
                    if(btn.ability != null) {
                        if (btn != this && !btn.ability.isPassive() && btn.dragging) {
                            isAnyAbilityButtonDragging = true;
                            break;
                        }
                    }
                }
            }
        }

        isAnyAbilityButtonDragging |= dragging;

        if(ability.isPassive()) {
            guiGraphics.blit(PASSIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, 38, 38, 38, 38);
        } else {
            guiGraphics.blit(ACTIVE_BACKGROUND, getX() - 2, getY() - 2, 0, 0, 38, 38, 38, 38);
        }

        if(ability.ability().value().upgrade().isPresent()) {
            if(ability.ability().value().upgrade().get().type() == Upgrade.Type.PASSIVE) {
                guiGraphics.blit(AUTOUPGRADE_ORNAMENTATION, getX() - 2, getY() - 2, 0, 0, 38, 38, 38, 38);
            }
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 50);
        if(dragging) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 100);
            guiGraphics.blit(ability.getIcon(), mouseX - 17, mouseY - 17, 0, 0, 34, 34, 34, 34);
            guiGraphics.pose().popPose();
        }

        if(!canBeRemoved || !dragging) {
            guiGraphics.blit(ability.getIcon(), getX(), getY(), 0, 0, 34, 34, 34, 34);
        }
        guiGraphics.pose().popPose();

        if (isHovered() && !isAnyAbilityButtonDragging) {
            FormattedText nameAndDescriptionRaw = ability.getName();

            if (!ability.getInfo(Minecraft.getInstance().player).isEmpty()) {
                nameAndDescriptionRaw = FormattedText.composite(nameAndDescriptionRaw, Component.empty().append("\n\n"));
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
}