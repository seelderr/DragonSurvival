package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

public @interface AbilityInfo {
    Type[] compatibleWith() default {};

    enum Type {
        PASSIVE,
        ACTIVE_SIMPLE,
        ACTIVE_CHANNELED
    }
}
