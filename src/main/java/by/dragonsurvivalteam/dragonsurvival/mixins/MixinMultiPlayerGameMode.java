package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( MultiPlayerGameMode.class )
public class MixinMultiPlayerGameMode{
	@Shadow
	private GameType localPlayerMode;
	@Final
	@Shadow
	private ClientPacketListener connection;

	@Inject( at = @At( "HEAD" ), method = "attack", cancellable = true )
	public void attack(Player pPlayer, Entity pTarget, CallbackInfo callbackInfo){
		if(pTarget instanceof DragonHitboxPart){
			callbackInfo.cancel();

			this.ensureHasSentCarriedItem();
			this.connection.send(ServerboundInteractPacket.createAttackPacket(pTarget, pPlayer.isShiftKeyDown()));
			if(this.localPlayerMode != GameType.SPECTATOR){
				pPlayer.attack(((DragonHitboxPart)pTarget).parentMob);
				pPlayer.resetAttackStrengthTicker();
			}
		}
	}

	@Shadow
	private void ensureHasSentCarriedItem(){}
}