package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.components.BackgroundColorSelectorComponent;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;

public class BackgroundColorButton extends ExtendedButton{

	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	public boolean toggled;
	public int xSize, ySize;
	private BackgroundColorSelectorComponent colorComponent;
	private Widget renderButton;
	private final DragonEditorScreen screen;


	public BackgroundColorButton(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler, DragonEditorScreen dragonEditorScreen){
		super(xPos, yPos, width, height, displayString, handler);
		this.xSize = width;
		this.ySize = height;
		this.screen = dragonEditorScreen;
	}

	@Override
	public void onPress(){
		if(!toggled){
			renderButton = new ExtendedButton(0, 0, 0, 0, StringTextComponent.EMPTY, null){
				@Override
				public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
					this.active = this.visible = false;

					if(colorComponent != null){
						colorComponent.visible = BackgroundColorButton.this.visible;
						if(colorComponent.visible){
							colorComponent.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
						}
					}
				}
			};

			Screen screen = Minecraft.getInstance().screen;

			colorComponent = new BackgroundColorSelectorComponent(this.screen, x - 50, y + height + 3, 120, 61);
			screen.children.add(0, colorComponent);
			screen.children.add(colorComponent);
			screen.buttons.add(renderButton);
		}else{
			screen.children.removeIf((s) -> s == colorComponent);
			screen.buttons.removeIf((s) -> s == renderButton);
		}

		toggled = !toggled;
	}

	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
		this.active = !screen.preset.skinAges.get(screen.level).defaultSkin;

		if(toggled && (!visible || (!isMouseOver(mouseX, mouseY) && (colorComponent == null || !colorComponent.isMouseOver(mouseX, mouseY))))){
			toggled = false;
			Screen screen = Minecraft.getInstance().screen;
			screen.children.removeIf((s) -> s == colorComponent);
			screen.buttons.removeIf((s) -> s == renderButton);
		}

		if(visible){
			Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
			GuiUtils.drawContinuousTexturedBox(mStack, x, y, 0, 0, width, height, 32, 32, 10, 0);
			Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/background_color_button.png"));
			blit(mStack, x + 3, y + 3, 0, 0, width - 6, height - 6, width - 6, height - 6);

			if(this.isHovered()){
				this.renderToolTip(mStack, mouseX, mouseY);
			}
		}
	}

	@Override
	public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_){
		GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.background_color")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
	}
}