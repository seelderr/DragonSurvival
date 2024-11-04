package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.mixins.Holder$ReferenceAccess;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class DSRecipes extends RecipeProvider {
    public DSRecipes(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull final RecipeOutput output, @NotNull final HolderLookup.Provider lookup) {
        MappedRegistry<Item> registry = (MappedRegistry<Item>) BuiltInRegistries.ITEM.freeze();
        //noinspection deprecation -> workaround to create recipes with items from other mods (without having to resort to one-entry-tags)
        registry.unfreeze();

        buildShaped(output, lookup);
        buildShapeless(output, lookup);

        // We don't re-freeze the registry because it would complain about unregistered holders
        // Since the data generation is over at this point it doesn't matter anyway
    }

    private void buildShaped(final RecipeOutput output, final HolderLookup.Provider lookup) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DSItems.ELDER_DRAGON_HEART.value())
                .pattern("DGD")
                .pattern("GHG")
                .pattern("NGN")
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('H', DSItems.WEAK_DRAGON_HEART.value())
                .define('N', Items.NETHERITE_SCRAP)
                .unlockedBy(getHasName(DSItems.WEAK_DRAGON_HEART.value()), has(DSItems.WEAK_DRAGON_HEART.value()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, DSItems.CAVE_DRAGON_TREAT.value())
                .pattern("DDD")
                .pattern("DCD")
                .pattern("DDD")
                .define('D', DSItems.ELDER_DRAGON_DUST.value())
                .define('C', DSItemTags.CHARRED_FOOD)
                .unlockedBy(getHasName(DSItems.ELDER_DRAGON_DUST.value()), has(DSItems.ELDER_DRAGON_DUST.value()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, DSItems.CHARGED_COAL.value())
                .pattern("RRR")
                .pattern("CCR")
                .pattern("CCR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('C', ItemTags.COALS)
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .save(output, DragonSurvival.res("charged_coal_from_dust"));

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, DSItems.CHARGED_COAL.value())
                .pattern("RRR")
                .pattern("CCR")
                .pattern("CCR")
                .define('R', new ProxyItem("regions_unexplored", "redstone_bulb"))
                .define('C', ItemTags.COALS)
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .save(output.withConditions(new ModLoadedCondition("regions_unexplored")), DragonSurvival.res("charged_coal_from_bulb"));
    }

    private void buildShapeless(final RecipeOutput output, final HolderLookup.Provider lookup) {
        // --- Misc --- //

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, Items.CHARCOAL)
                .requires(DSItemTags.CHARRED_FOOD)
                .unlockedBy("has_charred_food", has(DSItemTags.CHARRED_FOOD))
                .save(output, DragonSurvival.res("charcoal_from_charred_food"));

        // --- Dragon doors --- //

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.BUILDING_BLOCKS, DSBlocks.ACACIA_DRAGON_DOOR.value(), 2)
                .requires(Items.ACACIA_DOOR, 3)
                .unlockedBy(getHasName(Items.ACACIA_DOOR), has(Items.ACACIA_DOOR))
                .save(output);

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.BUILDING_BLOCKS, DSBlocks.BIRCH_DRAGON_DOOR.value(), 2)
                .requires(Items.BIRCH_DOOR, 3)
                .unlockedBy(getHasName(Items.BIRCH_DOOR), has(Items.BIRCH_DOOR))
                .save(output);

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.BUILDING_BLOCKS, DSBlocks.CAVE_DRAGON_DOOR.value())
                .requires(DSItemTags.WOODEN_DRAGON_DOORS)
                .requires(Items.GILDED_BLACKSTONE)
                .unlockedBy("has_wooden_dragon_doors", has(DSItemTags.WOODEN_DRAGON_DOORS))
                .save(output);

        // --- Small dragon doors --- //

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.BUILDING_BLOCKS, DSBlocks.SMALL_ACACIA_DRAGON_DOOR.value(), 3)
                .requires(DSBlocks.ACACIA_DRAGON_DOOR.value())
                .unlockedBy(getHasName(DSBlocks.ACACIA_DRAGON_DOOR.value()), has(DSBlocks.ACACIA_DRAGON_DOOR.value()))
                .save(output);

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.BUILDING_BLOCKS, DSBlocks.SMALL_BIRCH_DRAGON_DOOR.value(), 3)
                .requires(DSBlocks.BIRCH_DRAGON_DOOR.value())
                .unlockedBy(getHasName(DSBlocks.BIRCH_DRAGON_DOOR.value()), has(DSBlocks.BIRCH_DRAGON_DOOR.value()))
                .save(output);

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.BUILDING_BLOCKS, DSBlocks.SMALL_CAVE_DRAGON_DOOR.value(), 3)
                .requires(DSBlocks.CAVE_DRAGON_DOOR.value())
                .unlockedBy(getHasName(DSBlocks.CAVE_DRAGON_DOOR.value()), has(DSBlocks.CAVE_DRAGON_DOOR.value()))
                .save(output);

        // --- Pressure Plates --- //

        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.BUILDING_BLOCKS, DSBlocks.CAVE_DRAGON_PRESSURE_PLATE.value())
                .requires(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE)
                .requires(DSItems.ELDER_DRAGON_DUST.value())
                .unlockedBy(getHasName(DSItems.ELDER_DRAGON_DUST.value()), has(DSItems.ELDER_DRAGON_DUST.value()))
                .save(output);
    }

    @SuppressWarnings("deprecation") // ignore
    public record ProxyItem(String namespace, String path) implements ItemLike {
        @Override
        public @NotNull Item asItem() {
            Item item = new Item(new Item.Properties());
            ((Holder$ReferenceAccess) item.builtInRegistryHolder()).dragonSurvival$bindKey(ResourceKey.create(Registries.ITEM, DragonSurvival.location(namespace, path)));
            ((Holder$ReferenceAccess) item.builtInRegistryHolder()).dragonSurvival$bindValue(item);
            return item;
        }
    }
}
