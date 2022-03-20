package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class DSBooleanOption extends BooleanOption{
	private Widget btn;

	public DSBooleanOption(String p_i244779_1_,
		@Nullable
			ITextComponent p_i244779_2_, Predicate<GameSettings> p_i244779_3_, BiConsumer<GameSettings, Boolean> p_i244779_4_){
		super(p_i244779_1_, p_i244779_2_, p_i244779_3_, p_i244779_4_);
	}

	@Override
	public Optional<List<IReorderingProcessor>> getTooltip(){
		return btn.isHovered() ? Optional.empty() : super.getTooltip();
	}

	public Widget createButton(GameSettings pOptions, int pX, int pY, int pWidth){
		this.btn = super.createButton(pOptions, pX, pY, pWidth);
		return btn;
	}

	public ITextComponent getMessage(GameSettings p_238152_1_){
		boolean val = this.get(p_238152_1_);
		return new TranslationTextComponent(val ? "options.on" : "options.off").withStyle(val ? TextFormatting.GREEN : TextFormatting.RED);
	}
}