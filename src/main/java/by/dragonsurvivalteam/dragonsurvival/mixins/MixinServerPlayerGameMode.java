package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin( ServerPlayerGameMode.class )
public class MixinServerPlayerGameMode{
	@Redirect( method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At( value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;" ) )
	public ItemStack getTools(ServerPlayer instance){
		instance.detectEquipmentUpdates();
		return ClawToolHandler.getDragonTools(instance);
	}
}