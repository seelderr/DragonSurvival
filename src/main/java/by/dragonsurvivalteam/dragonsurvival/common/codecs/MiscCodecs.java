package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Arrays;

public class MiscCodecs {
    public static Codec<Double> doubleRange(double min, double max) {
        return Codec.DOUBLE.validate(value -> value >= min && value <= max
                ? DataResult.success(value)
                : DataResult.error(() -> "Value must be within range [" + min + ";" + max + "]: " + value)
        );
    }

    public record Bounds(double min, double max) {
        public static Codec<Bounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("min").forGetter(Bounds::min),
                Codec.DOUBLE.fieldOf("max").forGetter(Bounds::max)
        ).apply(instance, instance.stable(Bounds::new)));

        public boolean matches(double value) {
            return min <= value && value <= max;
        }
    }

    public static Codec<Bounds> bounds() {
        return Bounds.CODEC.validate(value -> {
            if (value.min() >= 1 && value.max() > value.min()) {
                return DataResult.success(value);
            } else {
                return DataResult.error(() -> "Min must be at least 1 and max must be larger than min " + value);
            }
        });
    }

    public record GrowthItem(HolderSet<Item> items, int growthInTicks) {
        public static Codec<GrowthItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(GrowthItem::items),
                Codec.INT.fieldOf("growth_in_ticks").forGetter(GrowthItem::growthInTicks)
        ).apply(instance, instance.stable(GrowthItem::new)));

        public static GrowthItem create(int growthInTicks, final TagKey<Item> tag) {
            return new GrowthItem(BuiltInRegistries.ITEM.getOrCreateTag(tag), growthInTicks);
        }

        public static GrowthItem create(int growthInTicks, final Item... items) {
            return new GrowthItem(HolderSet.direct(Arrays.stream(items).map(BuiltInRegistries.ITEM::wrapAsHolder).toList()), growthInTicks);
        }
    }
}
