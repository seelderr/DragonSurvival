package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components.BackgroundColorSelectorComponent;
import by.dragonsurvivalteam.dragonsurvival.mixins.client.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class BackgroundColorButton extends ExtendedButton {
    public static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/textbox.png");
    public static final ResourceLocation BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/background_color_button.png");

    private final DragonEditorScreen screen;
    public boolean toggled;
    public int xSize, ySize;
    private BackgroundColorSelectorComponent colorComponent;
    private Renderable renderButton;


    public BackgroundColorButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, DragonEditorScreen dragonEditorScreen) {
        super(xPos, yPos, width, height, displayString, handler);
        xSize = width;
        ySize = height;
        screen = dragonEditorScreen;
        setTooltip(Tooltip.create(Component.translatable("ds.gui.dragon_editor.background_color")));
    }

    @Override
    public void onPress() {
        if (!toggled) {
            renderButton = new ExtendedButton(0, 0, 0, 0, Component.empty(), null) {
                @Override
                public void renderWidget(@NotNull final GuiGraphics guiGraphics, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
                    active = visible = false;

                    if (colorComponent != null) {
                        colorComponent.visible = BackgroundColorButton.this.visible;
                        if (colorComponent.visible) {
                            colorComponent.render(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
                        }
                    }
                }
            };

            Screen screen = Minecraft.getInstance().screen;

            colorComponent = new BackgroundColorSelectorComponent(this.screen, getX() - 50, getY() + height + 3, 120, 61);
            screen.renderables.add(renderButton);
            colorComponent.children().forEach(listener -> ((ScreenAccessor) screen).dragonSurvival$children().add(listener));
        } else {
            colorComponent.children().forEach(component -> screen.children().removeIf(other -> component == other));
            screen.children().removeIf(listener -> listener == colorComponent);
            screen.renderables.removeIf(renderable -> renderable == renderButton);
        }

        toggled = !toggled;
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        active = !screen.preset.skinAges.get(screen.level).get().defaultSkin;

        if (toggled && (!visible || !isMouseOver(mouseX, mouseY) && (colorComponent == null || !colorComponent.isMouseOver(mouseX, mouseY)))) {
            toggled = false;
            Screen screen = Minecraft.getInstance().screen;
            colorComponent.children().forEach(component -> screen.children().removeIf(other -> component == other));
            screen.children().removeIf(s -> s == colorComponent);
            screen.renderables.removeIf(s -> s == renderButton);
        }

        if (visible) {
            guiGraphics.blit(BUTTON_TEXTURE, getX() + 3, getY() + 3, 0, 0, width - 6, height - 6, width - 6, height - 6);
        }
    }
}