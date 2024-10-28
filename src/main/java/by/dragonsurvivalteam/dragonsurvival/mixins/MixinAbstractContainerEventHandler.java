package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.emotes.EmoteMenuHandler;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerEventHandler.class)
public abstract class MixinAbstractContainerEventHandler {
    /**
     * Avoid gaining focus (e.g. tab- or arrow-navigation)<br>
     * Otherwise the user will no longer be able to type in the chat box until re-opening the chat window
     */
    @Inject(method = "setFocused", at = @At("HEAD"), cancellable = true)
    private void skipFocus(final GuiEventListener listener, final CallbackInfo callback) {
        if ((Object) this instanceof ChatScreen chatScreen) {
            /* TODO
                The button for setting the keybind should probably retain focus
                Currently the keybind also gets entered in the chat window - minor problem
            */
            if (listener instanceof Button && listener.getClass().getName().contains("EmoteMenuHandler")) {
                EmoteMenuHandler.focusChatBox(chatScreen);
                callback.cancel();
            }
        }
    }
}
