package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class Option {
    private final Component caption;

    public Option(String pCaptionKey) {
        this.caption = Component.translatable(pCaptionKey);
    }

    public abstract AbstractWidget createButton(Options pOptions, int pX, int pY, int pWidth);

    public Component getCaption() {
        return this.caption;
    }

    protected Component pixelValueLabel(int pValue) {
        return Component.translatable("options.pixel_value", this.getCaption(), pValue);
    }

    protected Component percentValueLabel(double pPercentage) {
        return Component.translatable("options.percent_value", this.getCaption(), (int)(pPercentage * 100.0D));
    }

    protected Component percentAddValueLabel(int pDoubleValue) {
        return Component.translatable("options.percent_add_value", this.getCaption(), pDoubleValue);
    }

    protected Component genericValueLabel(Component pValueMessage) {
        return Component.translatable("options.generic_value", this.getCaption(), pValueMessage);
    }

    protected Component genericValueLabel(int pValue) {
        return this.genericValueLabel(Component.literal(Integer.toString(pValue)));
    }
}
