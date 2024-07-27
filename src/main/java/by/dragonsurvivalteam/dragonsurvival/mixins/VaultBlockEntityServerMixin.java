package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.DSDataComponents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VaultBlockEntity.Server.class)
public class VaultBlockEntityServerMixin {
    @ModifyExpressionValue(method="resolveItemsToEject", at= @At(value = "NEW", target = "(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;"))
    private static LootParams.Builder addToolParam(LootParams.Builder value, @Local(argsOnly = true) Player pPlayer) {
        return value.withParameter(LootContextParams.TOOL, pPlayer.getItemInHand(pPlayer.getUsedItemHand()));
    }

    @Redirect(method="tryInsertKey", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/vault/VaultBlockEntity$Server;isValidToInsert(Lnet/minecraft/world/level/block/entity/vault/VaultConfig;Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean dragonSurvival$isValidToInsert(VaultConfig pConfig, ItemStack pStack, ServerLevel pLevel,
                                           BlockPos pPos, BlockState pState, VaultConfig pConfig2, VaultServerData pServerData, VaultSharedData pSharedData, Player pPlayer, ItemStack pStack2){
        if (pStack.getComponents().get(DSDataComponents.VALID_VAULTS) instanceof ExtraCodecs.TagOrElementLocation validVaults) {
            return pState.getBlockHolder().is(validVaults.id()) && pStack.getCount() >= pConfig.keyItem().getCount();
        }
        return VaultBlockEntity.Server.isValidToInsert(pConfig2, pStack2);
    }
}
