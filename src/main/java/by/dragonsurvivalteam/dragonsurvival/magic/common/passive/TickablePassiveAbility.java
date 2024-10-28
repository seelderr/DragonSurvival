package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;

import net.minecraft.world.entity.player.Player;

public abstract class TickablePassiveAbility extends PassiveDragonAbility {
	public abstract void onTick(Player player);
}