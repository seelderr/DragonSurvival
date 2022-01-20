package by.jackraidenph.dragonsurvival.client.gui.widgets.lists;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@OnlyIn( Dist.CLIENT )
public abstract class OptionListEntry extends ContainerObjectSelectionList.Entry<OptionListEntry>
{
	final List<AbstractWidget> children;
	public OptionListEntry(Map<Option, AbstractWidget> pOptions) {
		this.children = ImmutableList.copyOf(pOptions.values());
	}
	public boolean visible = false;
	
	public abstract int getHeight();
	public boolean isMouseOver(double p_231047_1_, double p_231047_3_)
	{
		return Objects.equals(((OptionsList)this.list).getEntryAtPos(p_231047_1_, p_231047_3_), this);
	}
}
