package by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SkeletonPieceBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks.DS_BLOCKS;

public class DSBlockTags extends BlockTagsProvider {
	public static final TagKey<Block> WOODEN_DRAGON_DOORS = key("wooden_dragon_doors");
	public static final TagKey<Block> DRAGON_ALTARS = key("dragon_altars");
	public static final TagKey<Block> DRAGON_TREASURES = key("dragon_treasures");
	public static final TagKey<Block> HUNTER_ABILITY_BLOCKS = key("hunter_ability_blocks");
	public static final TagKey<Block> WOODEN_DRAGON_DOORS_SMALL = key("wooden_dragon_doors_small");

	public DSBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, MODID, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		addToVanillaTags();

		tag(DRAGON_ALTARS).addAll(DS_BLOCKS.getEntries().stream()
						.filter(entry -> entry.value() instanceof DragonAltarBlock)
				        .map(DeferredHolder::getKey).toList());

		tag(DRAGON_TREASURES).addAll(DS_BLOCKS.getEntries().stream()
				.filter(entry -> entry.value() instanceof TreasureBlock)
				.map(DeferredHolder::getKey).toList());

		tag(key("dragon_bones")).addAll(DS_BLOCKS.getEntries().stream()
				.filter(entry -> entry.value() instanceof SkeletonPieceBlock)
				.map(DeferredHolder::getKey).toList());

		tag(WOODEN_DRAGON_DOORS_SMALL)
				.add(DSBlocks.OAK_SMALL_DOOR.value())
				.add(DSBlocks.SPRUCE_SMALL_DOOR.value())
				.add(DSBlocks.ACACIA_SMALL_DOOR.value())
				.add(DSBlocks.BIRCH_SMALL_DOOR.value())
				.add(DSBlocks.JUNGLE_SMALL_DOOR.value())
				.add(DSBlocks.DARK_OAK_SMALL_DOOR.value())
				.add(DSBlocks.WARPED_SMALL_DOOR.value())
				.add(DSBlocks.CRIMSON_SMALL_DOOR.value());

		tag(WOODEN_DRAGON_DOORS)
				.addTag(WOODEN_DRAGON_DOORS_SMALL)
				.add(DSBlocks.DRAGON_ALTAR_OAK_LOG.value())
				.add(DSBlocks.DRAGON_ALTAR_BIRCH_LOG.value())
				.add(DSBlocks.OAK_DRAGON_DOOR.value())
				.add(DSBlocks.SPRUCE_DRAGON_DOOR.value())
				.add(DSBlocks.ACACIA_DRAGON_DOOR.value())
				.add(DSBlocks.BIRCH_DRAGON_DOOR.value())
				.add(DSBlocks.JUNGLE_DRAGON_DOOR.value())
				.add(DSBlocks.DARK_OAK_DRAGON_DOOR.value())
				.add(DSBlocks.WARPED_DRAGON_DOOR.value())
				.add(DSBlocks.CRIMSON_DRAGON_DOOR.value())
				.add(DSBlocks.FOREST_DRAGON_DOOR.value())
				.add(DSBlocks.LEGACY_DRAGON_DOOR.value());

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

