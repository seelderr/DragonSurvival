package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;

@Mixin( ServerPlayerGameMode.class )
public class MixinPlayerInteractionManager{
	//	@Redirect( method = "destroyBlock",
	//		at = @At( value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
	//	public ItemStack getTools(LivingEntity player)
	//	{
	//		return ClawToolHandler.getDragonTools(player);
	//	}
}