package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin( ServerPlayerGameMode.class )
public class MixinPlayerInteractionManager{
		@Redirect( method = "destroyBlock",
			at = @At( value="HEAD", target="Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
		public ItemStack getTools(Player player)
		{
			return ClawToolHandler.getDragonTools(player);
		}
}