package by.dragonsurvivalteam.dragonsurvival.magic.common.active;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import net.minecraft.world.entity.player.Player;

public abstract class InstantCastAbility extends ActiveDragonAbility {
	public abstract void onCast(Player player);
	public boolean castFinished = false;

	@Override
	public void onKeyPressed(Player player, Runnable onFinish, long castStartTime){
		//System.out.println("Instant cast "+ getName() + " is on cooldown for " + getCurrentCooldown());
		if (!castFinished) {
			//System.out.println("Instant cast " + getName() + " fired at " + player.level.dayTime() + " with start time " + castStartTime);
			onCast(player);
			ManaHandler.consumeMana(player, getManaCost());
			startCooldown();
			onFinish.run();
		}
	}
	
	public void onKeyReleased(Player player) {
		//System.out.println("Key released for ability " + getName() + " and castFinished = " + castFinished);
		castFinished = false;
	}
}