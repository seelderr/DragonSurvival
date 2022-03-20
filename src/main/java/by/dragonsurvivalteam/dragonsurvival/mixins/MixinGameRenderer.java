package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( GameRenderer.class )
public class MixinGameRenderer{
	@Inject( at = @At( "HEAD" ), method = "getNightVisionScale", cancellable = true )
	private static void getNightVisionScale(LivingEntity entity, float scale, CallbackInfoReturnable<Float> ci){
		if(!(entity instanceof PlayerEntity)){
			return;
		}

		ci.setReturnValue(1f);
	}
}