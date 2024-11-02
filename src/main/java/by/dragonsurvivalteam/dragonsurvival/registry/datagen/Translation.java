package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Translation.Translations.class)
public @interface Translation {
    // Prefix determined by vanilla
    String ITEM_PREFIX = "item." + DragonSurvival.MODID + ".";
    String BLOCK_PREFIX = "block." + DragonSurvival.MODID + ".";
    String ENTITY_PREFIX = "entity." + DragonSurvival.MODID + ".";
    String EFFECT_PREFIX = "effect." + DragonSurvival.MODID + ".";

    // Custom prefix
    String DESCRIPTION_PREFIX = DragonSurvival.MODID + ".description";
    String DESCRIPTION_ADDITION_PREFIX = DragonSurvival.MODID + ".description.addition";
    String CONFIGURATION_PREFIX = DragonSurvival.MODID + ".configuration.";
    String ABILITY_PREFIX = DragonSurvival.MODID + ".ability.";
    String ADVANCEMENT_PREFIX = DragonSurvival.MODID + ".advancement.";
    String SKIN_PART_PREFIX = DragonSurvival.MODID + ".skin_part.";
    String GUI_PREFIX = DragonSurvival.MODID + ".gui.";
    String KEYBIND_PREFIX = DragonSurvival.MODID + ".keybind";
    String EMOTE_PREFIX = DragonSurvival.MODID + ".emote.";

    // Suffix
    String CONFIGURATION_SUFFIX = ".tooltip";

    String key();

    Type type();

    String locale() default "en_us";

    String[] comments();

    // To allow multiple translations (potentially of differing types) to be set on one field
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Translations {
        Translation[] value();
    }

    enum Type {
        ITEM(ITEM_PREFIX, ""),
        BLOCK(BLOCK_PREFIX, ""),
        ENTITY(ENTITY_PREFIX, ""),
        EFFECT(EFFECT_PREFIX, ""),
        DESCRIPTION(DESCRIPTION_PREFIX, ""),
        DESCRIPTION_ADDITION(DESCRIPTION_ADDITION_PREFIX, ""),
        CONFIGURATION(CONFIGURATION_PREFIX, CONFIGURATION_SUFFIX),
        ABILITY(ABILITY_PREFIX, ""),
        ADVANCEMENT(ADVANCEMENT_PREFIX, ""),
        SKIN_PART(SKIN_PART_PREFIX, ""),
        GUI(GUI_PREFIX, ""),
        KEYBIND(KEYBIND_PREFIX, ""),
        EMOTE(EMOTE_PREFIX, ""),
        MISC("", "");

        public final String prefix;
        public final String suffix;

        Type(final String prefix, final String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }
}
