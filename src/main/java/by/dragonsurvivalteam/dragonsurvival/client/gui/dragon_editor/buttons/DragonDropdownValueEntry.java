package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownValueEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class DragonDropdownValueEntry extends DropdownValueEntry
{
	private final int num;
	private final String value;
	private final String localeString;
	private final Consumer<String> setter;
	private final DragonEditorDropdownButton source;
	private final Component message;
	private ExtendedButton button;

	public DragonDropdownValueEntry(DragonEditorDropdownButton source, int num, String value, String localeString, Consumer<String> setter) {
		super(source, num, value, setter);
		this.num = num;
		this.value = value;
		this.setter = setter;
		this.source = source;
		this.localeString = localeString;
		message = Component.translatable(localeString);
	}

	@Override
	public List<? extends GuiEventListener> children(){
		return ImmutableList.of(button);
	}

	@Override
	public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		if(button == null){
			if(list != null)
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight + 1, null, null){
					@Override
					public Component getMessage(){
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
		}else{
			button.y = pTop;
			button.visible = source.visible;
			button.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
		}
	}
}