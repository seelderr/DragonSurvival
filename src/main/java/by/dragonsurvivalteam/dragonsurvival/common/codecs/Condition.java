package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarHeartItem;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;

public class Condition {
    public static ContextAwarePredicate none() {
        return EntityPredicate.wrap(EntityPredicate.Builder.entity().build());
    }

    public static EntityPredicate defaultNaturalGrowthBlocker() {
        return EntityPredicate.Builder.entity().subPredicate(
                DragonPredicate.Builder.dragon().starHeart(StarHeartItem.State.ACTIVE).build()
        ).build();
    }

    public static EntityPredicate build(final DragonPredicate.Builder builder) {
        return EntityPredicate.Builder.entity().subPredicate(builder.build()).build();
    }

    // --- Builder --- //

    public static EntityPredicate.Builder dragonType(final AbstractDragonType type) {
        return EntityPredicate.Builder.entity().subPredicate(DragonPredicate.Builder.dragon().type(type).build());
    }

    public static EntityPredicate.Builder dragonStage(final Holder<DragonStage> dragonStage) {
        return EntityPredicate.Builder.entity().subPredicate(DragonPredicate.Builder.dragon().stage(dragonStage).build());
    }

    public static EntityPredicate.Builder dragonBody(final Holder<DragonBody> dragonBody) {
        return EntityPredicate.Builder.entity().subPredicate(DragonPredicate.Builder.dragon().body(dragonBody).build());
    }

    public static EntityPredicate.Builder dragonSizeBetween(double min, double max) {
        return EntityPredicate.Builder.entity().subPredicate(DragonPredicate.Builder.dragon().sizeBetween(min, max).build());
    }

    public static EntityPredicate.Builder dragonSizeAtLeast(double min) {
        return EntityPredicate.Builder.entity().subPredicate(DragonPredicate.Builder.dragon().sizeAtLeast(min).build());
    }

    public static EntityPredicate.Builder dragonSizeAtMost(double max) {
        return EntityPredicate.Builder.entity().subPredicate(DragonPredicate.Builder.dragon().sizeAtLeast(max).build());
    }
}
