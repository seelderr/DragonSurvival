package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Translation.Translations.class)
public @interface Translation {
    /** If it's empty the key will be derived from the field (behaviour depends on the field type) */
    String key() default "";

    Type type();

    String locale() default "en_us";

    /**
     * Translation for the key <br>
     * Comment entries will be separated by a newline (\n)
     */
    String[] comments();

    // To allow multiple translations (potentially of differing types) to be set on one field
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Translations {
        Translation[] value();
    }

    enum Type {
        ITEM("item." + DragonSurvival.MODID + ".", ""),
        BLOCK("block." + DragonSurvival.MODID + ".", ""),
        ENTITY("entity." + DragonSurvival.MODID + ".", ""),
        EFFECT("effect." + DragonSurvival.MODID + ".", ""),
        EFFECT_DESCRIPTION("effect." + DragonSurvival.MODID + ".", ".desc"),
        ENCHANTMENT("enchantment." + DragonSurvival.MODID + ".", ""),
        ENCHANTMENT_DESCRIPTION("enchantment." + DragonSurvival.MODID + ".", ".desc"),
        DESCRIPTION(DragonSurvival.MODID + ".description.", ""),
        DESCRIPTION_ADDITION(DragonSurvival.MODID + ".description.addition.", ""),
        CONFIGURATION(DragonSurvival.MODID + ".configuration.", ".tooltip"),
        ABILITY(DragonSurvival.MODID + ".ability.", ""),
        ADVANCEMENT(DragonSurvival.MODID + ".advancement.", ""),
        SKIN_PART(DragonSurvival.MODID + ".skin_part.", ""),
        GUI(DragonSurvival.MODID + ".gui.", ""),
        KEYBIND(DragonSurvival.MODID + ".keybind.", ""),
        EMOTE(DragonSurvival.MODID + ".emote.", ""),
        /** When used on {@link String} and no specified key it's expected that the string contains the translation key */
        MISC("", "");

        public final String prefix;
        public final String suffix;

        Type(final String prefix, final String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String wrap(final String key) {
            return prefix + key + suffix;
        }
    }
}
