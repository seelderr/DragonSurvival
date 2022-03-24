package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.components.CopyEditorSettingsComponent;
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

public class CopySettingsButton extends ExtendedButton{

	public boolean toggled;
	private CopyEditorSettingsComponent component;
	private Widget renderButton;
	private final DragonEditorScreen screen;

	public CopySettingsButton(DragonEditorScreen screen, int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler){
		super(xPos, yPos, width, height, displayString, handler);
		this.screen = screen;
	}


	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		this.active = this.visible = screen.showUi;
		super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

		if(visible){
			Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/copy_icon.png"));
			blit(pMatrixStack, x, y, 0, 0, 16, 16, 16, 16);
		}

		if(toggled && (!visible || (!isMouseOver(pMouseX, pMouseY) && (component == null || !component.isMouseOver(pMouseX, pMouseY))))){
			toggled = false;
			Screen screen = Minecraft.getInstance().screen;
			screen.children.removeIf((s) -> s == component);
			screen.buttons.removeIf((s) -> s == renderButton);
		}

		if(this.isHovered() && (component != null && !component.isMouseOver(pMouseX, pMouseY) || !toggled)){
			this.renderToolTip(pMatrixStack, pMouseX, pMouseY);
		}
	}

	@Override
	public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_){
		GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.copy")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
	}

	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){}

	@Override
	public void onPress(){
		if(!toggled){
			renderButton = new ExtendedButton(0, 0, 0, 0, StringTextComponent.EMPTY, null){
				@Override
				public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
					this.active = this.visible = false;

					if(component != null){
						component.visible = CopySettingsButton.this.visible;

						if(component.visible){
							component.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
						}
					}
				}
			};

			int offset = screen.height - (y + 80);
			component = new CopyEditorSettingsComponent(screen, this, x + width - 80, y + (Math.min(offset, 0)), 80, 70);
			screen.children.add(0, component);
			screen.children.add(component);
			screen.buttons.add(renderButton);
		}else{
			screen.children.removeIf((s) -> s == component);
			screen.buttons.removeIf((s) -> s == renderButton);
		}

		toggled = !toggled;
	}
}