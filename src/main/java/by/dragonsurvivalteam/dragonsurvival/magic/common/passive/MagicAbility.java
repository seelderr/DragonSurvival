package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;


import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class MagicAbility extends PassiveDragonAbility {
    @Override
    public Component getDescription() {
        if (player == null) {
            return Component.empty();
        }

        int experienceMana = 0;

        if (ServerConfig.consumeExperienceAsMana) {
            experienceMana = Math.min(99, ManaHandler.getManaFromExperience(player));
        }

        String manaFromExperience = experienceMana > 0 ? "+" + experienceMana : "0";

        int abilityLevel = Math.min(99, getLevel());
        String manaFromAbility = abilityLevel > 0 ? "+" + abilityLevel : "0";

        AbstractDragonBody body = DragonUtils.getDragonBody(player);
        String manaFromBody = "0";

        if (body != null) {
            int bodyMana = (int) Math.min(99, body.getManaBonus() * 1);
            manaFromBody = bodyMana > 0 ? "+" + bodyMana : "" + bodyMana;
        }

        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), Math.min(99, ManaHandler.getMaxMana(player)), manaFromAbility, manaFromBody, manaFromExperience);
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    public int getMana() {
        return getLevel();
    }

    @Override
    public ResourceLocation getIcon() {
        return getPlayer() == null ? super.getIcon() : getSkillTextures()[Mth.clamp(getLevel() + Math.max(0, (Math.min(50, getPlayer().experienceLevel) - 5) / 5), 0, getSkillTextures().length - 1)];
    }
}