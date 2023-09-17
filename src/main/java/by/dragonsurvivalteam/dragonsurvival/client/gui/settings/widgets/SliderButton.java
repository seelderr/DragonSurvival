package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;


import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SliderButton extends AbstractOptionSliderButton /*implements TooltipAccessor*/ {
    private final ProgressOption option;
    public List<FormattedCharSequence> tooltip;

    public SliderButton(Options pOptions, int pX, int pY, int pWidth, int pHeight, ProgressOption pProgressOption, List<FormattedCharSequence> pTooltip) {
        super(pOptions, pX, pY, pWidth, pHeight, (double)((float)pProgressOption.toPct(pProgressOption.get(pOptions))));
        this.option = pProgressOption;
        this.tooltip = pTooltip;
        this.updateMessage();
    }

    protected void applyValue() {
        this.option.set(this.options, this.option.toValue(this.value));
        this.options.save();
    }

    protected void updateMessage() {
        this.setMessage(this.option.getMessage(this.options));
    }

    public Tooltip getTooltip() {
        return Tooltip.create(Component.literal(tooltip.toString()));
    }
}