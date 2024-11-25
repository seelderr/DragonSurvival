package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record DragonStagePredicate(Optional<Holder<DragonStage>> dragonStage, Optional<MinMaxBounds.Doubles> growthPercentage) {
    public static final Codec<DragonStagePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DragonStage.CODEC.optionalFieldOf("dragon_stage").forGetter(DragonStagePredicate::dragonStage),
            MiscCodecs.percentageBounds().optionalFieldOf("growth_percentage").forGetter(DragonStagePredicate::growthPercentage)
    ).apply(instance, DragonStagePredicate::new));

    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    public boolean matches(@NotNull final DragonStateHandler data) {
        if (!data.isDragon()) {
            return false;
        }

        if (dragonStage().isPresent() && !dragonStage().get().is(data.getStage())) {
            return false;
        }

        if (growthPercentage().isPresent() && !growthPercentage().get().matches(data.getStage().value().getProgress(data.getSize()))) {
            return false;
        }

        return true;
    }
}