package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks.DS_BLOCKS;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataBlockTagProvider extends BlockTagsProvider {
	public static final TagKey<Block> WOODEN_DRAGON_DOORS = createKey("wooden_dragon_doors");
	public static final TagKey<Block> DRAGON_ALTARS = createKey("dragon_altars");
	public static final TagKey<Block> DRAGON_TREASURES = createKey("dragon_treasures");
	public static final TagKey<Block> HUNTER_ABILITY_BLOCKS = createKey("hunter_ability_blocks");

	public DataBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(DRAGON_ALTARS).addAll(DS_BLOCKS.getEntries().stream()
						.filter(entry -> entry.getDelegate().value() instanceof DragonAltarBlock)
				        .map(entry -> BuiltInRegistries.BLOCK.getResourceKey(entry.getDelegate().value()).get()).toList());

		tag(DRAGON_TREASURES).addAll(DS_BLOCKS.getEntries().stream()
				.filter(entry -> entry.getDelegate().value() instanceof TreasureBlock)
				.map(entry -> BuiltInRegistries.BLOCK.getResourceKey(entry.getDelegate().value()).get()).toList());


		TagKey<Block> woodenDragonDoorsSmall = createKey("wooden_dragon_doors_small");

		tag(woodenDragonDoorsSmall)
				.add(DSBlocks.OAK_SMALL_DOOR.get())
				.add(DSBlocks.SPRUCE_SMALL_DOOR.get())
				.add(DSBlocks.ACACIA_SMALL_DOOR.get())
				.add(DSBlocks.BIRCH_SMALL_DOOR.get())
				.add(DSBlocks.JUNGLE_SMALL_DOOR.get())
				.add(DSBlocks.DARK_OAK_SMALL_DOOR.get())
				.add(DSBlocks.WARPED_SMALL_DOOR.get())
				.add(DSBlocks.CRIMSON_SMALL_DOOR.get());

		tag(WOODEN_DRAGON_DOORS)
				.add(DSBlocks.DRAGON_ALTAR_OAK_LOG.get())
				.add(DSBlocks.DRAGON_ALTAR_BIRCH_LOG.get())
				.add(DSBlocks.OAK_DRAGON_DOOR.get())
				.add(DSBlocks.SPRUCE_DRAGON_DOOR.get())
				.add(DSBlocks.ACACIA_DRAGON_DOOR.get())
				.add(DSBlocks.BIRCH_DRAGON_DOOR.get())
				.add(DSBlocks.JUNGLE_DRAGON_DOOR.get())
				.add(DSBlocks.DARK_OAK_DRAGON_DOOR.get())
				.add(DSBlocks.WARPED_DRAGON_DOOR.get())
				.add(DSBlocks.CRIMSON_DRAGON_DOOR.get())
				.add(DSBlocks.FOREST_DRAGON_DOOR.get())
				.add(DSBlocks.LEGACY_DRAGON_DOOR.get())
				.addOptionalTag(woodenDragonDoorsSmall.location()); // TODO :: Has problems finding the tag?

		tag(BlockTags.MINEABLE_WITH_AXE)
				.addTag(WOODEN_DRAGON_DOORS)
				.add(DSBlocks.FOREST_PRESSURE_PLATE.get());

		tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.add(DSBlocks.DRAGON_ALTAR_STONE.get())
				.add(DSBlocks.DRAGON_ALTAR_SANDSTONE.get())
				.add(DSBlocks.DRAGON_ALTAR_RED_SANDSTONE.get())
				.add(DSBlocks.DRAGON_ALTAR_PURPUR_BLOCK.get())
				.add(DSBlocks.DRAGON_ALTAR_NETHER_BRICKS.get())
				.add(DSBlocks.DRAGON_ALTAR_MOSSY_COBBLESTONE.get())
				.add(DSBlocks.DRAGON_ALTAR_BLACKSTONE.get())
				.add(DSBlocks.CAVE_DRAGON_DOOR.get())
				.add(DSBlocks.SEA_DRAGON_DOOR.get())
				.add(DSBlocks.IRON_DRAGON_DOOR.get())
				.add(DSBlocks.STONE_SMALL_DOOR.get())
				.add(DSBlocks.SLEEPER_SMALL_DOOR.get())
				.add(DSBlocks.CAVE_SMALL_DOOR.get())
				.add(DSBlocks.SEA_SMALL_DOOR.get())
				.add(DSBlocks.IRON_SMALL_DOOR.get())
				.add(DSBlocks.MURDERER_SMALL_DOOR.get())
				.add(DSBlocks.MURDERER_DRAGON_DOOR.get())
				.add(DSBlocks.SLEEPER_DRAGON_DOOR.get())
				.add(DSBlocks.STONE_DRAGON_DOOR.get())
				.add(DSBlocks.CAVE_SOURCE_OF_MAGIC.get())
				.add(DSBlocks.FOREST_SOURCE_OF_MAGIC.get())
				.add(DSBlocks.SEA_SOURCE_OF_MAGIC.get())
				.add(DSBlocks.TREASURE_DEBRIS.get())
				.add(DSBlocks.TREASURE_DIAMOND.get())
				.add(DSBlocks.TREASURE_EMERALD.get())
				.add(DSBlocks.TREASURE_COPPER.get())
				.add(DSBlocks.TREASURE_GOLD.get())
				.add(DSBlocks.TREASURE_IRON.get())
				.add(DSBlocks.HELMET_BLOCK_1.get())
				.add(DSBlocks.HELMET_BLOCK_2.get())
				.add(DSBlocks.HELMET_BLOCK_3.get())
				.add(DSBlocks.DRAGON_BEACON.get())
				.add(DSBlocks.DRAGON_MEMORY_BLOCK.get())
				.add(DSBlocks.PEACE_DRAGON_BEACON.get())
				.add(DSBlocks.MAGIC_DRAGON_BEACON.get())
				.add(DSBlocks.FIRE_DRAGON_BEACON.get())
				.add(DSBlocks.DRAGON_PRESSURE_PLATE.get())
				.add(DSBlocks.HUMAN_PRESSURE_PLATE.get())
				.add(DSBlocks.SEA_PRESSURE_PLATE.get())
				.add(DSBlocks.CAVE_PRESSURE_PLATE.get());

		tag(BlockTags.NEEDS_STONE_TOOL)
				.add(DSBlocks.TREASURE_GOLD.get())
				.add(DSBlocks.TREASURE_EMERALD.get())
				.add(DSBlocks.TREASURE_DIAMOND.get())
				.add(DSBlocks.TREASURE_DEBRIS.get())
				.add(DSBlocks.IRON_DRAGON_DOOR.get())
				.add(DSBlocks.IRON_SMALL_DOOR.get())
				.add(DSBlocks.MURDERER_DRAGON_DOOR.get())
				.add(DSBlocks.MURDERER_SMALL_DOOR.get())
				.add(DSBlocks.TREASURE_COPPER.get())
				.add(DSBlocks.TREASURE_IRON.get())
				.add(DSBlocks.HELMET_BLOCK_1.get())
				.add(DSBlocks.HELMET_BLOCK_2.get())
				.add(DSBlocks.HELMET_BLOCK_3.get());

		tag(BlockTags.NEEDS_IRON_TOOL)
				.add(DSBlocks.DRAGON_BEACON.get())
				.add(DSBlocks.DRAGON_MEMORY_BLOCK.get())
				.add(DSBlocks.PEACE_DRAGON_BEACON.get())
				.add(DSBlocks.MAGIC_DRAGON_BEACON.get())
				.add(DSBlocks.FIRE_DRAGON_BEACON.get());

		tag(HUNTER_ABILITY_BLOCKS)
				.addTag(BlockTags.FLOWERS)
				.addTag(BlockTags.SAPLINGS)
				.add(Blocks.WARPED_NYLIUM)
				.add(Blocks.CRIMSON_NYLIUM)
				.add(Blocks.GRASS_BLOCK)
				.add(Blocks.FERN)
				.add(Blocks.LARGE_FERN)
				.add(Blocks.DEAD_BUSH)
				.add(Blocks.SWEET_BERRY_BUSH)
				.add(Blocks.TALL_GRASS)
				.add(Blocks.GLOW_LICHEN)
				.add(Blocks.CRIMSON_ROOTS)
				.add(Blocks.WARPED_ROOTS)
				.add(Blocks.NETHER_SPROUTS)
				.add(Blocks.BIG_DRIPLEAF)
				.add(Blocks.SMALL_DRIPLEAF);
	}

	private static TagKey<Block> createKey(@NotNull final String name) {
		return BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Block tags";
	}
}