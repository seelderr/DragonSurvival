package by.dragonsurvivalteam.dragonsurvival.registry;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class DSEnchantments {
    private static ResourceKey<Enchantment> register(String key) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(MODID, key));
    }

    public static final ResourceKey<Enchantment> DRAGONSBANE = register("dragonsbane");
    public static final ResourceKey<Enchantment> DRAGONSBOON = register("dragonsboon");
    public static final ResourceKey<Enchantment> DRAGONSBONK = register("dragonsbonk");
    public static final ResourceKey<Enchantment> SHRINK = register("shrink");
}
