package by.jackraidenph.dragonsurvival.client.gui.widgets.lists;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OptionsList extends AbstractOptionList<OptionsList.Entry>
{
	public OptionsList(Minecraft p_i51130_1_, int p_i51130_2_, int p_i51130_3_, int p_i51130_4_, int p_i51130_5_, int p_i51130_6_) {
		super(p_i51130_1_, p_i51130_2_, p_i51130_3_, p_i51130_4_, p_i51130_5_, p_i51130_6_);
		this.centerListVertically = false;
	}
	
	public int addCategory(String p_214333_1_) {
		return this.addEntry(new CategoryEntry(new StringTextComponent(p_214333_1_)));
	}
	
	public int addBig(AbstractOption p_214333_1_) {
		return this.addEntry(OptionsList.OptionEntry.big(this.minecraft.options, this.width, p_214333_1_));
	}
	
	public void addSmall(AbstractOption p_214334_1_, @Nullable AbstractOption p_214334_2_) {
		this.addEntry(OptionsList.OptionEntry.small(this.minecraft.options, this.width, p_214334_1_, p_214334_2_));
	}
	
	public void addSmall(AbstractOption[] p_214335_1_) {
		for(int i = 0; i < p_214335_1_.length; i += 2) {
			this.addSmall(p_214335_1_[i], i < p_214335_1_.length - 1 ? p_214335_1_[i + 1] : null);
		}
		
	}
	
	public int getRowWidth() {
		return 400;
	}
	
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}
	
	@Nullable
	public Widget findOption(AbstractOption p_243271_1_) {
		for(OptionsList.Entry optionsrowlist$row : this.children()) {
			for(IGuiEventListener widget : optionsrowlist$row.children()) {
				if(widget instanceof Widget) {
					if (widget instanceof OptionButton && ((OptionButton)widget).getOption() == p_243271_1_) {
						return (Widget)widget;
					}
				}
			}
		}
		
		return null;
	}
	
	public Optional<Widget> getMouseOver(double p_238518_1_, double p_238518_3_) {
		for(OptionsList.Entry optionsrowlist$row : this.children()) {
			for(IGuiEventListener widget : optionsrowlist$row.children()) {
				if(widget instanceof Widget) {
					if (((Widget)widget).isMouseOver(p_238518_1_, p_238518_3_)) {
						return Optional.of((Widget)widget);
					}
				}
			}
		}
		
		return Optional.empty();
	}
	
	@OnlyIn( Dist.CLIENT)
	public abstract static class Entry extends AbstractOptionList.Entry<OptionsList.Entry> {
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class OptionEntry extends OptionsList.Entry {
		private final List<Widget> children;
		
		private OptionEntry(List<Widget> p_i50481_1_) {
			this.children = p_i50481_1_;
		}
		
		public static OptionsList.OptionEntry big(GameSettings p_214384_0_, int p_214384_1_, AbstractOption p_214384_2_) {
			return new OptionsList.OptionEntry(ImmutableList.of(p_214384_2_.createButton(p_214384_0_, p_214384_1_ / 2 - 155, 0, 310)));
		}
		
		public static OptionsList.OptionEntry small(GameSettings p_214382_0_, int p_214382_1_, AbstractOption p_214382_2_, @Nullable
				AbstractOption p_214382_3_) {
			Widget widget = p_214382_2_.createButton(p_214382_0_, p_214382_1_ / 2 - 155, 0, 150);
			return p_214382_3_ == null ? new OptionsList.OptionEntry(ImmutableList.of(widget)) : new OptionsList.OptionEntry(ImmutableList.of(widget, p_214382_3_.createButton(p_214382_0_, p_214382_1_ / 2 - 155 + 160, 0, 150)));
		}
		
		public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			this.children.forEach((p_238519_5_) -> {
				p_238519_5_.y = p_230432_3_;
				p_238519_5_.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
			});
		}
		
		public List<? extends IGuiEventListener> children() {
			return this.children;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public class CategoryEntry extends OptionsList.Entry {
		private final ITextComponent name;
		private final int width;
		
		public CategoryEntry(ITextComponent p_i232280_2_) {
			this.name = p_i232280_2_;
			this.width = OptionsList.this.minecraft.font.width(this.name);
		}
		
		public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			OptionsList.this.minecraft.font.draw(p_230432_1_, this.name, (float)(OptionsList.this.minecraft.screen.width / 2 - this.width / 2), (float)(p_230432_3_ + p_230432_6_ - 9 - 1), 16777215);
		}
		
		public boolean changeFocus(boolean p_231049_1_) {
			return false;
		}
		
		public List<? extends IGuiEventListener> children() {
			return Collections.emptyList();
		}
	}
}
