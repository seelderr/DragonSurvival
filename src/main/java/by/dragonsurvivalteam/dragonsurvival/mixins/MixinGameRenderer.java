package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( GameRenderer.class )
public class MixinGameRenderer{
	@Inject( at = @At( "HEAD" ), method = "getNightVisionScale", cancellable = true )
	private static void getNightVisionScale(LivingEntity entity, float scale, CallbackInfoReturnable<Float> ci){
		if(!(entity instanceof Player player) || !player.hasEffect(MobEffects.NIGHT_VISION))
			return;

		if(ClientConfig.stableNightVision){
			ci.setReturnValue(1f);
		}
	}
}