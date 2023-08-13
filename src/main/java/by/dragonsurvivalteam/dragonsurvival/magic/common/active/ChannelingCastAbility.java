package by.dragonsurvivalteam.dragonsurvival.magic.common.active;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public abstract class ChannelingCastAbility extends ActiveDragonAbility {
	public int chargeTime = 0;

	public abstract int getSkillChargeTime();
	public abstract int getContinuousManaCostTime();
	public abstract int getInitManaCost();

	@Override
	public void onKeyPressed(Player player, Runnable onFinish){
		chargeTime++;

		if(chargeTime >= getSkillChargeTime()){
			onChanneling(player, chargeTime - getSkillChargeTime());

			if(chargeTime % getContinuousManaCostTime() == 0){
				ManaHandler.consumeMana(player, getManaCost());
			}
		}else{
			onCharging(player, chargeTime);

			if(chargeTime == getSkillChargeTime() / 2){
				ManaHandler.consumeMana(player, getInitManaCost());
			}
		}
	}

	@Override
	public boolean canConsumeMana(Player player){
		int manaCost = chargeTime < getSkillChargeTime() / 2 ? getManaCost() + getInitManaCost() : getManaCost();
		return ManaHandler.canConsumeMana(player, manaCost);
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
			components.add(Component.translatable("ds.skill.cast_time", Functions.ticksToSeconds(getSkillChargeTime())));

		return components;
	}

	public int getChargeTime() {
		return chargeTime;
	}
}