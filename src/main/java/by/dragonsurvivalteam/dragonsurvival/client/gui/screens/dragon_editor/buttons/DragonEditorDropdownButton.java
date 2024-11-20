package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader.DefaultPartLoader;
import by.dragonsurvivalteam.dragonsurvival.mixins.client.ScreenAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class DragonEditorDropdownButton extends DropDownButton {
    private final DragonEditorScreen dragonEditorScreen;
    private final EnumSkinLayer layers;

    public DragonEditorDropdownButton(DragonEditorScreen dragonEditorScreen, int x, int y, int xSize, int ySize, String current, String[] values, EnumSkinLayer layers) {
        super(x, y, xSize, ySize, current, values, selected -> {
            dragonEditorScreen.actionHistory.add(new DragonEditorScreen.EditorAction<>(dragonEditorScreen.dragonPartSelectAction, new Pair<>(layers, selected)));
        });
        this.dragonEditorScreen = dragonEditorScreen;
        this.layers = layers;
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTicks) {
        active = visible = dragonEditorScreen.showUi;
        super.renderWidget(guiGraphics, mouseX, mouseY, pPartialTicks);
        String currentValue = DragonEditorScreen.partToTranslation(dragonEditorScreen.preset.get(dragonEditorScreen.dragonLevel.getKey()).get().layerSettings.get(layers).get().selectedSkin);

        if (!Objects.equals(currentValue, current)) {
            current = currentValue;
            updateMessage();
        }

        List<String> valueList = DragonEditorHandler.getDragonPartKeys(dragonEditorScreen.dragonType, dragonEditorScreen.dragonBody, layers);

        if (layers != EnumSkinLayer.BASE) {
            valueList.addFirst(DefaultPartLoader.NO_PART);
        }

        valueList = valueList.stream().map(DragonEditorScreen::partToTranslation).toList();

        values = valueList.toArray(new String[0]);
        active = !dragonEditorScreen.preset.get(dragonEditorScreen.dragonLevel.getKey()).get().defaultSkin;
    }

    @Override
    public void onPress() {
        Screen screen = Minecraft.getInstance().screen;

        if (screen == null) {
            return;
        }

        if (!toggled) {
            int offset = screen.height - (getY() + height + 80);
            list = new DropdownList(getX(), getY() + height + Math.min(offset, 0), width, (int) (Math.max(1, Math.min(values.length, maxItems)) * (height * 1.5f)), 16);
            DropdownEntry center = null;

            for (String translationKey : values) {
                DropdownEntry ent = createEntry(translationKey);
                list.addEntry(ent);

                if (Objects.equals(translationKey, current))
                    center = ent;
            }

            if (center != null)
                list.centerScrollOn(center);

            boolean hasBorder = false;
            if (!screen.children().isEmpty()) {
                screen.renderables.addFirst(list);
                screen.renderables.add(list);

                ((ScreenAccessor) screen).dragonSurvival$children().addFirst(list);
                ((ScreenAccessor) screen).dragonSurvival$children().add(list);

                for (GuiEventListener child : screen.children())
                    if (child instanceof ContainerObjectSelectionList) {
                        if (((ContainerObjectSelectionList<?>) child).visible) {
                            hasBorder = true;
                            break;
                        }
                    }
            } else {
                ((ScreenAccessor) screen).dragonSurvival$children().add(list);
                screen.renderables.add(list);
            }

            boolean finalHasBorder = hasBorder;
            renderButton = new ExtendedButton(0, 0, 0, 0, Component.empty(), pButton -> {
            }) {
                @Override
                public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                    active = visible = false;
                    list.visible = DragonEditorDropdownButton.this.visible;

                    if (finalHasBorder)
                        RenderSystem.enableScissor(0, (int) (32 * Minecraft.getInstance().getWindow().getGuiScale()), Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight() - (int) (32 * Minecraft.getInstance().getWindow().getGuiScale()) * 2);

                    if (list.visible)
                        list.render(graphics, mouseX, mouseY, partialTick);

                    if (finalHasBorder)
                        RenderSystem.disableScissor();
                }
            };
            ((ScreenAccessor) screen).dragonSurvival$children().add(renderButton);
            screen.renderables.add(renderButton);
        } else {
            screen.children().removeIf(s -> s == list || s == renderButton);
            screen.renderables.removeIf(s -> s == list || s == renderButton);
        }

        toggled = !toggled;
        updateMessage();
    }

    public DropdownEntry createEntry(String translationKEy) {
        return new DragonDropdownValueEntry(this, translationKEy, setter);
    }
}