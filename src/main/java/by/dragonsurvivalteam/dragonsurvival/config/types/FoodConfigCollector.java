package by.dragonsurvivalteam.dragonsurvival.config.types;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class FoodConfigCollector implements CustomConfig {
    private static final int NUTRITION = 2;
    private static final int SATURATION = 3;

    private final Supplier<HolderSet<Item>> collector;
    private final @Nullable Pair<Integer, Float> foodData;
    private final String originalData;

    private FoodConfigCollector(final Supplier<HolderSet<Item>> collector, @Nullable final Pair<Integer, Float> foodData, final String originalData) {
        this.collector = collector;
        this.foodData = foodData;
        this.originalData = originalData;
    }

    @Override
    public String convert() {
        return originalData;
    }

    public Map<Item, FoodProperties> collectFoodData() {
        Map<Item, FoodProperties> foodMap = new ConcurrentHashMap<>();

        for (Holder<Item> holder : collector.get()) {
            Item item = holder.value();

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
            foodMap.put(item, builder.build());
        }

        return foodMap;
    }

    public static FoodConfigCollector of(final String location, int nutrition, float saturation) {
        Supplier<HolderSet<Item>> collector = ConfigUtils.itemSupplier(location.split(":"));
        return new FoodConfigCollector(collector, Pair.of(nutrition, saturation), location + ":" + nutrition + ":" + saturation);
    }

    public static FoodConfigCollector of(final String data) {
        String[] splitData = data.split(":");
        Supplier<HolderSet<Item>> collector = ConfigUtils.itemSupplier(splitData);

        if (splitData.length == 4) {
            int nutrition = Integer.parseInt(splitData[NUTRITION]);
            float saturation = Float.parseFloat(splitData[SATURATION]);
            return new FoodConfigCollector(collector, Pair.of(nutrition, saturation), data);
        }

        return new FoodConfigCollector(collector, null, data);
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
