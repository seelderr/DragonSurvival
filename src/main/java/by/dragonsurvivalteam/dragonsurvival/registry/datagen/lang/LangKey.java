package by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang;

import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;

/** Translation keys which are used in multiple places */
public class LangKey {
    /** This is not assigned to one element but rather used at two places which dynamically create the translation key / translation */
    public static final String CATEGORY_PREFIX = Translation.Type.CONFIGURATION.prefix + "category.";

    // --- GUI --- //

    @Translation(type = Translation.Type.MISC, comments = "Cancel")
    public static final String GUI_CANCEL = Translation.Type.GUI.wrap("general.cancel");

    @Translation(type = Translation.Type.MISC, comments = "Confirm")
    public static final String GUI_CONFIRM = Translation.Type.GUI.wrap("general.confirm");

    @Translation(type = Translation.Type.MISC, comments = "Glowing")
    public static final String GUI_GLOWING = Translation.Type.GUI.wrap("general.glowing");

    @Translation(type = Translation.Type.MISC, comments = "DRAGON EDITOR")
    public static final String GUI_DRAGON_EDITOR = Translation.Type.GUI.wrap("general.dragon_editor");

    // --- Abilities --- //

    @Translation(type = Translation.Type.MISC, comments = "§6■ Damage:§r %s")
    public static final String ABILITY_DAMAGE = Translation.Type.ABILITY.wrap("general.damage");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Range:§r %s blocks")
    public static final String ABILITY_RANGE = Translation.Type.ABILITY.wrap("general.range_blocks");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Radius:§r %s")
    public static final String ABILITY_AOE = Translation.Type.ABILITY.wrap("general.aoe");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Cooldown:§r %ss")
    public static final String ABILITY_COOLDOWN = Translation.Type.ABILITY.wrap("general.cooldown");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Chance: §c%s%%§r")
    public static final String ABILITY_CHANCE = Translation.Type.ABILITY.wrap("general.chance");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Mana cost:§r %s")
    public static final String ABILITY_MANA_COST = Translation.Type.ABILITY.wrap("general.mana_cost");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Cast time:§r %ss")
    public static final String ABILITY_CAST_TIME = Translation.Type.ABILITY.wrap("general.cast_time");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Duration:§r %ss")
    public static final String ABILITY_DURATION = Translation.Type.ABILITY.wrap("general.duration");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Currently bound to:§r [%s]")
    public static final String ABILITY_KEYBIND = Translation.Type.ABILITY.wrap("general.keybind");

    //  "ds.skill.help_1": "■ §6Active skills§r are used in combat.\n- §9Skill power§r scales off your current experience level. The higher your EXP level, the stronger your active skills.\n- §9Experience or mana§r points are used to cast spells.\n- §9Controls§r - check in-game Minecraft control settings! You can drag and drop skill icons around.",
    //  "ds.skill.help_1_alt": "■ §6Active skills§r are used in combat.\n- §9Experience or mana§r points are used to cast spells.\n- §9Controls§r - check in the game settings! You can drag and drop skill icons around.",
    //  "ds.skill.help_2": "■ §aPassive skills§r are upgraded by spending experience levels.\n- §9Mana§r - do not forget use the Source of Magic and Dragons Treats for an infinite supply of mana!\n- §9More information§r can be found on our Wiki and in our Discord. Check the Curseforge mod page.",
    //  "ds.skill.help_2_alt": "■ §aPassive skills§r can be upgraded permanently, unlike Active skills.\n- §9Mana§r - do not forget to use the Source of Magic and Dragons Treats for an infinite supply of mana!\n- §9More information§r can be found on our Wiki and in our Discord. Check the Curseforge mod page.",
    //  "ds.skill.help_3": "■ §dInnate skills§r are a dragon's quirks, and represent the benefits and drawbacks of each dragon type.",
    //  "ds.skill.help.claws": "■ A dragon is §6born§r with strong claws and teeth, but you can make them even better! Just put §6any tools§r§f here in your claw slots and your bare paw will borrow their aspect as long as they are intact.\n§7■ Does not stack with «Claws and Teeth» skill, which only applies if these slots are empty.",
    //  "ds.help.source_of_magic": "■ A source of magic can be charged with Elder Dragon items. \n■ Crouch + right click to use.\n\n■ §6+10 sec§r - Elder Dragon Dust\n■ §6+50 sec§r - Elder Dragon Bone\n■ §6+100 sec§r - Dragon Heart Shard\n■ §6+300 sec§r - Weak Dragon Heart\n■ §6+1000 sec§r - Elder Dragon Heart",
    //  "ds.skill_cooldown_check_failure": "§fThis ability is §r§cnot ready§r§f yet!§r (%s)",
    //  "ds.skill_mana_check_failure": "§fNot enough§r §cmana or experience§r!",
    //  "ds.skill.nofly": "§fThis skill cannot be used §r§cwhile flying§r§f!§f",
    //  "ds.skill.required_level": "§6■ Requires level:§r %s",
    //  "ds.skill.level.up": "§aUpgrade the skill for§r %s §alevels§r",
    //  "ds.skill.level.down": "§aDowngrade the skill",
    //  "ds.skill.claws.damage": "§d■ Claw damage bonus:§r %s",
    //  "ds.skill.harvest_level": "§d■ Harvest level:§r %s",
    //  "ds.skill.harvest_level.wood": "Wood",
    //  "ds.skill.harvest_level.stone": "Stone",
    //  "ds.skill.harvest_level.iron": "Iron",
    //  "ds.skill.harvest_level.diamond": "Diamond",
    //  "ds.skill.harvest_level.netherite": "Netherite",
}
