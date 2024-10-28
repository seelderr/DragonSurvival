package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;

public class EnchantmentUtils {
    public static int getLevel(@NotNull final LivingEntity entity, @NotNull final ResourceKey<Enchantment> enchantment) {
        return entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantment).map(reference -> EnchantmentHelper.getEnchantmentLevel(reference, entity)).orElse(0);
    }

    public static int getLevel(@NotNull final Level level, @NotNull final ResourceKey<Enchantment> enchantment, @NotNull final ItemStack stack) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantment).map(stack::getEnchantmentLevel).orElse(0);
    }

    public static Holder.Reference<Enchantment> getHolder(ResourceKey<Enchantment> resourceKey) {
        var lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        return lookup == null ? null : lookup.getOrThrow(resourceKey);
    }

    public static void addEnchantment(ResourceKey<Enchantment> resourceKey, ItemEnchantments.Mutable temp, int level) {
        Holder<Enchantment> holder = EnchantmentUtils.getHolder(resourceKey);
        if (holder != null)
            temp.set(holder, level);
    }
}
