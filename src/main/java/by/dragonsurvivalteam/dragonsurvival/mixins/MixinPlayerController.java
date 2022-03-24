package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( MultiPlayerGameMode.class )
public class MixinPlayerController
{
	@Inject( at = @At("HEAD"), method = "attack", cancellable = true)
	public void attack(Player pPlayer, Entity pTargetEntity, CallbackInfo callbackInfo) {
		if(pTargetEntity  instanceof DragonHitboxPart){
			callbackInfo.cancel();
			
			this.ensureHasSentCarriedItem();
			this.connection.send(ServerboundInteractPacket.createAttackPacket(pTargetEntity, pPlayer.isShiftKeyDown()));
			if (this.localPlayerMode != GameType.SPECTATOR) {
				pPlayer.attack(((DragonHitboxPart)pTargetEntity).parentMob);
				pPlayer.resetAttackStrengthTicker();
			}
		}
	}
	@Shadow
	private GameType localPlayerMode;
	
	@Shadow
	private ClientPacketListener connection;
	
	@Shadow
	private void ensureHasSentCarriedItem() {}
}
