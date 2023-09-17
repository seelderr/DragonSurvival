package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists;

import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.Option;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class OptionsList extends ContainerObjectSelectionList<OptionListEntry>{
	public static ConcurrentHashMap<Option, String> configMap = new ConcurrentHashMap<>();
	public static CopyOnWriteArrayList<Integer> activeCats = new CopyOnWriteArrayList<>();
	public int listWidth;

	public OptionsList(int listWidth, int height, int top, int bottom){
		super(Minecraft.getInstance(), listWidth, height, top, bottom, Minecraft.getInstance().font.lineHeight * 2 + 8);
		this.listWidth = listWidth;
		setRenderBackground(false);
	}


	public CategoryEntry addCategory(String p_214333_1_, CategoryEntry ent, int catNum){
		String name = p_214333_1_.substring(0, 1).toUpperCase(Locale.ROOT) + p_214333_1_.substring(1).replace("_", " ");
		CategoryEntry entry = new CategoryEntry(this, Component.empty().append(name), ent, catNum);
		entry.origName = p_214333_1_;
		addEntry(entry);
		return entry;
	}

	@Override
	public int addEntry(OptionListEntry p_230513_1_){
		return super.addEntry(p_230513_1_);
	}

	@Override
	protected int getMaxPosition(){
		int size = headerHeight;
		for(OptionListEntry ent : children())
			size += ent.getHeight();
		return size;
	}

	@Override
	public void centerScrollOn(OptionListEntry p_230951_1_){
		int num = children().indexOf(p_230951_1_);
		int size = 0;
		for(int i = 0; i < num; i++)
			size += getEntry(i).getHeight();

		setScrollAmount(size + p_230951_1_.getHeight() / 2 - (y1 - y0) / 2);
	}

	@Override
	protected void ensureVisible(OptionListEntry p_230954_1_){
		int i = getRowTop(children().indexOf(p_230954_1_));
		int j = i - y0 - 4 - p_230954_1_.getHeight();
		if(j < 0)
			scroll(j);

		int k = y1 - i - p_230954_1_.getHeight() - p_230954_1_.getHeight();
		if(k < 0)
			scroll(-k);
	}

	public void scroll(int p_230937_1_){
		setScrollAmount(getScrollAmount() + (double)p_230937_1_);
	}

	@Override
	protected int getRowTop(int index){
		int height = 0;

		for (int i = 0; i < index; i++) {
			OptionListEntry e = getEntry(i);
			height += e.getHeight();
		}

		return y0 + 4 - (int) getScrollAmount() + height - 4;
	}

	@Override
	public boolean removeEntry(OptionListEntry p_230956_1_){
		return super.removeEntry(p_230956_1_);
	}

	public void add(final Option[] options, final CategoryEntry entry) {
        for (Option option : options) {
            add(option, entry);
        }
	}

	public void add(Option option, CategoryEntry entry){
		AbstractWidget widget = option.createButton(minecraft.options, getScrollbarPosition() - 165, 0, 140);
		addEntry(new OptionEntry(ImmutableMap.of(option, widget), option, option.getCaption(), widget, entry));
	}

	@Override
	public int getScrollbarPosition(){
		return Minecraft.getInstance().screen.width - 32;
	}

	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_){
		updateScrollingState(p_231044_1_, p_231044_3_, p_231044_5_);
		if(!isMouseOver(p_231044_1_, p_231044_3_))
			return false;
		else{
			OptionListEntry e = getEntryAtPos(p_231044_1_, p_231044_3_);
			if(e != null){
				if(e.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_)){
					setFocused(e);
					setDragging(true);
					return true;
				}
			}else if(p_231044_5_ == 0){
				clickedHeader((int)(p_231044_1_ - (double)(x0 + width / 2 - getRowWidth() / 2)), (int)(p_231044_3_ - (double)y0) + (int)getScrollAmount() - 4);
				return true;
			}

			return scrolling;
		}
	}

	public OptionListEntry getEntryAtPos(double p_230933_1_, double p_230933_3_){
		int i = getRowWidth() / 2;
		int j = x0 + width / 2;
		int k = j - i;
		int l = j + i;
		int i1 = Mth.floor(p_230933_3_ - (double)y0) - headerHeight + (int)getScrollAmount() - 4;
		int curSize = 0;
		int j1 = 0;
		for(int g = 0; g < children().size(); g++){
			curSize += getEntry(g).getHeight();
			if(curSize >= i1){
				j1 = g;
				break;
			}
		}

		return p_230933_1_ < (double)getScrollbarPosition() && p_230933_1_ >= (double)k && p_230933_1_ <= (double)l && j1 >= 0 && i1 >= 0 && j1 < getItemCount() ? children().get(j1) : null;
	}

	@Override
	public int getRowWidth() {
		return listWidth;
	}

	/** Handles the rendering of the entries (but not the options when clicking on an entry) */
	@Override
	protected void renderList(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float partialTicks) {
		int itemCount = getItemCount();

		for (int index = 0; index < itemCount; index++) {
			int top = getRowTop(index);
			int bottom = getRowBottom(index);

			OptionListEntry entry = getEntry(index);
			entry.visible = bottom >= y0 + 16 && top <= y1 - 16;

			if (bottom >= y0 && top <= y1) {
				int entryHeight = entry.getHeight();
				int rowWidth = getRowWidth();
				int rowLeft = getRowLeft();
				boolean mouseOver = isMouseOver(pMouseX, pMouseY) && Objects.equals(getEntryAtPos(pMouseX, pMouseY), entry);
				entry.render(guiGraphics, index, top, rowLeft, rowWidth, entryHeight, pMouseX, pMouseY, mouseOver, partialTicks);
			}
		}
	}

	public int getRowBottom(int index) {
		return getRowTop(index) + getEntry(index).getHeight();
	}

	@Nullable
	public CategoryEntry findCategory(String text, String lastKey){
		for(OptionListEntry optionsrowlist$row : children())
			if(optionsrowlist$row instanceof CategoryEntry cat){
				
				if(cat.parent == null || cat.parent.origName.equals(lastKey)){
					if(cat.origName.equals(text)){
						return cat;
					}
				}
			}

		return null;
	}

	public OptionEntry findClosest(String text){
		OptionEntry closest = null;
		int dif = -1;

		for(OptionListEntry row : children())
			if(row instanceof OptionEntry ent){
				String difText = StringUtils.difference(ent.key.getString().toLowerCase(Locale.ROOT).replace(" ", ""), text.toLowerCase(Locale.ROOT).replace(" ", ""));
				if(difText.length() >= ent.key.getString().length()) continue;

				if(dif == -1 || difText.length() < dif){
					closest = ent;
					dif = difText.length();
				}
			}

		return closest;
	}

	@Nullable
	public AbstractWidget findOption(Option pOption){
		for(OptionListEntry optionslist$entry : children())
			if(optionslist$entry instanceof OptionEntry){
				AbstractWidget abstractwidget = ((OptionEntry)optionslist$entry).options.get(pOption);
				if(abstractwidget != null){
					return abstractwidget;
				}
			}

		return null;
	}

	public Optional<AbstractWidget> getMouseOver(double p_238518_1_, double p_238518_3_){
		for(OptionListEntry optionsrowlist$row : children())
			for(AbstractWidget widget : optionsrowlist$row.children){
				if(widget.isMouseOver(p_238518_1_, p_238518_3_)){
					return Optional.of(widget);
				}
			}

		return Optional.empty();
	}
}