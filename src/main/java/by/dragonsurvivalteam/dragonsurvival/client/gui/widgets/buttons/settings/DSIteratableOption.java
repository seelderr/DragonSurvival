package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@OnlyIn( Dist.CLIENT )
public class DSIteratableOption extends IteratableOption{
	private Widget btn;

	public DSIteratableOption(String p_i51164_1_, BiConsumer<GameSettings, Integer> p_i51164_2_, BiFunction<GameSettings, IteratableOption, ITextComponent> p_i51164_3_){
		super(p_i51164_1_, p_i51164_2_, p_i51164_3_);
	}

	@Override
	public Optional<List<IReorderingProcessor>> getTooltip(){
		return btn.isHovered() ? Optional.empty() : super.getTooltip();
	}

	public Widget createButton(GameSettings pOptions, int pX, int pY, int pWidth){
		this.btn = super.createButton(pOptions, pX, pY, pWidth);
		return btn;
	}
}