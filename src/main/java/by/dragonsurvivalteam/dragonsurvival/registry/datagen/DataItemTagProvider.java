package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

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
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DataItemTagProvider extends ItemTagsProvider {


	public DataItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pLookupProvider, pBlockTags, modId, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(createForgeKey("raw_fishes")).add(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
		tag(createForgeKey("raw_meats")).add(Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT);

		tag(createKey("dragon_survival_food")).add(DSItems.SEA_DRAGON_TREAT.value(), DSItems.FOREST_DRAGON_TREAT.value(), DSItems.CAVE_DRAGON_TREAT.value(), DSItems.HOT_DRAGON_ROD.value(), DSItems.EXPLOSIVE_COPPER.value(), DSItems.DOUBLE_QUARTZ.value(), DSItems.QUARTZ_EXPLOSIVE_COPPER.value(), DSItems.CHARRED_MEAT.value(), DSItems.CHARRED_VEGETABLE.value(), DSItems.CHARRED_MUSHROOM.value(), DSItems.CHARRED_SEAFOOD.value(), DSItems.CHARGED_COAL.value(), DSItems.CHARGED_SOUP.value(), DSItems.MEAT_WILD_BERRIES.value(), DSItems.SMELLY_MEAT_PORRIDGE.value(), DSItems.SWEET_SOUR_RABBIT.value(), DSItems.MEAT_CHORUS_MIX.value(), DSItems.DIAMOND_CHORUS.value(), DSItems.LUMINOUS_OINTMENT.value(), DSItems.FROZEN_RAW_FISH.value(), DSItems.SEASONED_FISH.value(), DSItems.GOLDEN_CORAL_PUFFERFISH.value(), DSItems.GOLDEN_TURTLE_EGG.value());
		tag(createKey("sea_dragon_survival_food")).add(DSItems.SEA_DRAGON_TREAT.value(), DSItems.FROZEN_RAW_FISH.value(), DSItems.SEASONED_FISH.value(), DSItems.GOLDEN_CORAL_PUFFERFISH.value(), DSItems.GOLDEN_TURTLE_EGG.value());
		tag(createKey("cave_dragon_survival_food")).add(DSItems.CAVE_DRAGON_TREAT.value(), DSItems.HOT_DRAGON_ROD.value(), DSItems.EXPLOSIVE_COPPER.value(), DSItems.DOUBLE_QUARTZ.value(), DSItems.QUARTZ_EXPLOSIVE_COPPER.value(), DSItems.CHARRED_MEAT.value(), DSItems.CHARRED_VEGETABLE.value(), DSItems.CHARRED_MUSHROOM.value(), DSItems.CHARRED_SEAFOOD.value(), DSItems.CHARGED_COAL.value(), DSItems.CHARGED_SOUP.value());
		tag(createKey("forest_survival_food")).add(DSItems.FOREST_DRAGON_TREAT.value(), DSItems.MEAT_WILD_BERRIES.value(), DSItems.SMELLY_MEAT_PORRIDGE.value(), DSItems.SWEET_SOUR_RABBIT.value(), DSItems.MEAT_CHORUS_MIX.value(), DSItems.DIAMOND_CHORUS.value());
		tag(createKey("charred_food")).add(DSItems.CHARGED_COAL.value(), DSItems.CHARGED_SOUP.value(), DSItems.CHARRED_MEAT.value(), DSItems.CHARRED_MUSHROOM.value(), DSItems.CHARRED_SEAFOOD.value(), DSItems.CHARRED_VEGETABLE.value());
		tag(createKey("copper")).addTag(createForgeKey("ingots/copper")).addOptional(ResourceLocation.parse("cavesandcliffs:raw_copper"));

		this.copy(DataBlockTagProvider.DRAGON_ALTARS, createKey("dragon_altars"));
		this.copy(DataBlockTagProvider.DRAGON_TREASURES, createKey("dragon_treasures"));
		this.copy(DataBlockTagProvider.WOODEN_DRAGON_DOORS, createKey("wooden_dragon_doors"));
	}

	private static TagKey<Item> createKey(@NotNull final String name) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
	}

	private static TagKey<Item> createForgeKey(String name) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Item tags";
	}
}