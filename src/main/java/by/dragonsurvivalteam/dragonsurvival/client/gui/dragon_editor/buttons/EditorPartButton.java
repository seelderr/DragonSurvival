/*
package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.Locale;
import java.util.function.Consumer;

public class EditorPartButton extends ExtendedButton{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public MutableComponent message;
	private DragonEditorScreen screen;
	private final DragonStateHandler handler = new DragonStateHandler();
	private EnumSkinLayer layer;
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
		this.layer = layer;
		message = Component.translatable("ds.skin_part." + screen.type.getTypeName().toLowerCase(Locale.ROOT) + "." + value.toLowerCase(Locale.ROOT));

		if(!value.equals("None")){
			texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/editor_icons/" + screen.type.getTypeName().toLowerCase(Locale.ROOT) + "/" + layer.name.toLowerCase(Locale.ROOT) + "/" + value.toLowerCase(Locale.ROOT) + ".png");
		}
	}


	@Override
	public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial){
		int u = !active ? 32 : 0;
		int v = isHoveredOrFocused() && active ? 32 : 0;
		ScreenUtils.blitWithBorder(mStack, BACKGROUND_TEXTURE, x, y, u, v, width, height, 32, 32, 10, 10, 10, 10, (float)getBlitOffset());

		if(texture != null){
			RenderSystem.setShaderTexture(0, texture);
			blit(mStack, x + 3, y + 3, 0, 0, width - 6, height - 6, width - 6, height - 6);
		}

		TextRenderUtil.drawScaledTextSplit(mStack, x + 4, y + height - 10, 0.4f, message, getFGColor(), width - 9, 200);
	}

	@Override
	public void onPress(){
		super.onPress();
		screen.doAction();
		source.current = value;
		source.onPress();
		setter.accept(value);
	}
}*/
