package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.items.armor.PermanentEnchantmentItem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public class MixinSmithingMenu {

    @Inject(method="onTake", at = @At(value= "RETURN"))
    private void addDefaultEnchantments(Player pPlayer, ItemStack pStack, CallbackInfo ci) {
        if (pStack.getItem() instanceof PermanentEnchantmentItem item) {
            ItemEnchantments.Mutable itemEnchantments$mutable = new ItemEnchantments.Mutable(pStack.getTagEnchantments());
            for (Holder<Enchantment> e: item.getDefaultEnchantments().keySet()) {
                itemEnchantments$mutable.set(e, 1);
            }
            EnchantmentHelper.setEnchantments(pStack, item.getDefaultEnchantments());
        }
    }
}
