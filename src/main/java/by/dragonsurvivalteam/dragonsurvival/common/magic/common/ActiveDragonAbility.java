package by.dragonsurvivalteam.dragonsurvival.common.magic.common;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAbilityCasting;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ActiveDragonAbility extends DragonAbility{
	public int errorTicks;
	public ITextComponent errorMessage;
	protected int manaCost;
	protected Integer[] requiredLevels;
	protected int castTime;
	protected int currentCastingTime;
	protected int abilityCooldown;
	protected int currentCooldown;

	public ActiveDragonAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels){
		super(type, id, icon, minLevel, maxLevel);
		this.manaCost = manaCost;
		this.requiredLevels = requiredLevels;
		this.abilityCooldown = cooldown;
		this.castTime = castTime;
	}

	@Override
	public ActiveDragonAbility createInstance(){
		return new ActiveDragonAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}

	@Override
	public ArrayList<ITextComponent> getInfo(){
		ArrayList<ITextComponent> components = super.getInfo();

		components.add(new TranslationTextComponent("ds.skill.mana_cost", getManaCost()));

		if(getCastingTime() > 0){
			components.add(new TranslationTextComponent("ds.skill.cast_time", Functions.ticksToSeconds(getCastingTime())));
		}

		if(getMaxCooldown() > 0){
			components.add(new TranslationTextComponent("ds.skill.cooldown", Functions.ticksToSeconds(getMaxCooldown())));
		}

		return components;
	}

	public int getManaCost(){
		return player != null && player.hasEffect(DragonEffects.SOURCE_OF_MAGIC) ? 0 : manaCost;
	}

	@Override
	public void onKeyPressed(PlayerEntity player){
		this.onActivation(player);
	}

	public void onActivation(PlayerEntity player){
		if(player == null){
			return;
		}

		resetSkill();
		consumeMana(player);
	}

	public void consumeMana(PlayerEntity player){
		ManaHandler.consumeMana(player, this.getManaCost());
	}

	public void resetSkill(){
		stopCasting();
		startCooldown();

		DragonStateProvider.getCap(getPlayer()).ifPresent(dragonStateHandler -> {
			if(!player.level.isClientSide){
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncAbilityCasting(player.getId(), null));
			}
			dragonStateHandler.getMagic().setCurrentlyCasting(null);
		});
	}

	public void startCooldown(){
		this.currentCooldown = this.getMaxCooldown();
	}

	public CompoundNBT saveNBT(){
		CompoundNBT nbt = super.saveNBT();
		nbt.putInt("cooldown", currentCooldown);
		nbt.putInt("castTime", currentCastingTime);

		return nbt;
	}

	public void loadNBT(CompoundNBT nbt){
		super.loadNBT(nbt);
		currentCooldown = nbt.getInt("cooldown");
		currentCastingTime = nbt.getInt("castTime");
	}

	@Override
	public int hashCode(){
		int result = Objects.hash(getManaCost(), castTime);
		result = 31 * result + Arrays.hashCode(getRequiredLevels());
		return result;
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof ActiveDragonAbility)){
			return false;
		}
		ActiveDragonAbility ability = (ActiveDragonAbility)o;
		return getManaCost() == ability.getManaCost() && castTime == ability.castTime && Arrays.equals(getRequiredLevels(), ability.getRequiredLevels());
	}

	public int getMaxCooldown(){
		return abilityCooldown;
	}

	public int getCastingTime(){
		return castTime;
	}

	public int getNextRequiredLevel(){
		if(getLevel() <= maxLevel){
			if(getRequiredLevels().length > getLevel() && getLevel() > 0){
				return getRequiredLevels()[getLevel()];
			}
		}

		return 0;
	}

	@Override
	public int getLevel(){
		if(isDisabled()){
			return 0;
		}

		if(requiredLevels != null && getPlayer() != null){
			int level = 0;

			for(int req : requiredLevels){
				if(getPlayer().experienceLevel >= req || ConfigHandler.SERVER.noEXPRequirements.get()){
					level++;
				}
			}

			return level;
		}
		return super.getLevel();
	}

	public Integer[] getRequiredLevels(){
		return requiredLevels;
	}

	public int getCurrentRequiredLevel(){
		if(getRequiredLevels().length >= getLevel() && getLevel() > 0){
			return getRequiredLevels()[getLevel() - 1];
		}

		return 0;
	}

	public int getLevelCost(){
		return 1 + (int)(0.75 * getLevel());
	}

	public boolean canRun(PlayerEntity player, int keyMode){
		if(player.isCreative()){
			return true;
		}
		if(player.isSpectator()){
			return false;
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);
		if(handler == null){
			return false;
		}

		if(!this.canConsumeMana(player)){
			if(keyMode == GLFW.GLFW_PRESS){
				errorMessage = new TranslationTextComponent("ds.skill_mana_check_failure");
				errorTicks = Functions.secondsToTicks(5);
				player.playSound(SoundEvents.GENERIC_SPLASH, 0.15f, 100f);
			}
			stopCasting();
			return false;
		}

		if(this.getCooldown() != 0){
			if(keyMode == GLFW.GLFW_PRESS){
				errorMessage = new TranslationTextComponent("ds.skill_cooldown_check_failure", nf.format(this.getCooldown() / 20F) + "s").withStyle(TextFormatting.RED);
				errorTicks = Functions.secondsToTicks(5);
				player.playSound(SoundEvents.WITHER_SHOOT, 0.05f, 100f);
			}
			stopCasting();
			return false;
		}

		if(!canMoveWhileCasting() || ServerFlightHandler.isGliding(player)){
			if(handler.isWingsSpread() && player.isFallFlying() || (!player.isOnGround() && player.fallDistance > 0.15F)){
				if(keyMode == GLFW.GLFW_PRESS){
					errorMessage = new TranslationTextComponent("ds.skill.nofly");
					errorTicks = Functions.secondsToTicks(5);
					player.playSound(SoundEvents.WITHER_SHOOT, 0.05f, 100f);
				}
				stopCasting();
				return false;
			}
		}

		return true;
	}

	public boolean canConsumeMana(PlayerEntity player){
		return ManaHandler.getCurrentMana(player) >= this.getManaCost() || ConfigHandler.SERVER.consumeEXPAsMana.get() && ((player.totalExperience / 10) >= getManaCost() || player.experienceLevel > 0);
	}

	public int getCooldown(){
		return this.currentCooldown;
	}

	public void stopCasting(){
		this.currentCastingTime = 0;
	}

	public boolean canMoveWhileCasting(){return true;}

	public void setCooldown(int newCooldown){
		this.currentCooldown = newCooldown;
	}

	public void decreaseCooldownTimer(){
		if(this.currentCooldown > 0){
			this.currentCooldown--;
		}
	}

	public void tickCasting(){
		if(this.currentCastingTime <= this.getCastingTime()){
			this.currentCastingTime++;
		}
	}

	public void setCastTime(int castTime){
		this.currentCastingTime = castTime;
	}

	public int getCurrentCastTimer(){
		return currentCastingTime;
	}

	public AbilityAnimation getStartingAnimation(){return null;}

	public AbilityAnimation getLoopingAnimation(){return null;}

	public AbilityAnimation getStoppingAnimation(){return null;}
}