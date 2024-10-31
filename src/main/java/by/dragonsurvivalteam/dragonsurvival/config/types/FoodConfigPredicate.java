package by.dragonsurvivalteam.dragonsurvival.config.types;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class FoodConfigPredicate implements CustomConfig {
    private static final int NUTRITION = 2;
    private static final int SATURATION = 3;

    private final Predicate<Item> predicate;
    private final @Nullable Pair<Integer, Float> foodData;
    private final String originalData;

    private FoodConfigPredicate(final Predicate<Item> predicate, @Nullable final Pair<Integer, Float> foodData, final String originalData) {
        this.predicate = predicate;
        this.foodData = foodData;
        this.originalData = originalData;
    }

    @Override
    public String convert() {
        return originalData;
    }

    @Override
    public String toString() {
        return convert();
    }

    @Override
    public boolean equals(final Object object) {
        return super.equals(object) || object instanceof FoodConfigPredicate config && config.convert().equals(convert());
    }

    public Optional<FoodProperties> getFoodData(final Item item) {
        if (!predicate.test(item)) {
            return Optional.empty();
        }

        // Direct access to get the original data, bypassing other mods (including us)
        FoodProperties originalProperties = item.components().get(DataComponents.FOOD);

        int nutrition = 1;
        float saturation = 0;

        FoodProperties.Builder builder = new FoodProperties.Builder();

        if (originalProperties != null) {
            nutrition = originalProperties.nutrition();
            saturation = originalProperties.saturation();

            if (originalProperties.canAlwaysEat()) {
                builder.alwaysEdible();
            }

            // eat duration in ticks is 16 when fast eating is enabled
            if (originalProperties.eatDurationTicks() <= 16) {
                builder.fast();
            }

            originalProperties.effects().forEach(possibleEffect -> builder.effect(possibleEffect.effectSupplier(), possibleEffect.probability()));
        }

        if (foodData != null) {
            nutrition = foodData.first();
            saturation = foodData.second();
        }

        // saturation is calculated in 'FoodConstants#saturationByModifier' when the properties are built
        // The configured entries are expected to supply the "final" value they expect for saturation
        saturation = (saturation / nutrition) / 2;

        builder.nutrition(nutrition).saturationModifier(saturation);
        return Optional.of(builder.build());
    }

    public static FoodConfigPredicate of(final String location, int nutrition, float saturation) {
        Predicate<Item> predicate = ConfigUtils.itemPredicate(location.split(":"));
        return new FoodConfigPredicate(predicate, Pair.of(nutrition, saturation), location + ":" + nutrition + ":" + saturation);
    }

    public static FoodConfigPredicate of(final String data) {
        String[] splitData = data.split(":");
        Predicate<Item> predicate = ConfigUtils.itemPredicate(splitData);

        if (splitData.length == 4) {
            int nutrition = Integer.parseInt(splitData[NUTRITION]);
            float saturation = Float.parseFloat(splitData[SATURATION]);
            return new FoodConfigPredicate(predicate, Pair.of(nutrition, saturation), data);
        }

        return new FoodConfigPredicate(predicate, null, data);
    }

    public static boolean validate(final String data) {
        String[] splitData = data.split(":");

        if (!ConfigUtils.validateResourceLocation(splitData)) {
            return false;
        }

        if (splitData.length == 4) {
            return ConfigUtils.validateInteger(splitData[NUTRITION]) && ConfigUtils.validateFloat(splitData[SATURATION]);
        }

        return splitData.length == 2;
    }
}
