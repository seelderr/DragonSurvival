package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.DSTags;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VaultBlockEntity.Server.class)
public class VaultBlockEntityServerMixin {
    @ModifyExpressionValue(method="resolveItemsToEject", at= @At(value = "NEW", target = "(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;"))
    private static LootParams.Builder addToolParam(LootParams.Builder value, @Local(argsOnly = true) Player pPlayer) {
        return value.withParameter(LootContextParams.TOOL, pPlayer.getItemInHand(pPlayer.getUsedItemHand()));
    }

    @Inject(method = "isValidToInsert", at=@At("RETURN"), cancellable = true)
    private static void checkIfDragonKeyIsValid(VaultConfig pConfig, ItemStack pStack, CallbackInfoReturnable<Boolean> cir) {
        if (pStack.is(DSTags.VAULT_KEYS)) {
            cir.setReturnValue(true);
        }
    }
}
