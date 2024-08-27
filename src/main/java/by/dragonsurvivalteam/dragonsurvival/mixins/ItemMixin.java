package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import java.util.List;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin
{
    @Unique
    private static List<ResourceKey<Enchantment>> dragonSurvival$enchantmentsToNotDisplayDescription = List.of(
            DSEnchantments.SHRINK,
            DSEnchantments.DRAGONSBONK,
            DSEnchantments.DRAGONSBOON
    );

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void appendEnchantmentDescriptionToDSEnchantments(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag, CallbackInfo ci)
    {
        if (pStack.getItem() instanceof EnchantedBookItem enchantedBookItem)
        {
            ItemEnchantments enchantments = pStack.get(DataComponents.STORED_ENCHANTMENTS);
            if(enchantments != null) {
                // Do not write the tooltip unless we have only one enchantment so that we don't cause confusion by writing the enchantment description on the tooltip of a book with multiple enchantments
                if(enchantments.size() == 1) {
                    enchantments.keySet().forEach(enchantment -> {
                        if(dragonSurvival$enchantmentsToNotDisplayDescription.stream().noneMatch(enchantment::is)) {
                            if(pContext.registries().lookup(Registries.ENCHANTMENT).get().getOrThrow(enchantment.getKey()).getKey().location().getNamespace().equals(DragonSurvivalMod.MODID)) {
                                // Remove the "dragonsurvival:" prefix and add the "ds.description." prefix to the registered name
                                pTooltipComponents.add(Component.translatable("ds.description." + enchantment.getRegisteredName().substring(DragonSurvivalMod.MODID.length() + 1)));
                            }
                        }
                    });
                }
            }
        }
    }
}
