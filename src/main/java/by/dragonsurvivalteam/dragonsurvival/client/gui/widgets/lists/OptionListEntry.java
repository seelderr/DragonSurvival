package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists;

import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.Option;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public abstract class OptionListEntry extends Entry<OptionListEntry>{
	final List<AbstractWidget> children;
	public boolean visible = false;

	public OptionListEntry(Map<Option, AbstractWidget> pOptions){
		children = ImmutableList.copyOf(pOptions.values());
	}

	public abstract int getHeight();

	@Override
	public boolean isMouseOver(double p_231047_1_, double p_231047_3_){
		return Objects.equals(((OptionsList)list).getEntryAtPos(p_231047_1_, p_231047_3_), this);
	}
}