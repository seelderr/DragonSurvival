package by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DSItemTags extends ItemTagsProvider {
    public static final TagKey<Item> KEEP_EFFECTS = key("keep_effects");
    public static final TagKey<Item> SEA_ADDITIONAL_WATER_USABLES = key("sea_additional_water_usables");

    public DSItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper helper) {
        super(output, provider, blockTags, DragonSurvivalMod.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        addToVanillaTags();

        // Effects from these items are kept even if they're not the correct food for the dragon
        tag(KEEP_EFFECTS)
                .addOptional(ResourceLocation.fromNamespaceAndPath("gothic", "elixir_of_speed"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("gothic", "elixir_of_health"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("gothic", "elixir_of_mental_cleansing"));

        // Additional usable modded items which restore water when used
        tag(SEA_ADDITIONAL_WATER_USABLES)
                .addOptional(DragonSurvivalMod.location("immersive_weathering", "icicle"));

        // Used in recipes
        tag(key("charred_food"))
                .add(DSItems.CHARGED_COAL.value())
                .add(DSItems.CHARGED_SOUP.value())
                .add(DSItems.CHARRED_MEAT.value())
                .add(DSItems.CHARRED_MUSHROOM.value())
                .add(DSItems.CHARRED_SEAFOOD.value())
                .add(DSItems.CHARRED_VEGETABLE.value());

        // Used in recipes
        tag(key("cold_items"))
                .add(Items.SNOWBALL)
                .add(Items.ICE)
                .add(Items.PACKED_ICE)
                .add(Items.SNOW)
                .add(Items.SNOW_BLOCK)
                .add(Items.POWDER_SNOW_BUCKET)
                .addOptional(ResourceLocation.fromNamespaceAndPath("immersive_weathering", "icicle"));

        // Used in enchantments
        tag(key("enchantable/chest_armor_and_elytra"))
                .addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(Items.ELYTRA);

        copy(DSBlockTags.DRAGON_ALTARS, key("dragon_altars"));
        copy(DSBlockTags.DRAGON_TREASURES, key("dragon_treasures"));
        copy(DSBlockTags.WOODEN_DRAGON_DOORS_SMALL, key("wooden_dragon_doors_small"));
        copy(DSBlockTags.WOODEN_DRAGON_DOORS, key("wooden_dragon_doors"));
    }

    private void addToVanillaTags() {
        DSItems.DS_ITEMS.getEntries().forEach(holder -> {
            Item item = holder.value();

            if (item instanceof ArmorItem armor) {
                switch (armor.getEquipmentSlot()) {
                    case HEAD -> tag(ItemTags.HEAD_ARMOR).add(item);
                    case CHEST -> tag(ItemTags.CHEST_ARMOR).add(item);
                    case FEET -> tag(ItemTags.FOOT_ARMOR).add(item);
                    case LEGS -> tag(ItemTags.LEG_ARMOR).add(item);
                }
            } else if (item instanceof SwordItem) {
                tag(ItemTags.SWORDS).add(item);
            }
        });
    }

    private static TagKey<Item> key(@NotNull final String name) {
        return ItemTags.create(DragonSurvivalMod.res(name));
    }

    @Override
    public @NotNull String getName() {
        return "Dragon Survival Item tags";
    }
}