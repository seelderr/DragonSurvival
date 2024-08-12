package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VaultBlockEntity.Server.class)
public class VaultBlockEntityServerMixin {
    /*@ModifyExpressionValue(method="resolveItemsToEject", at= @At(value = "NEW", target = "(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;"))
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
    }*/
}
