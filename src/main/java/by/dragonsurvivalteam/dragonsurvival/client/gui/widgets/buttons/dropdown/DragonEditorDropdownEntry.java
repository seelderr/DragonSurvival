package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons.EditorPartButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DragonEditorDropdownEntry extends DropdownEntry {
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public final List<EditorPartButton> children = new ArrayList<>();
	public int num;
	public DropDownButton source;

	public DragonEditorDropdownEntry(DropDownButton source, int num){
		this.num = num;
		this.source = source;
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		children.forEach(button -> {
			button.setY(pTop);
			button.visible = source.visible;
			button.active = !Objects.equals(source.current, button.value);
			button.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
		});
	}

	@Override
	public List<? extends GuiEventListener> children(){
		return children;
	}

	@Override
	public List<? extends NarratableEntry> narratables(){
		return children;
	}
}