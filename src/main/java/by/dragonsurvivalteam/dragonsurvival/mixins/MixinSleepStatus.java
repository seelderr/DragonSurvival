package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin( SleepStatus.class )
public class MixinSleepStatus{
	@Shadow private int activePlayers;
	@Shadow private int sleepingPlayers;

	/**
	 * @author
	 */
	@Overwrite
	public boolean update(List<ServerPlayer> pPlayers){
		int i = this.activePlayers;
		int j = this.sleepingPlayers;
		this.activePlayers = 0;
		this.sleepingPlayers = 0;

		for(ServerPlayer serverplayer : pPlayers){
			if(!serverplayer.isSpectator()){
				++this.activePlayers;

				DragonStateHandler handler = DragonUtils.getHandler(serverplayer);

				if(serverplayer.isSleeping() || handler.treasureResting){
					++this.sleepingPlayers;
				}
			}
		}

		return (j > 0 || this.sleepingPlayers > 0) && (i != this.activePlayers || j != this.sleepingPlayers);
	}
}