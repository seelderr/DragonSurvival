package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataItemTagProvider extends ItemTagsProvider {
	public DataItemTagProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final CompletableFuture<TagLookup<Block>> blockTags, final String modId, @Nullable final ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, modId, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(createForgeKey("raw_fishes")).add(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
		tag(createForgeKey("raw_meats")).add(Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT);

		tag(createKey("dragon_survival_food")).add(DSItems.SEA_DRAGON_TREAT, DSItems.FOREST_DRAGON_TREAT, DSItems.CAVE_DRAGON_TREAT, DSItems.HOT_DRAGON_ROD, DSItems.EXPLOSIVE_COPPER, DSItems.DOUBLE_QUARTZ, DSItems.QUARTZ_EXPLOSIVE_COPPER, DSItems.CHARRED_MEAT, DSItems.CHARRED_VEGETABLE, DSItems.CHARRED_MUSHROOM, DSItems.CHARRED_SEAFOOD, DSItems.CHARGED_COAL, DSItems.CHARGED_SOUP, DSItems.MEAT_WILD_BERRIES, DSItems.SMELLY_MEAT_PORRIDGE, DSItems.SWEET_SOUR_RABBIT, DSItems.MEAT_CHORUS_MIX, DSItems.DIAMOND_CHORUS, DSItems.LUMINOUS_OINTMENT, DSItems.FROZEN_RAW_FISH, DSItems.SEASONED_FISH, DSItems.GOLDEN_CORAL_PUFFERFISH, DSItems.GOLDEN_TURTLE_EGG);
		tag(createKey("sea_dragon_survival_food")).add(DSItems.SEA_DRAGON_TREAT, DSItems.FROZEN_RAW_FISH, DSItems.SEASONED_FISH, DSItems.GOLDEN_CORAL_PUFFERFISH, DSItems.GOLDEN_TURTLE_EGG);
		tag(createKey("cave_dragon_survival_food")).add(DSItems.CAVE_DRAGON_TREAT, DSItems.HOT_DRAGON_ROD, DSItems.EXPLOSIVE_COPPER, DSItems.DOUBLE_QUARTZ, DSItems.QUARTZ_EXPLOSIVE_COPPER, DSItems.CHARRED_MEAT, DSItems.CHARRED_VEGETABLE, DSItems.CHARRED_MUSHROOM, DSItems.CHARRED_SEAFOOD, DSItems.CHARGED_COAL, DSItems.CHARGED_SOUP);
		tag(createKey("forest_survival_food")).add(DSItems.FOREST_DRAGON_TREAT, DSItems.MEAT_WILD_BERRIES, DSItems.SMELLY_MEAT_PORRIDGE, DSItems.SWEET_SOUR_RABBIT, DSItems.MEAT_CHORUS_MIX, DSItems.DIAMOND_CHORUS);
		tag(createKey("charred_food")).add(DSItems.CHARGED_COAL, DSItems.CHARGED_SOUP, DSItems.CHARRED_MEAT, DSItems.CHARRED_MUSHROOM, DSItems.CHARRED_SEAFOOD, DSItems.CHARRED_VEGETABLE);
		tag(createKey("copper")).addTag(createForgeKey("ingots/copper")).addOptional(new ResourceLocation("cavesandcliffs:raw_copper"));

		this.copy(DataBlockTagProvider.DRAGON_ALTARS, createKey("dragon_altars"));
		this.copy(DataBlockTagProvider.DRAGON_TREASURES, createKey("dragon_treasures"));
		this.copy(DataBlockTagProvider.WOODEN_DRAGON_DOORS, createKey("wooden_dragon_doors"));
	}

	private static TagKey<Item> createKey(@NotNull final String name) {
		return ItemTags.create(new ResourceLocation(DragonSurvivalMod.MODID, name));
	}

	private static TagKey<Item> createForgeKey(String name) {
		return ItemTags.create(new ResourceLocation("forge", name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Item tags";
	}
}