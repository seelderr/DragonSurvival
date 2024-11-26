package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PenaltySupply implements INBTSerializable<CompoundTag> {
    private final HashMap<String, Data> supplyData = new HashMap<>();

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        supplyData.forEach((key, value) -> tag.put(key, value.serializeNBT()));
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        supplyData.clear();
        tag.getAllKeys().forEach(key -> supplyData.put(key, Data.deserializeNBT(tag.getCompound(key))));
    }

    public boolean hasSupply(final String supplyType) {
        Data data = supplyData.get(supplyType);

        if (data == null) {
            return false;
        }

        return data.getSupply() > 0;
    }

    public float getPercentage(final String supplyType) {
        Data data = supplyData.get(supplyType);

        if (data == null) {
            return 0;
        }

        return (data.getSupply() / data.getMaximumSupply()) * 100;
    }

    public void reduce(final String supplyType) {
        Data data = supplyData.get(supplyType);

        if (data == null) {
            return;
        }

        data.reduce();
    }

    public void regenerate(final String supplyType) {
        Data data = supplyData.get(supplyType);

        if (data == null) {
            return;
        }

        data.regenerate();
    }

    public void initialize(final String supplyType, float maximumSupply, float reductionRate, float regenerationRate) {
        if (!supplyData.containsKey(supplyType)) {
            // TODO :: start at 0 or maximum?
            supplyData.put(supplyType, new Data(maximumSupply, maximumSupply, reductionRate, regenerationRate));
        }
    }

    public enum RateType {REDUCTION, REGENERATION}

    private static class Data {
        private static final String MAXIMUM_SUPPLY = "maximum_supply";
        private static final String CURRENT_SUPPLY = "current_supply";
        private static final String REDUCTION_RATE = "reduction_rate";
        private static final String REGENERATION_RATE = "regeneration_rate";

        private final float maximumSupply;
        private float currentSupply;

        private final Rate reductionRate;
        private final Rate regenerationRate;

        public Data(float maximumSupply, float currentSupply, float reductionRate, float regenerationRate) {
            this.maximumSupply = maximumSupply;
            this.currentSupply = currentSupply;
            this.reductionRate = new Rate(reductionRate);
            this.regenerationRate = new Rate(regenerationRate);
        }

        public Data(float maximumSupply, float currentSupply, final Rate reductionRate, final Rate regenerationRate) {
            this.maximumSupply = maximumSupply;
            this.currentSupply = currentSupply;
            this.reductionRate = reductionRate;
            this.regenerationRate = regenerationRate;
        }

        public float getSupply() {
            return currentSupply;
        }

        public float getMaximumSupply() {
            return maximumSupply;
        }

        public void reduce() {
            currentSupply = Math.max(0, currentSupply - reductionRate.value());
        }

        public void regenerate() {
            currentSupply = Math.min(maximumSupply, currentSupply + regenerationRate.value());
        }

        public void addModifier(final RateType rateType, final AttributeModifier.Operation operation, float amount) {
            switch (rateType) {
                case REDUCTION -> addModifier(reductionRate, operation, amount);
                case REGENERATION -> addModifier(regenerationRate, operation, amount);
            }
        }

        private void addModifier(final Rate rate, final AttributeModifier.Operation operation, float amount) {
            switch (operation) {
                case ADD_VALUE -> rate.add(amount);
                case ADD_MULTIPLIED_BASE -> rate.multiplyBase(amount);
                case ADD_MULTIPLIED_TOTAL -> rate.multiplyTotal(amount);
            }
        }

        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat(MAXIMUM_SUPPLY, maximumSupply);
            tag.putFloat(CURRENT_SUPPLY, currentSupply);
            tag.put(REDUCTION_RATE, reductionRate.serializeNBT());
            tag.put(REGENERATION_RATE, regenerationRate.serializeNBT());

            return tag;
        }

        public static Data deserializeNBT(@NotNull final CompoundTag tag) {
            float maximumSupply = tag.getFloat(MAXIMUM_SUPPLY);
            float currentSupply = tag.getFloat(CURRENT_SUPPLY);
            Rate reductionRate = Rate.deserializeNBT(tag.getCompound(REDUCTION_RATE));
            Rate regenerationRate = Rate.deserializeNBT(tag.getCompound(REGENERATION_RATE));

            return new Data(maximumSupply, currentSupply, reductionRate, regenerationRate);
        }
    }

    private static class Rate {
        private static final String BASE_RATE = "base_rate";
        private static final String ADD_MODIFIER = "add_modifier";
        private static final String MULTIPLY_BASE_MODIFIER = "multiply_base_modifier";
        private static final String MULTIPLY_TOTAL_MODIFIER = "multiply_total_modifier";

        private final float baseRate;

        // TODO :: fix this otherwise abilities might add modifiers multiple times or sth.
        private float addModifier;
        private float multiplyBaseModifier;
        private float multiplyTotalModifier;

        public Rate(final float baseRate) {
            this.baseRate = baseRate;
        }

        public float value() {
            return ((baseRate + addModifier) * multiplyBaseModifier) * (1 + multiplyTotalModifier);
        }

        public void add(float amount) {
            addModifier += amount;
        }

        public void multiplyBase(float amount) {
            multiplyBaseModifier += amount;
        }

        public void multiplyTotal(float amount) {
            multiplyTotalModifier += amount;
        }

        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat(BASE_RATE, baseRate);
            tag.putFloat(ADD_MODIFIER, addModifier);
            tag.putFloat(MULTIPLY_BASE_MODIFIER, multiplyBaseModifier);
            tag.putFloat(MULTIPLY_TOTAL_MODIFIER, multiplyTotalModifier);

            return tag;
        }

        public static Rate deserializeNBT(@NotNull final CompoundTag tag) {
            Rate rate = new Rate(tag.getFloat(BASE_RATE));
            rate.add(tag.getFloat(ADD_MODIFIER));
            rate.add(tag.getFloat(MULTIPLY_BASE_MODIFIER));
            rate.add(tag.getFloat(MULTIPLY_TOTAL_MODIFIER));

            return rate;
        }
    }
}
