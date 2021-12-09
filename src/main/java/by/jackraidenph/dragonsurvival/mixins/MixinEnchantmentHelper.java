package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( EnchantmentHelper.class )
public class MixinEnchantmentHelper
{
	@Inject( at = @At("HEAD"), method = "hasAquaAffinity", cancellable = true)
	private static void hasAquaAffinity(LivingEntity entity, CallbackInfoReturnable<Boolean> ci) {
		if (!(entity instanceof PlayerEntity))
			return;
		
		PlayerEntity player = (PlayerEntity) entity;
		
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.getType() == DragonType.SEA) {
				ci.setReturnValue(true);
			}
		});
	}
}
