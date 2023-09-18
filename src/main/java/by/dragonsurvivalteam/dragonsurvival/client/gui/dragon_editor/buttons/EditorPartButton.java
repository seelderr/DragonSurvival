package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Consumer;

public class EditorPartButton extends ExtendedButton{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public MutableComponent message;
	private final DragonEditorScreen screen;
	public String value;
	public Consumer<String> setter;
	public DropDownButton source;
	private ResourceLocation texture;

	public EditorPartButton(DragonEditorScreen screen, DropDownButton source, int xPos, int yPos, int width, int height, String value, Consumer<String> setter, EnumSkinLayer layer){
		super(xPos, yPos, width, height, Component.empty(), s -> {});
		this.value = value;
		this.setter = setter;
		this.source = source;
		this.screen = screen;
		message = Component.translatable("ds.skin_part." + screen.type.getTypeName().toLowerCase(Locale.ROOT) + "." + value.toLowerCase(Locale.ROOT));

		if(!value.equals("None")){
			texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/editor_icons/" + screen.type.getTypeName().toLowerCase(Locale.ROOT) + "/" + layer.name.toLowerCase(Locale.ROOT) + "/" + value.toLowerCase(Locale.ROOT) + ".png");
		}
	}


	@Override
	public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
		// Render above DropdownList / DropDownButton
		int zLevel = 375;

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, zLevel);

		int u = !active ? 32 : 0;
		int v = isHoveredOrFocused() && active ? 32 : 0;
		guiGraphics.blitWithBorder(BACKGROUND_TEXTURE, getX(), getY(), u, v, width, height, 32, 32, 10, 10, 10, 10/*, (float)getBlitOffset()*/);

		if(texture != null){
			guiGraphics.blit(texture, getX() + 3, getY() + 3, 0, 0, width - 6, height - 6, width - 6, height - 6);
		}

		TextRenderUtil.drawScaledTextSplit(guiGraphics, getX() + 4, getY() + height - 10, 0.4f, message, getFGColor(), width - 9, zLevel);
		guiGraphics.pose().popPose();
	}

	@Override
	public void onPress(){
		super.onPress();
		screen.doAction();
		source.current = value;
		source.onPress();
		setter.accept(value);
	}
}