package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin( ServerPlayerGameMode.class )
public class MixinServerPlayerGameMode{
	@Redirect( method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At( value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;" ) )
	public ItemStack getTools(ServerPlayer instance){
		instance.detectEquipmentUpdates();
		return ClawToolHandler.getDragonTools(instance);
	}

	@Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canHarvestBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"))
	private boolean modifiedHarvestCheck(BlockState instance, BlockGetter blockGetter, BlockPos pos, Player player) {
		boolean originalCheck = instance.canHarvestBlock(blockGetter, pos, player);
		// This works but not sure about the performance impact etc.
		ResourceLocation location = ForgeRegistries.BLOCKS.getKey(instance.getBlock());

		if (location != null && location.getNamespace().equals("minecraft")) {
			// Don't bother checking vanilla blocks - they should work by default
			return originalCheck;
		}

		if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof TieredItem) {
			// If the player had a tool in the hand don't bother checking for dragon tools
			return originalCheck;
		}

		if (!originalCheck && player.level instanceof ServerLevel) {
			DragonStateHandler cap = DragonUtils.getHandler(player);

			UUID id = UUID.randomUUID();
			FakePlayer fakePlayer = new FakePlayer((ServerLevel) player.level, new GameProfile(id, id.toString()));

			for (int i = 0; i < 4; i++) {
				ItemStack tool = cap.getClawToolData().getClawsInventory().getItem(i);

				if (tool == ItemStack.EMPTY || !tool.isCorrectToolForDrops(instance)) {
					continue;
				}

				// If certain mods have problems: Could also copy other stuff (inventory, capabilities etc.)
				fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, tool);
				boolean reCheck = instance.canHarvestBlock(blockGetter, pos, fakePlayer);

				if (reCheck) {
					return true;
				}
			}
		}

		return originalCheck;
	}
}