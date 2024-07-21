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

import java.util.Objects;

public class EnchantmentUtils {
    public static int getLevel(@NotNull final Level level, @NotNull final ResourceKey<Enchantment> enchantment, @NotNull final LivingEntity entity) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantment).map(reference -> EnchantmentHelper.getEnchantmentLevel(reference, entity)).orElse(0);
    }

    public static int getLevel(@NotNull final Level level, @NotNull final ResourceKey<Enchantment> enchantment, @NotNull final ItemStack stack) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantment).map(stack::getEnchantmentLevel).orElse(0);
    }

    public static Holder.Reference<Enchantment> getHolder(ResourceKey<Enchantment> resourceKey) {
        return Objects.requireNonNull(CommonHooks.resolveLookup(Registries.ENCHANTMENT)).getOrThrow(resourceKey);
    }

    public static void addEnchantment(ResourceKey<Enchantment> resourceKey, ItemEnchantments.Mutable temp, int enchantmentLevel) {
        var lookup = net.neoforged.neoforge.common.CommonHooks.resolveLookup(net.minecraft.core.registries.Registries.ENCHANTMENT);
        if (lookup != null)
            temp.set(getHolder(resourceKey), enchantmentLevel);
    }
}
