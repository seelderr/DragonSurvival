package by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags;

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
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DSItemTags extends ItemTagsProvider {
	public static final TagKey<Item> KEEP_EFFECTS = createKey("keep_effects");

	public DSItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper helper) {
		super(output, provider, blockTags, DragonSurvivalMod.MODID, helper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(createKey("charred_food")).add(DSItems.CHARGED_COAL.value(), DSItems.CHARGED_SOUP.value(), DSItems.CHARRED_MEAT.value(), DSItems.CHARRED_MUSHROOM.value(), DSItems.CHARRED_SEAFOOD.value(), DSItems.CHARRED_VEGETABLE.value());

		tag(KEEP_EFFECTS)
				.addOptional(ResourceLocation.fromNamespaceAndPath("gothic", "elixir_of_speed"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("gothic", "elixir_of_health"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("gothic", "elixir_of_mental_cleansing"));

		copy(DSBlockTags.DRAGON_ALTARS, createKey("dragon_altars"));
		copy(DSBlockTags.DRAGON_TREASURES, createKey("dragon_treasures"));
		copy(DSBlockTags.WOODEN_DRAGON_DOORS, createKey("wooden_dragon_doors"));
	}

	private static TagKey<Item> createKey(@NotNull final String name) {
		return ItemTags.create(DragonSurvivalMod.res(name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Item tags";
	}
}