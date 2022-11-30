package by.dragonsurvivalteam.dragonsurvival.magic.common.active;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public abstract class ChannelingCastAbility extends ActiveDragonAbility {
	@Getter
	public int chargeTime = 0;

	public abstract int getSkillChargeTime();
	public abstract int getChargingManaCost();

	@Override
	public void onKeyPressed(Player player, Runnable onFinish){
		chargeTime++;

		if(chargeTime >= getSkillChargeTime()){
			onChanneling(player, chargeTime - getSkillChargeTime());

			if(player.tickCount % 40 == 0){
				ManaHandler.consumeMana(player, getManaCost());
			}
		}else{
			onCharging(player, chargeTime);

			if(chargeTime == getSkillChargeTime() / 2){
				ManaHandler.consumeMana(player, getChargingManaCost());
			}
		}
	}

	public boolean canConsumeMana(Player player){
		int manaCost = chargeTime <= getSkillChargeTime() / 2 ? getManaCost() + getChargingManaCost() : getManaCost();

		return ManaHandler.getCurrentMana(player) >= manaCost
		       || ServerConfig.consumeEXPAsMana && (player.totalExperience / 10 >= manaCost
		                                            || player.experienceLevel > 0);
	}

	@Override
	public void onKeyReleased(Player player){
		if(chargeTime >= getSkillChargeTime()){
			castComplete(player);
			startCooldown();
		}

		chargeTime = 0;
	}

	public abstract void onCharging(Player player, int currentChargeTime);
	public abstract void onChanneling(Player player, int castDuration);
	public abstract void castComplete(Player player);

	@Override
	public CompoundTag saveNBT(){
		CompoundTag tag = super.saveNBT();
		tag.putInt("chargeTime", chargeTime);
		return tag;
	}

	@Override
	public void loadNBT(CompoundTag nbt){
		super.loadNBT(nbt);
		chargeTime = nbt.getInt("chargeTime");
	}

	@Override
	public ArrayList<Component> getInfo(){
		ArrayList<Component> components = super.getInfo();

		if(getSkillChargeTime() > 0)
			components.add(new TranslatableComponent("ds.skill.cast_time", Functions.ticksToSeconds(getSkillChargeTime())));

		return components;
	}
}