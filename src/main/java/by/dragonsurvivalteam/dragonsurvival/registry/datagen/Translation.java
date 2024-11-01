package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Translation.Translations.class)
public @interface Translation {
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
        ITEM("item." + DragonSurvival.MODID + ".", ""),
        BLOCK("block." + DragonSurvival.MODID + ".", ""),
        ENTITY("entity." + DragonSurvival.MODID + ".", ""),
        EFFECT("effect." + DragonSurvival.MODID + ".", ""),
        DESCRIPTION(DragonSurvival.MODID + ".description", ""),
        DESCRIPTION_ADDITION(DragonSurvival.MODID + ".description.add.", ""),
        CONFIGURATION(DragonSurvival.MODID + ".configuration.", ".tooltip"),
        ABILITY(DragonSurvival.MODID + ".ability", ""),
        ADVANCEMENT(DragonSurvival.MODID + ".advancement.", ""),
        SKIN_PART(DragonSurvival.MODID + ".skin_part.", ""),
        GUI(DragonSurvival.MODID + ".gui.", ""),
        KEYBIND(DragonSurvival.MODID + ".keybind", ""),
        EMOTE(DragonSurvival.MODID + ".emote.", ""),
        MISC(DragonSurvival.MODID + ".", "");

        public final String prefix;
        public final String suffix;

        Type(final String prefix, final String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }
}
