package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProgressOption extends Option {
    protected final float steps;
    protected final double minValue;
    protected double maxValue;
    private final Function<Options, Double> getter;
    private final BiConsumer<Options, Double> setter;
    private final BiFunction<Options, ProgressOption, Component> toString;
    private final Tooltip tooltip;

    public ProgressOption(String pCaptionKey, double pMinValue, double pMaxValue, float pSteps, Function<Options, Double> pGetter, BiConsumer<Options, Double> pSetter, BiFunction<Options, ProgressOption, Component> pToString, final Tooltip tooltip) {
        super(pCaptionKey);
        this.minValue = pMinValue;
        this.maxValue = pMaxValue;
        this.steps = pSteps;
        this.getter = pGetter;
        this.setter = pSetter;
        this.toString = pToString;
        this.tooltip = tooltip;
    }

    public AbstractWidget createButton(Options pOptions, int pX, int pY, int pWidth) {
        return new SliderButton(pOptions, pX, pY, pWidth, 20, this, tooltip);
    }

    public double toPct(double pValue) {
        return Mth.clamp((this.clamp(pValue) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
    }

    public double toValue(double pValue) {
        return this.clamp(Mth.lerp(Mth.clamp(pValue, 0.0D, 1.0D), this.minValue, this.maxValue));
    }

    private double clamp(double pValue) {
        if (this.steps > 0.0F) {
            pValue = (double)(this.steps * (float)Math.round(pValue / (double)this.steps));
        }

        return Mth.clamp(pValue, this.minValue, this.maxValue);
    }

    public double getMinValue() {
        return this.minValue;
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(float pValue) {
        this.maxValue = (double)pValue;
    }

    public void set(Options pOptions, double pValue) {
        this.setter.accept(pOptions, pValue);
    }

    public double get(Options pOptions) {
        return this.getter.apply(pOptions);
    }

    public Component getMessage(Options pOptions) {
        return this.toString.apply(pOptions, this);
    }
}