package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
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

public class DropdownValueEntry extends DropdownEntry{
	private final int num;
	private final String value;
	private final Consumer<String> setter;

	private ExtendedButton button;
	private final DropDownButton source;

	private final StringTextComponent message;

	public DropdownValueEntry(DropDownButton source, int num, String value, Consumer<String> setter){
		this.num = num;
		this.value = value;
		this.setter = setter;
		this.source = source;
		message = new StringTextComponent((value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT)).replace("_", " "));
	}

	@Override
	public List<? extends IGuiEventListener> children(){
		return ImmutableList.of(button);
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		if(button == null){
			if(list != null){
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight, null, null){
					@Override
					public ITextComponent getMessage(){
						return message;
					}

					@Override
					public void onPress(){
						source.current = value;
						source.onPress();
						setter.accept(value);
					}

					@Override
					public int getBlitOffset(){
						return 10;
					}
				};
			}
		}else{
			button.y = pTop;
			button.visible = source.visible;
			button.active = (!Objects.equals(source.current, value));
			button.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		}
	}
}