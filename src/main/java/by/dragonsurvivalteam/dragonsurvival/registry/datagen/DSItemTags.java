package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DSItemTags extends ItemTagsProvider {
	public DSItemTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pLookupProvider, pBlockTags, DragonSurvivalMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(createKey("charred_food")).add(DSItems.CHARGED_COAL.value(), DSItems.CHARGED_SOUP.value(), DSItems.CHARRED_MEAT.value(), DSItems.CHARRED_MUSHROOM.value(), DSItems.CHARRED_SEAFOOD.value(), DSItems.CHARRED_VEGETABLE.value());

		this.copy(DataBlockTagProvider.DRAGON_ALTARS, createKey("dragon_altars"));
		this.copy(DataBlockTagProvider.DRAGON_TREASURES, createKey("dragon_treasures"));
		this.copy(DataBlockTagProvider.WOODEN_DRAGON_DOORS, createKey("wooden_dragon_doors"));
	}

	private static TagKey<Item> createKey(@NotNull final String name) {
		return ItemTags.create(DragonSurvivalMod.res(name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Item tags";
	}
}