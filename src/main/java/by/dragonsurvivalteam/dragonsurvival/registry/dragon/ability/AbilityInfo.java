package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AbilityInfo {
    Type[] compatibleWith() default {};

    enum Type {
        PASSIVE,
        ACTIVE_SIMPLE,
        ACTIVE_CHANNELED
    }
}
