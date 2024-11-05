package by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang;

import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;

public class LangKey {
    /** This is not assigned to one element but rather used at two places which dynamically create the translation key / translation */
    public static final String CATEGORY_PREFIX = Translation.Type.CONFIGURATION.prefix + "category.";

    // --- GUI --- //

    @Translation(type = Translation.Type.MISC, comments = "Cancel")
    public static final String GUI_CANCEL = Translation.Type.GUI.wrap("cancel");

    @Translation(type = Translation.Type.MISC, comments = "Confirm")
    public static final String GUI_CONFIRM = Translation.Type.GUI.wrap("confirm");

    @Translation(type = Translation.Type.MISC, comments = "Glowing")
    public static final String GUI_GLOWING = Translation.Type.GUI.wrap("glowing");
}
