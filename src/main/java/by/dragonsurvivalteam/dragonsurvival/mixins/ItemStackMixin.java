package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.items.armor.PermanentEnchantmentItem;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Redirect(method="getTooltipLines", at= @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"))
    public void dragonSurvival$getTooltipLines(ItemStack instance, DataComponentType pComponent, Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag, @Local Item.TooltipContext pTooltipContext, @Local Consumer<Component> pConsumer) {
        if (((ItemStack)(Object)this).getItem() instanceof PermanentEnchantmentItem item) {
            //for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : item.getDefaultEnchantments().entrySet()) {
                ItemEnchantments old = ((ItemStack)(Object)this).get(DataComponents.ENCHANTMENTS);
                try {
                    ((ItemStack) (Object) this).set(DataComponents.ENCHANTMENTS, ((ItemStack) (Object) this).getAllEnchantments(pTooltipContext.registries().lookup(Registries.ENCHANTMENT).orElseThrow()));
                    ((ItemStack) (Object) this).addToTooltip(DataComponents.ENCHANTMENTS, pTooltipContext, pConsumer, pTooltipFlag);
                } catch (NullPointerException | NoSuchElementException ignored) {}
                ((ItemStack)(Object)this).set(DataComponents.ENCHANTMENTS, old);
            //}
            //((ItemStack)(Object)this).getItem().appendHoverText(((ItemStack)(Object)this), pTooltipContext, list, pTooltipFlag);
        }
    }
}
