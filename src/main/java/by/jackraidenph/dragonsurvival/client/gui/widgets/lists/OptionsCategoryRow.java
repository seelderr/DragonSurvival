package by.jackraidenph.dragonsurvival.client.gui.widgets.lists;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

@OnlyIn( Dist.CLIENT)
public class OptionsCategoryRow extends AbstractOptionList.Entry<OptionsRowList.Row>
{
	private final ITextComponent name;
	private final int width;
	
	public OptionsCategoryRow(ITextComponent p_i232280_2_) {
		this.name = p_i232280_2_;
		this.width = Minecraft.getInstance().font.width(this.name);
	}
	
	public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
		Minecraft.getInstance().font.draw(p_230432_1_, this.name, (float)(Minecraft.getInstance().screen.width / 2 - this.width / 2), (float)(p_230432_3_ + p_230432_6_ - 9 - 1), 16777215);
	}
	
	public boolean changeFocus(boolean p_231049_1_) {
		return false;
	}
	
	public List<? extends IGuiEventListener> children() {
		return Collections.emptyList();
	}
}
