package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons.EditorPartButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DragonEditorDropdownEntry extends DropdownEntry{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public int num;
	public DropDownButton source;
	public final List<EditorPartButton> children = new ArrayList<>();
	public DragonEditorDropdownEntry(DropDownButton source, int num){
		this.num = num;
		this.source = source;
	}

	public void render(MatrixStack pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks) {
		this.children.forEach((p_238519_5_) -> {
			p_238519_5_.y = pTop;
			p_238519_5_.visible = source.visible;
			p_238519_5_.active = (!Objects.equals(source.current, p_238519_5_.value));
			p_238519_5_.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		});
	}

	public List<? extends IGuiEventListener> children() {
		return this.children;
	}
}