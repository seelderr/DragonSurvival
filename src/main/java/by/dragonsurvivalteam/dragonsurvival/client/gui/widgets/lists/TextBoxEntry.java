package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.List;

@OnlyIn( Dist.CLIENT )
public class TextBoxEntry extends OptionListEntry{
	public final Widget widget;
	public Widget removeButton;

	private final CategoryEntry category;

	public TextBoxEntry(OptionsList optionsList, Widget widget, CategoryEntry categoryEntry){
		this.widget = widget;
		this.category = categoryEntry;

		removeButton = new ExtendedButton(optionsList.getScrollbarPosition() - 32 - 25, 1, 50, 20, new StringTextComponent("Remove"), (btn) -> {
			for(OptionListEntry child : optionsList.children()){
				if(child.children().contains(widget)){
					optionsList.removeEntry(child);
					optionsList.scroll(-child.getHeight());
					return;
				}
			}
		});
	}

	public void render(MatrixStack pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		widget.y = pTop;
		widget.visible = getHeight() != 0 && visible;
		widget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

		removeButton.y = pTop;
		removeButton.visible = getHeight() != 0 && visible;
		removeButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
	}

	@Override
	public int getHeight(){
		if(category != null){
			CategoryEntry entry = category.parent;
			while(entry != null){
				if(!entry.enabled){
					return 0;
				}else{
					entry = entry.parent;
				}
			}
		}

		return category == null || category.enabled ? 20 : 0;
	}

	public List<? extends IGuiEventListener> children(){
		return ImmutableList.of(this.widget, this.removeButton);
	}
}