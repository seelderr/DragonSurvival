package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class DropdownValueEntry extends by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry{
	private final int num;
	private final String value;
	private final Consumer<String> setter;
	private final DropDownButton source;
	private final TextComponent message;
	private ExtendedButton button;

	public DropdownValueEntry(DropDownButton source, int num, String value, Consumer<String> setter){
		this.num = num;
		this.value = value;
		this.setter = setter;
		this.source = source;
		message = new TextComponent(value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT));
	}

	@Override
	public List<? extends GuiEventListener> children(){
		return ImmutableList.of(button);
	}

	@Override
	public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		if(button == null){
			if(list != null){
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight + 1, null, null){
					@Override
					public TextComponent getMessage(){
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
			button.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
		}
	}

	@Override
	public List<? extends NarratableEntry> narratables(){
		return null;
	}
}