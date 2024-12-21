package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.List;

public record ItemBasedUpgrade(List<HolderSet<Item>> itemsPerLevel, HolderSet<Item> downgradeItems) {
    public static final Codec<ItemBasedUpgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.ITEM).listOf().fieldOf("items_per_level").forGetter(ItemBasedUpgrade::itemsPerLevel),
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("downgrade_items").forGetter(ItemBasedUpgrade::downgradeItems)
    ).apply(instance, ItemBasedUpgrade::new));

    public MutableComponent getDescription(int level) {
        return Component.empty();
    }

    public boolean isDowngradeItem(Holder<Item> item) {
        return downgradeItems.contains(item);
    }

    public boolean isUpgradeItemForLevel(Holder<Item> item, int level) {
        return itemsPerLevel.get(level - 1).contains(item);
    }
}
