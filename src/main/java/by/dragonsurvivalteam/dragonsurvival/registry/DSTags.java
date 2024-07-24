package by.dragonsurvivalteam.dragonsurvival.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DSTags {
    public static final TagKey<Item> VAULT_KEYS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(MODID, "vault_keys"));
}
