package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( PlayerController.class )
public class MixinPlayerController{
	@Shadow
	private GameType localPlayerMode;
	@Shadow
	private ClientPlayNetHandler connection;

	@Inject( at = @At( "HEAD" ), method = "attack", cancellable = true )
	public void attack(PlayerEntity pPlayer, Entity pTargetEntity, CallbackInfo callbackInfo){
		if(pTargetEntity instanceof DragonHitboxPart){
			callbackInfo.cancel();

			this.ensureHasSentCarriedItem();
			this.connection.send(new CUseEntityPacket(((DragonHitboxPart)pTargetEntity).parentMob, pPlayer.isShiftKeyDown()));
			if(this.localPlayerMode != GameType.SPECTATOR){
				pPlayer.attack(((DragonHitboxPart)pTargetEntity).parentMob);
				pPlayer.resetAttackStrengthTicker();
			}
		}
	}

	@Shadow
	private void ensureHasSentCarriedItem(){}
}