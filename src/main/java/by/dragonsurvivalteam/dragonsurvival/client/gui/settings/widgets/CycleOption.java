package by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class CycleOption<T> extends Option {
    private final CycleOption.OptionSetter<T> setter;
    private final Function<Options, T> getter;
    private final Supplier<CycleButton.Builder<T>> buttonSetup;
    private Function<Minecraft, OptionInstance.TooltipSupplier<T>> tooltip = minecraft -> ignored -> Tooltip.create(Component.empty());

    public CycleOption(String pCaptionKey, Function<Options, T> pGetter, CycleOption.OptionSetter<T> pSetter, Supplier<CycleButton.Builder<T>> pButtonSetup) {
        super(pCaptionKey);
        this.getter = pGetter;
        this.setter = pSetter;
        this.buttonSetup = pButtonSetup;
    }

    public static <T> CycleOption<T> create(String pCaptionKey, List<T> pValues, Function<T, Component> pValueStringifier, Function<Options, T> pGetter, CycleOption.OptionSetter<T> pSetter) {
        return new CycleOption<>(pCaptionKey, pGetter, pSetter, () -> CycleButton.builder(pValueStringifier).withValues(pValues));
    }

    public static <T> CycleOption<T> create(String pCaptionKey, Supplier<List<T>> pValues, Function<T, Component> pValueStringifier, Function<Options, T> pGetter, CycleOption.OptionSetter<T> pSetter) {
        return new CycleOption<>(pCaptionKey, pGetter, pSetter, () -> CycleButton.builder(pValueStringifier).withValues(pValues.get()));
    }

    public static <T> CycleOption<T> create(String pCaptionKey, List<T> pDefaultList, List<T> pSelectedList, BooleanSupplier pAltListSelector, Function<T, Component> pValueStringifier, Function<Options, T> pGetter, CycleOption.OptionSetter<T> pSetter) {
        return new CycleOption<>(pCaptionKey, pGetter, pSetter, () -> CycleButton.builder(pValueStringifier).withValues(pAltListSelector, pDefaultList, pSelectedList));
    }

    public static <T> CycleOption<T> create(String pCaptionKey, T[] pValues, Function<T, Component> pValueStringifier, Function<Options, T> pGetter, CycleOption.OptionSetter<T> pSetter) {
        return new CycleOption<>(pCaptionKey, pGetter, pSetter, () -> CycleButton.builder(pValueStringifier).withValues(pValues));
    }

    public static CycleOption<Boolean> createBinaryOption(String pCaptionKey, Component pDefaultValue, Component pSelectedValue, Function<Options, Boolean> pGetter, CycleOption.OptionSetter<Boolean> pSetter) {
        return new CycleOption<>(pCaptionKey, pGetter, pSetter, () -> CycleButton.booleanBuilder(pDefaultValue, pSelectedValue));
    }

    public static CycleOption<Boolean> createOnOff(String pCaptionKey, Function<Options, Boolean> pGetter, CycleOption.OptionSetter<Boolean> pSetter) {
        return new CycleOption<>(pCaptionKey, pGetter, pSetter, CycleButton::onOffBuilder);
    }

    public static CycleOption<Boolean> createOnOff(String pCaptionKey, Component pDefaultValue, Function<Options, Boolean> pGetter, CycleOption.OptionSetter<Boolean> pSetter) {
        return createOnOff(pCaptionKey, pGetter, pSetter).setTooltip((p_167791_) -> {
            List<FormattedCharSequence> list = p_167791_.font.split(pDefaultValue, 200);
            return ignored -> Tooltip.create(Component.literal(list.toString())); // FIXME 1.20
        });
    }

    public CycleOption<T> setTooltip(Function<Minecraft, OptionInstance.TooltipSupplier<T>> pTooltip) {
        this.tooltip = pTooltip;
        return this;
    }

    public AbstractWidget createButton(Options pOptions, int pX, int pY, int pWidth) {
        OptionInstance.TooltipSupplier<T> tooltipsupplier = this.tooltip.apply(Minecraft.getInstance());
        return this.buttonSetup.get().withTooltip(tooltipsupplier).withInitialValue(this.getter.apply(pOptions)).create(pX, pY, pWidth, 20, this.getCaption(), (p_167725_, p_167726_) -> {
            this.setter.accept(pOptions, this, p_167726_);
            pOptions.save();
        });
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface OptionSetter<T> {
        void accept(Options pOptions, Option pOption, T pValue);
    }
}
