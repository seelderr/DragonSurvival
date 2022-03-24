package by.dragonsurvivalteam.dragonsurvival.client.gui.components;

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.CopySettingsButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ExtendedCheckbox;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;
import java.util.List;

public class CopyEditorSettingsComponent extends FocusableGui implements IRenderable{
	public boolean visible;

	private final ExtendedButton confirm;
	private final ExtendedButton cancel;
	private final CheckboxButton newborn;
	private final CheckboxButton young;
	private final CheckboxButton adult;

	private final CopySettingsButton btn;
	private final DragonEditorScreen screen;
	private final int x;
	private final int y;
	private final int xSize;
	private final int ySize;

	public CopyEditorSettingsComponent(DragonEditorScreen screen, CopySettingsButton btn, int x, int y, int xSize, int ySize){
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		this.btn = btn;

		confirm = new ExtendedButton(x + (xSize / 2) - 18, y + ySize - 15, 15, 15, StringTextComponent.EMPTY, null){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				mStack.pushPose();
				mStack.translate(0, 0, 100);
				setMessage(StringTextComponent.EMPTY);
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(DragonAltarGUI.CONFIRM_BUTTON);
				blit(mStack, x + 1, y, 0, 0, 15, 15, 15, 15);
				mStack.popPose();

				if(isHovered){
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.tooltip.done")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}

			@Override
			public void onPress(){
				SkinAgeGroup preset = screen.preset.skinAges.getOrDefault(screen.level, new SkinAgeGroup(screen.level));

				screen.doAction();

				if(newborn.active && newborn.selected()){
					SkinAgeGroup ageGroup = new SkinAgeGroup(DragonLevel.BABY);
					ageGroup.readNBT(preset.writeNBT());
					screen.preset.skinAges.put(DragonLevel.BABY, ageGroup);
				}

				if(young.active && young.selected()){
					SkinAgeGroup ageGroup = new SkinAgeGroup(DragonLevel.YOUNG);
					ageGroup.readNBT(preset.writeNBT());
					screen.preset.skinAges.put(DragonLevel.YOUNG, ageGroup);
				}

				if(adult.active && adult.selected()){
					SkinAgeGroup ageGroup = new SkinAgeGroup(DragonLevel.ADULT);
					ageGroup.readNBT(preset.writeNBT());
					screen.preset.skinAges.put(DragonLevel.ADULT, ageGroup);
				}

				screen.update();
				btn.onPress();
			}
		};

		cancel = new ExtendedButton(x + (xSize / 2) + 3, y + ySize - 15, 15, 15, StringTextComponent.EMPTY, null){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
				mStack.pushPose();
				mStack.translate(0, 0, 100);
				setMessage(StringTextComponent.EMPTY);
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(DragonAltarGUI.CANCEL_BUTTON);
				blit(mStack, x, y, 0, 0, 15, 15, 15, 15);
				mStack.popPose();

				if(isHovered){
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.tooltip.cancel")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}


			@Override
			public void onPress(){
				btn.onPress();
			}
		};

		newborn = new ExtendedCheckbox(x + 5, y + 12, xSize - 10, 10, 10, new TranslationTextComponent("ds.level.newborn"), false, (s) -> {}){
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
				if(screen.level == DragonLevel.BABY){
					selected = true;
					active = false;
				}else{
					active = true;
				}
			}
		};
		young = new ExtendedCheckbox(x + 5, y + 27, xSize - 10, 10, 10, new TranslationTextComponent("ds.level.young"), false, (s) -> {}){
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
				if(screen.level == DragonLevel.YOUNG){
					selected = true;
					active = false;
				}else{
					active = true;
				}
			}
		};
		adult = new ExtendedCheckbox(x + 5, y + 27 + 15, xSize - 10, 10, 10, new TranslationTextComponent("ds.level.adult"), false, (s) -> {}){
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
				if(screen.level == DragonLevel.ADULT){
					selected = true;
					active = false;
				}else{
					active = true;
				}
			}
		};
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY){
		return visible && pMouseY >= (double)this.y - 3 && pMouseY <= (double)this.y + ySize + 3 && pMouseX >= (double)this.x && pMouseX <= (double)this.x + xSize;
	}

	@Override
	public List<? extends IGuiEventListener> children(){
		return ImmutableList.of(confirm, cancel, newborn, young, adult);
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		Minecraft.getInstance().textureManager.bind(DropdownList.BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y - 3, 0, 0, xSize, ySize + 6, 32, 32, 10, 10);
		confirm.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		cancel.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		newborn.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		young.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		adult.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		AbstractGui.drawCenteredString(pMatrixStack, Minecraft.getInstance().font, new TranslationTextComponent("ds.gui.dragon_editor.copy_to"), x + (xSize / 2), y + 1, 14737632);
	}
}