		tag(key("castle_blocks"))
				.add(Blocks.STONE_BRICKS)
				.add(Blocks.STONE_BRICK_STAIRS)
				.add(Blocks.STONE_BRICK_SLAB)
				.add(Blocks.STONE_BRICK_WALL)
				.add(Blocks.CRACKED_STONE_BRICKS)
				.add(Blocks.MOSSY_STONE_BRICKS)
				.add(Blocks.CHISELED_STONE_BRICKS)
				.add(Blocks.DEEPSLATE_BRICKS)
				.add(Blocks.DEEPSLATE_BRICK_STAIRS)
				.add(Blocks.DEEPSLATE_BRICK_SLAB)
				.add(Blocks.DEEPSLATE_BRICK_WALL)
				.add(Blocks.DEEPSLATE_TILES)
				.add(Blocks.DEEPSLATE_TILE_STAIRS)
				.add(Blocks.DEEPSLATE_TILE_SLAB)
				.add(Blocks.DEEPSLATE_TILE_WALL)
				.add(Blocks.CRACKED_DEEPSLATE_BRICKS)
				.add(Blocks.CRACKED_DEEPSLATE_TILES)
				.add(Blocks.POLISHED_DEEPSLATE)
				.add(Blocks.POLISHED_DEEPSLATE_STAIRS)
				.add(Blocks.POLISHED_DEEPSLATE_SLAB)
				.add(Blocks.POLISHED_DEEPSLATE_WALL)
				.add(Blocks.CHISELED_DEEPSLATE)
				.add(Blocks.CHISELED_POLISHED_BLACKSTONE)
				.add(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS)
				.add(Blocks.CHAIN)
				.add(Blocks.LANTERN);
	}
	
	private void addToVanillaTags() {
		tag(BlockTags.MINEABLE_WITH_AXE)
				.addTag(WOODEN_DRAGON_DOORS)
				.add(DSBlocks.FOREST_PRESSURE_PLATE.value());

		tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.add(DSBlocks.DRAGON_ALTAR_STONE.value())
				.add(DSBlocks.DRAGON_ALTAR_SANDSTONE.value())
				.add(DSBlocks.DRAGON_ALTAR_RED_SANDSTONE.value())
				.add(DSBlocks.DRAGON_ALTAR_PURPUR_BLOCK.value())
				.add(DSBlocks.DRAGON_ALTAR_NETHER_BRICKS.value())
				.add(DSBlocks.DRAGON_ALTAR_MOSSY_COBBLESTONE.value())
				.add(DSBlocks.DRAGON_ALTAR_BLACKSTONE.value())
				.add(DSBlocks.CAVE_DRAGON_DOOR.value())
				.add(DSBlocks.SEA_DRAGON_DOOR.value())
				.add(DSBlocks.IRON_DRAGON_DOOR.value())
				.add(DSBlocks.STONE_SMALL_DOOR.value())
				.add(DSBlocks.SLEEPER_SMALL_DOOR.value())
				.add(DSBlocks.CAVE_SMALL_DOOR.value())
				.add(DSBlocks.SEA_SMALL_DOOR.value())
				.add(DSBlocks.IRON_SMALL_DOOR.value())
				.add(DSBlocks.MURDERER_SMALL_DOOR.value())
				.add(DSBlocks.MURDERER_DRAGON_DOOR.value())
				.add(DSBlocks.SLEEPER_DRAGON_DOOR.value())
				.add(DSBlocks.STONE_DRAGON_DOOR.value())
				.add(DSBlocks.CAVE_SOURCE_OF_MAGIC.value())
				.add(DSBlocks.FOREST_SOURCE_OF_MAGIC.value())
				.add(DSBlocks.SEA_SOURCE_OF_MAGIC.value())
				.add(DSBlocks.TREASURE_DEBRIS.value())
				.add(DSBlocks.TREASURE_DIAMOND.value())
				.add(DSBlocks.TREASURE_EMERALD.value())
				.add(DSBlocks.TREASURE_COPPER.value())
				.add(DSBlocks.TREASURE_GOLD.value())
				.add(DSBlocks.TREASURE_IRON.value())
				.add(DSBlocks.HELMET_BLOCK_1.value())
				.add(DSBlocks.HELMET_BLOCK_2.value())
				.add(DSBlocks.HELMET_BLOCK_3.value())
				.add(DSBlocks.DRAGON_BEACON.value())
				.add(DSBlocks.DRAGON_MEMORY_BLOCK.value())
				.add(DSBlocks.PEACE_DRAGON_BEACON.value())
				.add(DSBlocks.MAGIC_DRAGON_BEACON.value())
				.add(DSBlocks.FIRE_DRAGON_BEACON.value())
				.add(DSBlocks.DRAGON_PRESSURE_PLATE.value())
				.add(DSBlocks.HUMAN_PRESSURE_PLATE.value())
				.add(DSBlocks.SEA_PRESSURE_PLATE.value())
				.add(DSBlocks.CAVE_PRESSURE_PLATE.value());
		
		tag(BlockTags.NEEDS_STONE_TOOL)
				.add(DSBlocks.TREASURE_GOLD.value())
				.add(DSBlocks.TREASURE_EMERALD.value())
				.add(DSBlocks.TREASURE_DIAMOND.value())
				.add(DSBlocks.TREASURE_DEBRIS.value())
				.add(DSBlocks.IRON_DRAGON_DOOR.value())
				.add(DSBlocks.IRON_SMALL_DOOR.value())
				.add(DSBlocks.MURDERER_DRAGON_DOOR.value())
				.add(DSBlocks.MURDERER_SMALL_DOOR.value())
				.add(DSBlocks.TREASURE_COPPER.value())
				.add(DSBlocks.TREASURE_IRON.value())
				.add(DSBlocks.HELMET_BLOCK_1.value())
				.add(DSBlocks.HELMET_BLOCK_2.value())
				.add(DSBlocks.HELMET_BLOCK_3.value());
		
		tag(BlockTags.NEEDS_IRON_TOOL)
				.add(DSBlocks.DRAGON_BEACON.value())
				.add(DSBlocks.DRAGON_MEMORY_BLOCK.value())
				.add(DSBlocks.PEACE_DRAGON_BEACON.value())
				.add(DSBlocks.MAGIC_DRAGON_BEACON.value())
				.add(DSBlocks.FIRE_DRAGON_BEACON.value());
	}

	private static TagKey<Block> key(@NotNull final String name) {
		return BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Block tags";
	}
}