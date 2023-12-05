package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Screen.class)
public class MixinScreen {
    /**
     Avoid getting focus when pressing certain keys (e.g. tab navigation) in the Chat window<br>
     Used together with {@link by.dragonsurvivalteam.dragonsurvival.client.emotes.EmoteMenuHandler#handleClicked(Screen, AbstractWidget, boolean)}<br>
     To make things work somewhat correctly
    */
    @ModifyVariable(method = "keyPressed", at = @At(value = "STORE"), name = "componentpath")
    public ComponentPath avoidFocus(final ComponentPath componentPath) {
        if (componentPath instanceof ComponentPath.Path path) {
            if (path.childPath().component().getClass().getName().contains("EmoteMenuHandler")) {
                return null;
            }
        }

        return componentPath;
    }
}
