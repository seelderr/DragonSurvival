package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.jackraidenph.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class ColoredDropdownValueEntry extends DropdownEntry
{
	public int num;
	public String value;
	public Consumer<String> setter;
	
	public ExtendedButton button;
	public DropDownButton source;
	private DragonEditorScreen screen;
	
	private StringTextComponent message;
	
	public ColoredDropdownValueEntry(DragonEditorScreen screen, DropDownButton source, int num, String value, Consumer<String> setter)
	{
		this.num = num;
		this.value = value;
		this.setter = setter;
		this.source = source;
		this.screen = screen;
		message = new StringTextComponent(value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT));
	}
	
	@Override
	public List<? extends IGuiEventListener> children()
	{
		return ImmutableList.of(button);
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks)
	{
		if(button == null) {
			if(list != null) {
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight+1, null, null)
				{
					@Override
					public ITextComponent getMessage()
					{
						return message;
					}
					
					@Override
					public void onPress()
					{
						source.current = value;
						source.onPress();
						setter.accept(value);
					}
					
					@Override
					public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
					{
						mStack.pushPose();
						mStack.translate(0, 0, 200);
						super.renderButton(mStack, mouseX, mouseY, partial);
						mStack.popPose();
					}
				};
			}
		}else {
			button.y = pTop;
			button.visible = source.visible;
			button.active = (!Objects.equals(source.current, value));
			button.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		}
	}
}
