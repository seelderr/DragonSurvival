package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Arrays;

public class MiscCodecs {
    public static Codec<HolderSet<DragonAbility>> dragonAbilityCodec() {
        int maximum = DragonAbility.MAX_ACTIVE + DragonAbility.MAX_PASSIVE;

        return RegistryCodecs.homogeneousList(DragonAbility.REGISTRY).validate(abilities -> {
            if (abilities.size() > maximum) {
                return DataResult.error(() -> "Defined [" + abilities.size() + "] abilities - only up to [" + maximum + "] are allowed");
            }

            int currentActive = 0;
            int currentPassive = 0;

            for (Holder<DragonAbility> ability : abilities) {
                if (ability.value().activation().right().isPresent()) {
                    currentActive++;
                } else {
                    currentPassive++;
                }
            }

            if (currentActive > DragonAbility.MAX_ACTIVE) {
                int finalCurrentActive = currentActive;
                return DataResult.error(() -> "Defined [" + finalCurrentActive + "] active abilities - only up to [" + DragonAbility.MAX_ACTIVE + "] are allowed");
            }

            if (currentPassive > DragonAbility.MAX_PASSIVE) {
                int finalCurrentPassive = currentPassive;
                return DataResult.error(() -> "Defined [" + finalCurrentPassive + "] passive abilities - only up to [" + DragonAbility.MAX_PASSIVE + "] are allowed");
            }

            return DataResult.success(abilities);
        });
    }

    public static Codec<MinMaxBounds.Doubles> percentageBounds() {
        return MinMaxBounds.Doubles.CODEC.validate(value -> {
            boolean isValid = true;

            if (value.min().isPresent()) {
                double min = value.min().get();

                if (min < 0 || min > 1) {
                    isValid = false;
                }
            }

            if (value.max().isPresent()) {
                double max = value.max().get();

                if (max < 0 || max > 1) {
                    isValid = false;
                }
            }

           return isValid ? DataResult.success(value) : DataResult.error(() -> "Percentage check must be between 0 and 1: [" + value + "]");
        });
    }

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

    public record DestructionData(double crushingSize, double blockDestructionSize, double crushingDamageScalar) {
        public static Codec<DestructionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("crushing_size").forGetter(DestructionData::crushingSize),
                Codec.DOUBLE.fieldOf("block_destruction_size").forGetter(DestructionData::blockDestructionSize),
                Codec.DOUBLE.fieldOf("crushing_damage_scalar").forGetter(DestructionData::crushingDamageScalar)
        ).apply(instance, instance.stable(DestructionData::new)));

        public boolean isCrushingAllowed(double dragonSize) {
            return dragonSize >= crushingSize;
        }

        public boolean isBlockDestructionAllowed(double dragonSize) {
            return dragonSize >= blockDestructionSize;
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted") // ignore
        public boolean isDestructionAllowed(double dragonSize) {
            return isCrushingAllowed(dragonSize) || isBlockDestructionAllowed(dragonSize);
        }
    }
}
