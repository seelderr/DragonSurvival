package by.dragonsurvivalteam.dragonsurvival.magic.common.active;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.AbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public abstract class ActiveDragonAbility extends DragonAbility{
	private int currentCooldown;

	public abstract int getManaCost();

	public void startCooldown(){
		currentCooldown = getSkillCooldown();
	}

	@Override
	public CompoundTag saveNBT(){
		return super.saveNBT(); // Client is the only tracker of cooldown state and doesn't need to overwrite its own cooldowns
	}

	@Override
	public void loadNBT(CompoundTag nbt){
		super.loadNBT(nbt);
		//currentCooldown = nbt.getInt("cooldown");
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

		DragonStateHandler handler = DragonStateProvider.getData(player);

		if(hasCastDisablingEffect(player)){
			return false;
		}

		if(!canConsumeMana(player)){
			MagicHUD.castingError(Component.translatable("ds.skill_mana_check_failure"));
			return false;
		}

		if(getCurrentCooldown() != 0){
			MagicHUD.castingError(Component.translatable("ds.skill_cooldown_check_failure", nf.format(getCurrentCooldown() / 20F) + "s").withStyle(ChatFormatting.RED));
			return false;
		}

		if(requiresStationaryCasting() || ServerFlightHandler.isGliding(player)){
			if(handler.isWingsSpread() && player.isFallFlying() || !player.onGround() && player.fallDistance > 0.15F){
				MagicHUD.castingError(Component.translatable("ds.skill.nofly"));
				return false;
			}
		}

		return !player.isSpectator();
	}

	public boolean hasCastDisablingEffect(Player player) {
		return player.hasEffect(DSEffects.MAGIC_DISABLED);
	}

	public boolean canConsumeMana(Player player){
		return ManaHandler.canConsumeMana(player, getManaCost());
	}

	public void tickCooldown(){
		if(getCurrentCooldown() > 0)
			setCurrentCooldown(getCurrentCooldown()-1);
		else if (getCurrentCooldown() < 0)
			setCurrentCooldown(0);
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

		components.add(Component.translatable("ds.skill.mana_cost", getManaCost()));

		if(getSkillCooldown() > 0)
			components.add(Component.translatable("ds.skill.cooldown", Functions.ticksToSeconds(getSkillCooldown())));

		return components;
	}

	public void setCurrentCooldown(int currentCooldown) {
		this.currentCooldown = currentCooldown;
	}

	public int getCurrentCooldown() {
		return currentCooldown;
	}
}