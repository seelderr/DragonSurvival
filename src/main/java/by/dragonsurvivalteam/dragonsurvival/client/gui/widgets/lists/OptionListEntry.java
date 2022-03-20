package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists;

import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

@OnlyIn( Dist.CLIENT )
public abstract class OptionListEntry extends AbstractOptionList.Entry<OptionListEntry>{
	public boolean visible = false;

	public abstract int getHeight();

	public boolean isMouseOver(double p_231047_1_, double p_231047_3_){
		return Objects.equals(((OptionsList)this.list).getEntryAtPos(p_231047_1_, p_231047_3_), this);
	}
}