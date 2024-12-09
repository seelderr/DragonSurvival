package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Upgrade;
import by.dragonsurvivalteam.dragonsurvival.magic.AbilityTooltipRenderer;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class AbilityButton extends Button {
    public static final ResourceLocation ACTIVE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_main.png");
    public static final ResourceLocation PASSIVE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_other.png");
    public static final ResourceLocation AUTOUPGRADE_ORNAMENTATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ability_screen/skill_autoupgrade.png");

    public DragonAbilityInstance ability;
    private final Screen screen;

    public AbilityButton(int x, int y, DragonAbilityInstance ability, Screen screen) {
        super(x, y, 34, 34, Component.empty(), action -> { /* Nothing to do */ }, DEFAULT_NARRATION);
        this.screen = screen;
        this.ability = ability;
    }

    public boolean dragging = false;

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY);

        if (!ability.isPassive()) {
            dragging = true;

            screen.renderables.forEach(s -> {
                if (s instanceof AbilityButton btn) {
                    if (btn != this && !ability.isPassive()) {
                        btn.onRelease(pMouseX, pMouseY);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);

        // FIXME
        /*if (skillType == 0) {
            screen.renderables.forEach(s -> {
                if (s instanceof AbilityButton btn) {
                    if (btn != this && btn.skillType == 0 && btn.dragging) {
                        MagicData data = MagicData.getData(Minecraft.getInstance().player);
                        btn.onRelease(pMouseX, pMouseY);
                        DragonAbilityInstance ab1 = data.getAbilityFromSlot(btn.slot);
                        DragonAbilityInstance ab2 = data.getAbilityFromSlot(slot);
                        cap.activeDragonAbilities.put(slot, ab1.getName());
                        cap.activeDragonAbilities.put(btn.slot, ab2.getName());
                        PacketDistributor.sendToServer(new SyncMagicCap.Data(Minecraft.getInstance().player.getId(), cap.serializeNBT(Minecraft.getInstance().player.registryAccess())));
                    }
                }
            });
        }*/
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        super.onRelease(pMouseX, pMouseY);

        if (!ability.isPassive()) {
            dragging = false;
        }
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // FIXME
        boolean isAnyAbilityButtonDragging = false;

        if (!ability.isPassive()) {
            for (Renderable s : screen.renderables) {
                if (s instanceof AbilityButton btn) {
                    if (btn != this && !btn.ability.isPassive() && btn.dragging) {
                        isAnyAbilityButtonDragging = true;
                        break;
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

        if (ability != null) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 50);
            int x, y;
            if(dragging) {
                x = mouseX - 17;
                y = mouseY - 17;
                guiGraphics.pose().translate(0, 0, 100);
            } else {
                x = getX();
                y = getY();
            }
            guiGraphics.blit(ability.getIcon(), x, y, 0, 0, 34, 34, 34, 34);
            guiGraphics.pose().popPose();
        }

        if (isHovered() && !isAnyAbilityButtonDragging) {
            if (ability != null) {
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
}