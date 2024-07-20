package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.items.armor.PermanentEnchantmentItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method="isEnchantable()Z", at= @At(value = "RETURN"), cancellable = true)
    public void dragonSurvival$isEnchantable(CallbackInfoReturnable<Boolean> cir) {
        if (((ItemStack)(Object)this).getItem() instanceof PermanentEnchantmentItem item) {
            cir.setReturnValue(PermanentEnchantmentItem.isEnchantable((ItemStack)(Object) this));
        }
    }
}
