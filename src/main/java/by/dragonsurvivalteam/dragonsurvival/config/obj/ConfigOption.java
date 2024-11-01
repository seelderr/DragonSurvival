package by.dragonsurvivalteam.dragonsurvival.config.obj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigOption {
    ConfigSide side();

    /**
     * Identifier for this conf - needs to be unique
     */
    String key();

    /**
     * Determines how validation will be handled
     */
    Validation validation() default Validation.DEFAULT;

    /**
     * The category the option will be found in - the array defines a path (e.g. {"a", "b", "c"} results in the path a.b.c.key)
     */
    String[] category() default {};

    /**
     * Comment for the config file <br>
     * The config screen uses translation keys instead (see {@link ConfigOption#localization()})
     */
    String[] comment();

    /**
     * Custom translation key for the config name <br> <br>
     * The default translation key is 'mod_id.configuration.key_of_this_config' <br>
     * (An example would be 'dragonsurvival.configuration.requireDragonFood') <br> <br>
     * Certain elements will have additional suffixes, like '.button' or '.title'
     */
    String localization() default "";
}