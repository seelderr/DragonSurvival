package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownValueEntry;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class DragonDropdownValueEntry extends DropdownValueEntry {
    private final String value;
    private final Consumer<String> setter;
    private final DragonEditorDropdownButton source;
    private final Component message;
    private ExtendedButton button;

    public DragonDropdownValueEntry(DragonEditorDropdownButton source, String translationKey, Consumer<String> setter) {
        super(source, translationKey, setter);
        this.value = translationKey;
        this.setter = setter;
        this.source = source;
        message = Component.translatable(translationKey);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return ImmutableList.of(button);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks) {
        if (button == null) {
            button = new ExtendedButton(pLeft + 3, 0, pWidth - 12, pHeight + 1, message, action -> { /* Nothing to do */ }) {
                @Override
                public @NotNull Component getMessage() {
                    return message;
                }

                @Override
                public void onPress() {
                    source.current = value;
                    setter.accept(value);
                    source.onPress();
                }
            };
        } else {
            button.setY(pTop);
            button.visible = source.visible;
            button.render(graphics, pMouseX, pMouseY, pPartialTicks);
        }
    }
}