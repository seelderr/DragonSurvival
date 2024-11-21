package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Objects;

public abstract class MagicAbility extends PassiveDragonAbility {
    @Override
    public Component getDescription() {
        if (player == null) {
            return Component.empty();
        }

        String manaFromExperience = "0";

        if (ServerConfig.consumeExperienceAsMana) {
            int experienceMana = Math.min(99, ManaHandler.getManaFromExperience(player));

            if (experienceMana > 0) {
                manaFromExperience = "+" + experienceMana;
            }
        }

        int abilityLevel = Math.min(99, getLevel());
        String manaFromAbility = abilityLevel > 0 ? "+" + abilityLevel : "0";

        DragonStateHandler data = DragonStateProvider.getData(player);
        double attributeValue = Objects.requireNonNull(data.getBody()).value().getAttributeValue(data.getTypeNameLowerCase(), data.getSize(), DSAttributes.MANA);
        int bodyMana = (int) Math.min(99, attributeValue - Objects.requireNonNull(player.getAttribute(DSAttributes.MANA)).getBaseValue());

        String manaFromBody = "0";

        if (bodyMana > 0) {
            manaFromBody = "+" + bodyMana;
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