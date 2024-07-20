package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.items.armor.PermanentEnchantmentItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public class MixinGrindstoneMenu {
    @Shadow @Final private Container repairSlots;

    @Inject(method="removeNonCursesFrom(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At(value= "RETURN"))
    private void addDefaultEnchantmentsBack(ItemStack pItem, CallbackInfoReturnable<ItemStack> cir) {
        if (pItem.getItem() instanceof PermanentEnchantmentItem item) {
            EnchantmentHelper.setEnchantments(pItem, item.getDefaultEnchantments());
        }
    }
}

