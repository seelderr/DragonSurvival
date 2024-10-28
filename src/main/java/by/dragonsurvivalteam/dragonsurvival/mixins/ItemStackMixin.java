package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.items.armor.PermanentEnchantmentItem;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"))
	public void dragonSurvival$getTooltipLines(ItemStack instance, DataComponentType pComponent, Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag, @Local(argsOnly = true) Item.TooltipContext pTooltipContext, @Local Consumer<Component> pConsumer) {
		if (((ItemStack) (Object) this).getItem() instanceof PermanentEnchantmentItem) {
			ItemEnchantments old = instance.get(DataComponents.ENCHANTMENTS);
			try {
				// Set the enchantment's to the built in item enchantments for the purposes of adding it to the tooltip, then restore the old enchantments
				instance.set(DataComponents.ENCHANTMENTS, ((ItemStack) (Object) this).getAllEnchantments(pTooltipContext.registries().lookup(Registries.ENCHANTMENT).orElseThrow()));
				instance.addToTooltip(DataComponents.ENCHANTMENTS, pTooltipContext, pConsumer, pTooltipFlag);
			} catch (NullPointerException | NoSuchElementException ignored) {
			}
			instance.set(DataComponents.ENCHANTMENTS, old);
		} else {
			instance.addToTooltip(pComponent, pContext, pTooltipAdder, pTooltipFlag);
		}
	}
}
