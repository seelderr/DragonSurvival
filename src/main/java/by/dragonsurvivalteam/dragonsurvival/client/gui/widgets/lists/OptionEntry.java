package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ResetSettingsButton;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;
import java.util.List;

@OnlyIn( Dist.CLIENT )
public class OptionEntry extends OptionListEntry{
	public final Widget widget;
	private final int width;
	public Widget resetButton;
	public ITextComponent key;
	public CategoryEntry category;
	private final AbstractOption option;

	public OptionEntry(AbstractOption option, ITextComponent textComponent, Widget widget, CategoryEntry categoryEntry){
		this.widget = widget;
		this.category = categoryEntry;
		this.key = textComponent;
		this.option = option;
		this.width = Minecraft.getInstance().font.width(key);

		resetButton = new ResetSettingsButton(widget.x + 3 + widget.getWidth() + (categoryEntry != null && categoryEntry.parent != null ? 0 : 1), 0, option);
	}

	public void render(MatrixStack pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		int indent = (category != null ? category.indent : 0);

		if(getHeight() != 0){
			int color = new Color(0.1F, 0.1F, 0.1F, 0.85F).getRGB();

			if(isMouseOver(pMouseX, pMouseY)){
				color = new Color(0.2F, 0.2F, 0.2F, 0.85F).getRGB();
			}

			AbstractGui.fill(pMatrixStack, 32 + indent, pTop, ((OptionsList)list).getScrollbarPosition(), (pTop + getHeight()), color);

			FontRenderer font = Minecraft.getInstance().font;
			font.draw(pMatrixStack, LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(key, ((OptionsList)list).getScrollbarPosition() - 32 - indent - 180))), 40 + indent, (float)(pTop + 6), 16777215);
		}
		widget.y = pTop;
		widget.visible = getHeight() != 0 && visible;
		widget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

		resetButton.y = pTop;
		resetButton.visible = getHeight() != 0 && visible;
		resetButton.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
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
		return ImmutableList.of(this.widget, this.resetButton);
	}
}