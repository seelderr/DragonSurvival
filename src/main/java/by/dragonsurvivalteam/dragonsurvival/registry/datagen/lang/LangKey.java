package by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang;

import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;

/**
 * A {@link by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation} annotation cannot use the variable it's annotated on <br>
 * To make sure these strings don't go out of sync we define them here and reference them in the variable and translation annotation
 */
public class LangKey {
    public static final String GUI_CONFIRM_LOSE_ALL = Translation.GUI_PREFIX + "dragon_editor.confirm.all";
    public static final String GUI_CONFIRM_LOSE_GROWTH =Translation.GUI_PREFIX + "dragon_editor.confirm.growth";
    public static final String GUI_CONFIRM_LOSE_ABILITIES = Translation.GUI_PREFIX+ "dragon_editor.confirm.abilities";
}
