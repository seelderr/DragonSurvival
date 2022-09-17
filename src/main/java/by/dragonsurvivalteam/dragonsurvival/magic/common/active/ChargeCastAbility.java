package by.dragonsurvivalteam.dragonsurvival.magic.common.active;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public abstract class ChargeCastAbility extends ActiveDragonAbility {
	@Getter
	public int castTime = 0;

	public abstract int getSkillCastingTime();

	@Override
	public void onKeyPressed(Player player, Runnable onFinish){
		if(castTime >= getSkillCastingTime()){
			castTime = 0;
			castingComplete(player);
			startCooldown();

			if(player.tickCount % 20 == 0){
				ManaHandler.consumeMana(player, getManaCost());
			}

			onFinish.run();
		}else{
			castTime++;
			onCasting(player, castTime);
		}
	}

	@Override
	public void onKeyReleased(Player player){
		castTime = 0;
	}

	public abstract void onCasting(Player player, int currentCastTime);
	public abstract void castingComplete(Player player);

	@Override
	public CompoundTag saveNBT(){
		CompoundTag tag = super.saveNBT();
		tag.putInt("castTime", castTime);
		return tag;
	}

	@Override
	public void loadNBT(CompoundTag nbt){
		super.loadNBT(nbt);
		castTime = nbt.getInt("castTime");
	}

	@Override
	public ArrayList<Component> getInfo(){
		ArrayList<Component> components = super.getInfo();

		if(getSkillCastingTime() > 0)
			components.add(new TranslatableComponent("ds.skill.cast_time", Functions.ticksToSeconds(getSkillCastingTime())));

		return components;
	}
}