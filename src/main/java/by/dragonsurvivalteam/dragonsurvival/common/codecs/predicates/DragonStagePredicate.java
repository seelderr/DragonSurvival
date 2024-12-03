package by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;

import java.util.Optional;

public record DragonStagePredicate(Optional<HolderSet<DragonStage>> dragonStage, Optional<MinMaxBounds.Doubles> growthPercentage, Optional<MinMaxBounds.Doubles> size) {
    public static final Codec<DragonStagePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(DragonStage.REGISTRY).optionalFieldOf("dragon_stage").forGetter(DragonStagePredicate::dragonStage),
            MiscCodecs.percentageBounds().optionalFieldOf("growth_percentage").forGetter(DragonStagePredicate::growthPercentage),
            MinMaxBounds.Doubles.CODEC.optionalFieldOf("size").forGetter(DragonStagePredicate::size)
    ).apply(instance, DragonStagePredicate::new));

    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    public boolean matches(final Holder<DragonStage> stage, double size) {
        if (stage == null) {
            return false;
        }

        if (dragonStage().isPresent() && !dragonStage().get().contains(stage)) {
            return false;
        }

        if (growthPercentage().isPresent() && !growthPercentage().get().matches(stage.value().getProgress(size))) {
            return false;
        }

        if (size().isPresent() && !size().get().matches(size)) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // ignore
    public static class Builder {
        private Optional<HolderSet<DragonStage>> dragonStage = Optional.empty();
        private Optional<MinMaxBounds.Doubles> size = Optional.empty();
        private Optional<MinMaxBounds.Doubles> growthPercentage = Optional.empty();

        public static DragonStagePredicate.Builder start() {
            return new DragonStagePredicate.Builder();
        }

        public DragonStagePredicate.Builder stage(final Holder<DragonStage> dragonStage) {
            this.dragonStage = Optional.of(HolderSet.direct(dragonStage));
            return this;
        }

        public DragonStagePredicate.Builder growth(final MinMaxBounds.Doubles growthPercentage) {
            this.growthPercentage = Optional.of(growthPercentage);
            return this;
        }

        public DragonStagePredicate.Builder growthAtLeast(double percentage) {
            this.growthPercentage = Optional.of((MinMaxBounds.Doubles.atLeast(percentage)));
            return this;
        }

        public DragonStagePredicate.Builder size(final MinMaxBounds.Doubles size) {
            this.size = Optional.of(size);
            return this;
        }

        public DragonStagePredicate.Builder sizeBetween(double min, double max) {
            this.size = Optional.of(MinMaxBounds.Doubles.between(min, max));
            return this;
        }

        public DragonStagePredicate.Builder sizeAtLeast(double min) {
            this.size = Optional.of(MinMaxBounds.Doubles.atLeast(min));
            return this;
        }

        public DragonStagePredicate.Builder sizeAtMost(double max) {
            this.size = Optional.of(MinMaxBounds.Doubles.atLeast(max));
            return this;
        }

        public DragonStagePredicate build() {
            return new DragonStagePredicate(dragonStage, growthPercentage, size);
        }
    }
}