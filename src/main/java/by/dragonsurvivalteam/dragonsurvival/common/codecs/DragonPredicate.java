package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarHeartItem;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record DragonPredicate(
        Optional<String> dragonType,
        Optional<DragonStagePredicate> dragonStage,
        Optional<HolderSet<DragonBody>> dragonBody,
        Optional<MinMaxBounds.Doubles> size,
        Optional<StarHeartItem.State> starHeartState
) implements EntitySubPredicate {
    public static final MapCodec<DragonPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("dragon_type").forGetter(DragonPredicate::dragonType),
            DragonStagePredicate.CODEC.optionalFieldOf("dragon_stage").forGetter(DragonPredicate::dragonStage),
            RegistryCodecs.homogeneousList(DragonBody.REGISTRY).optionalFieldOf("dragon_body").forGetter(DragonPredicate::dragonBody),
            MinMaxBounds.Doubles.CODEC.optionalFieldOf("size").forGetter(DragonPredicate::size),
            StarHeartItem.State.CODEC.optionalFieldOf("star_heart_state").forGetter(DragonPredicate::starHeartState)
    ).apply(instance, DragonPredicate::new));

    @Override
    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    public boolean matches(@NotNull final Entity entity, @NotNull final ServerLevel level, @Nullable final Vec3 position) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.isDragon()) {
            return false;
        }

        if (dragonType().isPresent() && !dragonType().get().equals(data.getTypeNameLowerCase())) {
            return false;
        }

        if (dragonStage().isPresent() && !dragonStage().get().matches(data)) {
            return false;
        }

        if (dragonBody().isPresent() && !dragonBody().get().contains(data.getBody())) {
            return false;
        }

        if (size().isPresent() && !size().get().matches(data.getSize())) {
            return false;
        }

        if (starHeartState().isPresent() && starHeartState().get() != data.starHeartState) {
            return false;
        }

        return true;
    }

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // ignore
    public static class Builder {
        private Optional<String> dragonType = Optional.empty();
        private Optional<DragonStagePredicate> dragonStage = Optional.empty();
        private Optional<HolderSet<DragonBody>> dragonBody = Optional.empty();
        private Optional<MinMaxBounds.Doubles> size = Optional.empty();
        private Optional<StarHeartItem.State> starHeartState = Optional.empty();

        public static DragonPredicate.Builder dragon() {
            return new DragonPredicate.Builder();
        }

        public DragonPredicate.Builder type(final AbstractDragonType dragonType) {
            this.dragonType = Optional.of(dragonType.getTypeNameLowerCase());
            return this;
        }

        public DragonPredicate.Builder stage(final Holder<DragonStage> dragonStage) {
            this.dragonStage = Optional.of(new DragonStagePredicate(Optional.of(dragonStage), Optional.empty()));
            return this;
        }

        public DragonPredicate.Builder stage(final Holder<DragonStage> dragonStage, final MinMaxBounds.Doubles growthPercentage) {
            this.dragonStage = Optional.of(new DragonStagePredicate(Optional.of(dragonStage), Optional.of(growthPercentage)));
            return this;
        }

        public DragonPredicate.Builder body(final Holder<DragonBody> dragonBody) {
            this.dragonBody = Optional.of(HolderSet.direct(dragonBody));
            return this;
        }

        public DragonPredicate.Builder sizeBetween(double min, double max) {
            this.size = Optional.of(MinMaxBounds.Doubles.between(min, max));
            return this;
        }

        public DragonPredicate.Builder sizeAtLeast(double min) {
            this.size = Optional.of(MinMaxBounds.Doubles.atLeast(min));
            return this;
        }

        public DragonPredicate.Builder sizeAtMost(double max) {
            this.size = Optional.of(MinMaxBounds.Doubles.atLeast(max));
            return this;
        }

        public DragonPredicate.Builder starHeart(final StarHeartItem.State starHeartState) {
            this.starHeartState = Optional.of(starHeartState);
            return this;
        }

        public DragonPredicate build() {
            return new DragonPredicate(dragonType, dragonStage, dragonBody, size, starHeartState);
        }
    }
}
