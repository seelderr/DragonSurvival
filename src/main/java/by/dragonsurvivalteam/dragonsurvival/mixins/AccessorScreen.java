package by.dragonsurvivalteam.dragonsurvival.mixins;

import java.util.List;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Screen.class)
public interface AccessorScreen {
	@Accessor("children")
	List<GuiEventListener> children();
}
