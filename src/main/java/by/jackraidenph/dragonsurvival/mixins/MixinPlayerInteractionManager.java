package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.common.handlers.magic.ClawToolHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin( ServerPlayerGameMode.class)
public class MixinPlayerInteractionManager
{
	@Redirect( method = "destroyBlock",
	           at = @At( value="INVOKE", target="Lnet/minecraft/world/entity/player/ServerPlayer;getMainHandItem()Lnet/minecraft/item/ItemStack;"))
	public ItemStack getTools(ServerPlayer player)
	{
		return ClawToolHandler.getDragonTools(player);
	}
}
