package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class AbilityButton extends Button {
    public static final ResourceLocation BLANK_2_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/blank2.png");

    private static final ResourceLocation BLANK_1_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/blank1.png");
    private static final ResourceLocation BLANK_3_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/blank3.png");

    public int skillType;
    public DragonAbility ability;

    private final Screen screen;
    private final int slot;

    public AbilityButton(int x, int y, int skillType, int slot, Screen screen) {
        super(x, y, 32, 32, Component.empty(), action -> { /* Nothing to do */ }, DEFAULT_NARRATION);
        this.slot = slot;
        this.skillType = skillType;
        this.screen = screen;
    }

    public boolean dragging = false;

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY);

        if (skillType == 0) {
            dragging = true;

            screen.renderables.forEach(s -> {
                if (s instanceof AbilityButton btn) {
                    if (btn != this && btn.skillType == 0) {
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

        if (skillType == 0) {
            dragging = false;
        }
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // FIXME
        /*DragonStateProvider.getOptional(Minecraft.getInstance().player).ifPresent(cap -> {
            DragonAbility ab =
                    skillType == 0 ? cap.getMagicData().getAbilityFromSlot(slot) :
                            skillType == 1 ? cap.getMagicData().getPassiveAbilityFromSlot(slot) :
                                    skillType == 2 ? cap.getMagicData().getInnateAbilityFromSlot(slot) : null;

            if (ab != null)
                ability = ab;

        });

        boolean isDragging = false;

        if (skillType == 0) {
            for (Renderable s : screen.renderables) {
                if (s instanceof AbilityButton btn) {
                    if (btn != this && btn.skillType == 0 && btn.dragging) {
                        isDragging = true;
                        break;
                    }
                }
            }
        }

        guiGraphics.blit(isDragging ? BLANK_3_TEXTURE : ability instanceof PassiveDragonAbility ? BLANK_2_TEXTURE : BLANK_1_TEXTURE, getX() - 1, getY() - 1, 0, 0, 20, 20, 20, 20);

        if (ability != null && !dragging) {
            guiGraphics.blit(ability.getIcon(), getX(), getY(), 0, 0, 32, 32, 32, 32);

            if (ability.isDisabled()) {
                RenderSystem.enableBlend();
                guiGraphics.blit(MagicDragonRender.INVALID_ICON, getX(), getY(), 0, 0, 32, 32, 32, 32);
                RenderSystem.disableBlend();
            }
        }

        if (isHovered()) {
            if (ability != null) {
                FormattedText desc = ability.getDescription();

                if (!ability.getInfo().isEmpty()) {
                    desc = FormattedText.composite(desc, Component.empty().append("\n\n"));
                }

                List<FormattedCharSequence> description = Minecraft.getInstance().font.split(desc, 143);
                int yPos = getY() - description.size() * 7;

                guiGraphics.pose().pushPose();
                // Render above the other UI elements
                guiGraphics.pose().translate(0, 0, 150);
                MagicDragonRender.drawAbilityHover(guiGraphics, getX() + width, yPos, ability);
                guiGraphics.pose().popPose();
            }
        }*/
    }
}