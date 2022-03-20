package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownValueEntry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DropDownButton extends ExtendedButton{
	public static final int maxItems = 4;
	public String current;
	public String[] values;
	public Consumer<String> setter;
	public boolean toggled;
	public DropdownList list;
	public ITextComponent message;
	public Widget renderButton;

	public DropDownButton(int x, int y, int xSize, int ySize, String current, String[] values, Consumer<String> setter){
		super(x, y, xSize, ySize, null, null);
		this.values = values;
		this.setter = setter;
		this.current = current;
		updateMessage();
	}

	public void updateMessage(){
		if(current != null){
			message = new StringTextComponent(current.substring(0, 1).toUpperCase(Locale.ROOT) + current.substring(1).toLowerCase(Locale.ROOT));
		}
	}

	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
		mStack.pushPose();
		mStack.translate(0, 0, 100);
		super.renderButton(mStack, mouseX, mouseY, partial);
		mStack.popPose();
	}

	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

		if(toggled && (!visible || (!isMouseOver(p_230430_2_, p_230430_3_) && !list.isMouseOver(p_230430_2_, p_230430_3_)))){
			toggled = false;
			Screen screen = Minecraft.getInstance().screen;
			screen.children.removeIf((s) -> s == list);
			screen.buttons.removeIf((s) -> s == renderButton);
		}


		if(toggled && list != null){
			Screen screen = Minecraft.getInstance().screen;
			int offset = screen.height - (y + height + 80);
			list.reposition(x, y + height + (Math.min(offset, 0)), width, (int)(Math.max(1, Math.min(values.length, maxItems)) * (height * 1.5f)));
		}
	}

	@Override
	public ITextComponent getMessage(){
		return message;
	}

	public void onClick(double pMouseX, double pMouseY){
		List<IGuiEventListener> list = Minecraft.getInstance().screen.children.stream().filter((s) -> s.isMouseOver(pMouseX, pMouseY)).collect(Collectors.toList());

		if(list.size() == 1 && list.get(0) == this){
			this.onPress();
		}
	}

	@Override
	public void onPress(){
		Screen screen = Minecraft.getInstance().screen;

		if(!toggled){
			int offset = screen.height - (y + height + 80);
			list = new DropdownList(x, y + height + (Math.min(offset, 0)), width, (int)(Math.max(1, Math.min(values.length, maxItems)) * (height * 1.5f)), 16);
			DropdownEntry center = null;

			for(int i = 0; i < values.length; i++){
				String val = values[i];
				DropdownEntry ent = createEntry(i, val);
				list.addEntry(ent);

				if(Objects.equals(val, current)){
					center = ent;
				}
			}

			if(center != null){
				list.centerScrollOn(center);
			}

			boolean hasBorder = false;
			if(screen.children.size() > 0){
				screen.children.add(0, list);
				screen.children.add(list);

				for(IGuiEventListener child : screen.children){
					if(child instanceof AbstractList){
						if(((AbstractList)child).renderTopAndBottom){
							hasBorder = true;
							break;
						}
					}
				}
			}else{
				screen.children.add(list);
			}

			boolean finalHasBorder = hasBorder;
			renderButton = new ExtendedButton(0, 0, 0, 0, StringTextComponent.EMPTY, null){
				@Override
				public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
					this.active = this.visible = false;
					list.visible = DropDownButton.this.visible;

					if(finalHasBorder){
						RenderSystem.enableScissor(0, (int)(32 * Minecraft.getInstance().getWindow().getGuiScale()), Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight() - (int)((32) * Minecraft.getInstance().getWindow().getGuiScale()) * 2);

					}

					if(list.visible){
						list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
					}

					if(finalHasBorder){
						RenderSystem.disableScissor();
					}
				}
			};
			screen.buttons.add(renderButton);
		}else{
			screen.children.removeIf((s) -> s == list);
			screen.buttons.removeIf((s) -> s == renderButton);
		}

		toggled = !toggled;
		updateMessage();
	}

	public DropdownEntry createEntry(int pos, String val){
		return new DropdownValueEntry(this, pos, val, setter);
	}
}