package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.DSDataComponents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VaultBlockEntity.Server.class)
public class VaultBlockEntityServerMixin {
    @ModifyReturnValue(method="isValidToInsert", at= @At(value = "RETURN"))
    private static boolean dragonSurvival$isValidToInsert(boolean original, VaultConfig pConfig, ItemStack pStack){
        if (pStack.getComponents().get(DSDataComponents.TARGET_POSITION) != null) {
            // Skip the components check if it is our vaults and keys
            return pStack.getCount() >= pConfig.keyItem().getCount();
        }
        return original;
    }
}
