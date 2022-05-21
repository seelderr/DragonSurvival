package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ResetSettingsButton;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@OnlyIn( Dist.CLIENT )
public class OptionEntry extends OptionListEntry{
	public final AbstractWidget widget;
	final Map<Option, AbstractWidget> options;
	private final int width;
	private final Option option;
	public AbstractWidget resetButton;
	public Component key;
	public CategoryEntry category;

	public OptionEntry(Map<Option, AbstractWidget> pOptions, Option option, Component textComponent, AbstractWidget widget, CategoryEntry categoryEntry){
		super(pOptions);
		this.widget = widget;
		this.category = categoryEntry;
		this.key = textComponent;
		this.option = option;
		this.width = Minecraft.getInstance().font.width(key);
		this.options = pOptions;

		resetButton = new ResetSettingsButton(widget.x + 3 + widget.getWidth() + (categoryEntry != null && categoryEntry.parent != null ? 0 : 1), 0, option);
	}

	@Override
	public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		int indent = category != null ? category.indent : 0;

		if(getHeight() != 0){
			int color = new Color(0.1F, 0.1F, 0.1F, 0.85F).getRGB();

			if(isMouseOver(pMouseX, pMouseY))
				color = new Color(0.2F, 0.2F, 0.2F, 0.85F).getRGB();

			Gui.fill(pPoseStack, 32 + indent, pTop, ((OptionsList)list).getScrollbarPosition(), pTop + getHeight(), color);

			Font font = Minecraft.getInstance().font;
			font.draw(pPoseStack, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(key, ((OptionsList)list).getScrollbarPosition() - 32 - indent - 180))), 40 + indent, (float)(pTop + 6), 16777215);
		}
		widget.y = pTop;
		widget.visible = getHeight() != 0 && visible;
		widget.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);

		resetButton.y = pTop;
		resetButton.visible = getHeight() != 0 && visible;
		resetButton.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
	}

	@Override
	public int getHeight(){
		if(category != null){
			CategoryEntry entry = category.parent;
			while(entry != null)
				if(!entry.enabled){
					return 0;
				}else{
					entry = entry.parent;
				}
		}

		return category == null || category.enabled ? 20 : 0;
	}

	@Override
	public List<? extends GuiEventListener> children(){
		return ImmutableList.of(this.widget, this.resetButton);
	}

	@Override
	public List<? extends NarratableEntry> narratables(){
		return Collections.emptyList();
	}
}