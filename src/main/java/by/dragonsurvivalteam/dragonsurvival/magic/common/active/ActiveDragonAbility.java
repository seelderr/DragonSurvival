package by.dragonsurvivalteam.dragonsurvival.magic.common.active;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.AbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public abstract class ActiveDragonAbility extends DragonAbility {
    @Translation(type = Translation.Type.MISC, comments = "§fNot enough§r §cmana or experience§r!")
    private static final String NO_MANA = Translation.Type.GUI.wrap("ability.no_mana");

    @Translation(type = Translation.Type.MISC, comments = "§fThis ability is §r§cnot ready§r§f yet!§r (%s)")
    private static final String COOLDOWN = Translation.Type.GUI.wrap("ability.cooldown");

    @Translation(type = Translation.Type.MISC, comments = "§fThis skill cannot be used §r§cwhile flying§r§f!§f")
    private static final String FLYING = Translation.Type.GUI.wrap("ability.flying");

    private int currentCooldown;

    public abstract int getManaCost();

    public void startCooldown() {
        currentCooldown = getSkillCooldown();
    }

    @Override
    public CompoundTag saveNBT() {
        return super.saveNBT(); // Client is the only tracker of cooldown state and doesn't need to overwrite its own cooldowns
    }

    @Override
    public void loadNBT(CompoundTag nbt) {
        super.loadNBT(nbt);
    }

    public abstract Integer[] getRequiredLevels();

    public abstract int getSkillCooldown();

    public int getNextRequiredLevel() { // TODO :: unused
        if (getLevel() <= getMaxLevel())
            if (getRequiredLevels().length > getLevel() && getLevel() > 0)
                return getRequiredLevels()[getLevel()];

        return 0;
    }

    @Override
    public int getLevel() {
        Player player = getPlayer();

        if (player == null || isDisabled()) {
            return 0;
        }

        if (ServerConfig.noExperienceRequirements) {
            return getMaxLevel();
        }

        Integer[] levels = getRequiredLevels();

        if (levels == null) {
            return 0;
        }

        int level = 0;

        for (int requiredLevel : levels) {
            if (player.experienceLevel >= requiredLevel) {
                level++;
            }
        }

        return level;
    }

    public int getCurrentRequiredLevel() {
        if (getRequiredLevels().length >= getLevel() && getLevel() > 0)
            return getRequiredLevels()[getLevel() - 1];

        return 0;
    }

    public int getLevelCost() { // TODO :: unused
        return 1 + (int) (0.75 * getLevel());
    }

    public boolean canCastSkill(Player player) {
        if (player.isCreative())
            return true;

        DragonStateHandler handler = DragonStateProvider.getData(player);

        if (hasCastDisablingEffect(player)) {
            return false;
        }

        if (!canConsumeMana(player)) {
            MagicHUD.castingError(Component.translatable(NO_MANA));
            return false;
        }

        if (getCurrentCooldown() != 0) {
            MagicHUD.castingError(Component.translatable(COOLDOWN, nf.format(getCurrentCooldown() / 20F) + "s").withStyle(ChatFormatting.RED));
            return false;
        }

        if (requiresStationaryCasting() || ServerFlightHandler.isGliding(player)) {
            if (handler.isWingsSpread() && player.isFallFlying() || !player.onGround() && player.fallDistance > 0.15F) {
                MagicHUD.castingError(Component.translatable(FLYING));
                return false;
            }
        }

        return !player.isSpectator();
    }

    public boolean hasCastDisablingEffect(Player player) {
        return player.hasEffect(DSEffects.MAGIC_DISABLED);
    }

    public boolean canConsumeMana(Player player) {
        return ManaHandler.hasEnoughMana(player, getManaCost());
    }

    public void tickCooldown() {
        if (getCurrentCooldown() > 0)
            setCurrentCooldown(getCurrentCooldown() - 1);
        else if (getCurrentCooldown() < 0)
            setCurrentCooldown(0);
    }

    public boolean requiresStationaryCasting() {
        return true;
    }

    public AbilityAnimation getStartingAnimation() {
        return null;
    }

    public AbilityAnimation getLoopingAnimation() {
        return null;
    }

    public AbilityAnimation getStoppingAnimation() {
        return null;
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();

        components.add(Component.translatable(LangKey.ABILITY_MANA_COST, getManaCost()));

        if (getSkillCooldown() > 0)
            components.add(Component.translatable(LangKey.ABILITY_COOLDOWN, Functions.ticksToSeconds(getSkillCooldown())));

        return components;
    }

    public void setCurrentCooldown(int currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }
}