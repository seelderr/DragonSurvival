package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarHeartItem;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;

public class Condition {
    public static ContextAwarePredicate none() {
        return EntityPredicate.wrap(EntityPredicate.Builder.entity().build());
    }

    public static EntityPredicate naturalGrowth() {
        return EntityPredicate.Builder.entity().subPredicate(
                DragonPredicate.Builder.dragon().starHeart(StarHeartItem.State.INACTIVE).build()
        ).build();
    }

    public static EntityPredicate build(final DragonPredicate.Builder builder) {
        return EntityPredicate.Builder.entity().subPredicate(builder.build()).build();
    }

    // --- Builder --- //

    public static EntityPredicate.Builder dragonType(final AbstractDragonType type) {
        return EntityPredicate.Builder.entity().subPredicate(DragonPredicate.Builder.dragon().type(type).build());
    }

    public static EntityPredicate.Builder dragonSize(double min, double max) {
        return EntityPredicate.Builder.entity().subPredicate(DragonPredicate.Builder.dragon().sizeRange(min, max).build());
    }
}
