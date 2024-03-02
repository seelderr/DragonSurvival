package by.dragonsurvivalteam.dragonsurvival.magic.common.active;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.AbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public abstract class ActiveDragonAbility extends DragonAbility{
	private int currentCooldown;

	public abstract int getManaCost();

	public void startCooldown(){
		currentCooldown = getSkillCooldown();
	}

	@Override
	public CompoundTag saveNBT(){
		CompoundTag nbt = super.saveNBT();
		nbt.putInt("cooldown", currentCooldown);
		return nbt;
	}

	@Override
	public void loadNBT(CompoundTag nbt){
		super.loadNBT(nbt);
		currentCooldown = nbt.getInt("cooldown");
	}

	public abstract Integer[] getRequiredLevels();
	public abstract int getSkillCooldown();

	public int getNextRequiredLevel(){
		if(getLevel() <= getMaxLevel())
			if(getRequiredLevels().length > getLevel() && getLevel() > 0)
				return getRequiredLevels()[getLevel()];

		return 0;
	}

	@Override
	public int getLevel(){
		if(isDisabled())
			return 0;

		if(getRequiredLevels() != null && getPlayer() != null){
			int level = 0;

			for(int req : getRequiredLevels())
				if(getPlayer().experienceLevel >= req || ServerConfig.noEXPRequirements)
					level++;

			return level;
		}
		return super.getLevel();
	}

	public int getCurrentRequiredLevel(){
		if(getRequiredLevels().length >= getLevel() && getLevel() > 0)
			return getRequiredLevels()[getLevel() - 1];

		return 0;
	}

	public int getLevelCost(){
		return 1 + (int)(0.75 * getLevel());
	}

	public boolean canCastSkill(Player player){
		if(player.isCreative())
			return true;

		DragonStateHandler handler = DragonUtils.getHandler(player);

		if(!canConsumeMana(player)){
			ClientMagicHUDHandler.castingError(new TranslatableComponent("ds.skill_mana_check_failure"));
			return false;
		}

		if(getCurrentCooldown() != 0){
			ClientMagicHUDHandler.castingError(new TranslatableComponent("ds.skill_cooldown_check_failure", nf.format(getCurrentCooldown() / 20F) + "s").withStyle(ChatFormatting.RED));
			return false;
		}

		if(requiresStationaryCasting() || ServerFlightHandler.isGliding(player)){
			if(handler.isWingsSpread() && player.isFallFlying() || !player.isOnGround() && player.fallDistance > 0.15F){
				ClientMagicHUDHandler.castingError(new TranslatableComponent("ds.skill.nofly"));
				return false;
			}
		}

		return !player.isSpectator();
	}

	public boolean canConsumeMana(Player player){
		return ManaHandler.canConsumeMana(player, getManaCost());
	}

	public void tickCooldown(){
		if(getCurrentCooldown() > 0)
			setCurrentCooldown(getCurrentCooldown()-1);
	}

	public boolean requiresStationaryCasting(){
		return true;
	}

	public AbilityAnimation getStartingAnimation(){
		return null;
	}

	public AbilityAnimation getLoopingAnimation(){
		return null;
	}

	public AbilityAnimation getStoppingAnimation(){
		return null;
	}

	@Override
	public ArrayList<Component> getInfo(){
		ArrayList<Component> components = super.getInfo();

		components.add(new TranslatableComponent("ds.skill.mana_cost", getManaCost()));

		if(getSkillCooldown() > 0)
			components.add(new TranslatableComponent("ds.skill.cooldown", Functions.ticksToSeconds(getSkillCooldown())));

		return components;
	}

	public void setCurrentCooldown(int currentCooldown) {
		this.currentCooldown = currentCooldown;
	}

	public int getCurrentCooldown() {
		return currentCooldown;
	}
}