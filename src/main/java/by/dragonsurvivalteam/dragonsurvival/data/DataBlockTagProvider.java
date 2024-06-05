package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DataBlockTagProvider extends TagsProvider<Block> {
	public static final TagKey<Block> WOODEN_DRAGON_DOORS = createKey("wooden_dragon_doors");
	public static final TagKey<Block> DRAGON_ALTARS = createKey("dragon_altars");
	public static final TagKey<Block> DRAGON_TREASURES = createKey("dragon_treasures");
	public static final TagKey<Block> HUNTER_ABILITY_BLOCKS = createKey("hunter_ability_blocks");

	protected DataBlockTagProvider(PackOutput pOutput, ResourceKey<? extends Registry<Block>> pRegistryKey, CompletableFuture<HolderLookup.Provider> pLookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pRegistryKey, pLookupProvider, modId, existingFileHelper);
	}


	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(DRAGON_ALTARS).add(DSBlocks.DS_BLOCKS.values().stream()
				.filter(block -> block instanceof DragonAltarBlock)
				.toList()
				.toArray(new Block[0]));

		tag(DRAGON_TREASURES).add(DSBlocks.DS_BLOCKS.values().stream()
				.filter(block -> block instanceof TreasureBlock)
				.toList()
				.toArray(new Block[0]));

		TagKey<Block> woodenDragonDoorsSmall = createKey("wooden_dragon_doors_small");

		tag(woodenDragonDoorsSmall)
				.add(DSBlocks.OAK_SMALL_DOOR)
				.add(DSBlocks.SPRUCE_SMALL_DOOR)
				.add(DSBlocks.ACACIA_SMALL_DOOR)
				.add(DSBlocks.BIRCH_SMALL_DOOR)
				.add(DSBlocks.JUNGLE_SMALL_DOOR)
				.add(DSBlocks.DARK_OAK_SMALL_DOOR)
				.add(DSBlocks.WARPED_SMALL_DOOR)
				.add(DSBlocks.CRIMSON_SMALL_DOOR);

		tag(WOODEN_DRAGON_DOORS)
				.add(DSBlocks.DRAGON_ALTAR_OAK_LOG)
				.add(DSBlocks.DRAGON_ALTAR_BIRCH_LOG)
				.add(DSBlocks.OAK_DRAGON_DOOR)
				.add(DSBlocks.SPRUCE_DRAGON_DOOR)
				.add(DSBlocks.ACACIA_DRAGON_DOOR)
				.add(DSBlocks.BIRCH_DRAGON_DOOR)
				.add(DSBlocks.JUNGLE_DRAGON_DOOR)
				.add(DSBlocks.DARK_OAK_DRAGON_DOOR)
				.add(DSBlocks.WARPED_DRAGON_DOOR)
				.add(DSBlocks.CRIMSON_DRAGON_DOOR)
				.add(DSBlocks.FOREST_DRAGON_DOOR)
				.add(DSBlocks.LEGACY_DRAGON_DOOR)
				.addOptionalTag(woodenDragonDoorsSmall.location()); // FIXME :: Has problems finding the tag?

		tag(BlockTags.MINEABLE_WITH_AXE)
				.addTag(WOODEN_DRAGON_DOORS)
				.add(DSBlocks.FOREST_PRESSURE_PLATE);

		tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.add(DSBlocks.DRAGON_ALTAR_STONE)
				.add(DSBlocks.DRAGON_ALTAR_SANDSTONE)
				.add(DSBlocks.DRAGON_ALTAR_RED_SANDSTONE)
				.add(DSBlocks.DRAGON_ALTAR_PURPUR_BLOCK)
				.add(DSBlocks.DRAGON_ALTAR_NETHER_BRICKS)
				.add(DSBlocks.DRAGON_ALTAR_MOSSY_COBBLESTONE)
				.add(DSBlocks.DRAGON_ALTAR_BLACKSTONE)
				.add(DSBlocks.CAVE_DRAGON_DOOR)
				.add(DSBlocks.SEA_DRAGON_DOOR)
				.add(DSBlocks.IRON_DRAGON_DOOR)
				.add(DSBlocks.STONE_SMALL_DOOR)
				.add(DSBlocks.SLEEPER_SMALL_DOOR)
				.add(DSBlocks.CAVE_SMALL_DOOR)
				.add(DSBlocks.SEA_SMALL_DOOR)
				.add(DSBlocks.IRON_SMALL_DOOR)
				.add(DSBlocks.MURDERER_SMALL_DOOR)
				.add(DSBlocks.MURDERER_DRAGON_DOOR)
				.add(DSBlocks.SLEEPER_DRAGON_DOOR)
				.add(DSBlocks.STONE_DRAGON_DOOR)
				.add(DSBlocks.CAVE_SOURCE_OF_MAGIC)
				.add(DSBlocks.FOREST_SOURCE_OF_MAGIC)
				.add(DSBlocks.SEA_SOURCE_OF_MAGIC)
				.add(DSBlocks.TREASURE_DEBRIS)
				.add(DSBlocks.TREASURE_DIAMOND)
				.add(DSBlocks.TREASURE_EMERALD)
				.add(DSBlocks.TREASURE_COPPER)
				.add(DSBlocks.TREASURE_GOLD)
				.add(DSBlocks.TREASURE_IRON)
				.add(DSBlocks.HELMET_BLOCK_1)
				.add(DSBlocks.HELMET_BLOCK_2)
				.add(DSBlocks.HELMET_BLOCK_3)
				.add(DSBlocks.DRAGON_BEACON)
				.add(DSBlocks.DRAGON_MEMORY_BLOCK)
				.add(DSBlocks.PEACE_DRAGON_BEACON)
				.add(DSBlocks.MAGIC_DRAGON_BEACON)
				.add(DSBlocks.FIRE_DRAGON_BEACON)
				.add(DSBlocks.DRAGON_PRESSURE_PLATE)
				.add(DSBlocks.HUMAN_PRESSURE_PLATE)
				.add(DSBlocks.SEA_PRESSURE_PLATE)
				.add(DSBlocks.CAVE_PRESSURE_PLATE);

		tag(BlockTags.NEEDS_STONE_TOOL)
				.add(DSBlocks.TREASURE_GOLD)
				.add(DSBlocks.TREASURE_EMERALD)
				.add(DSBlocks.TREASURE_DIAMOND)
				.add(DSBlocks.TREASURE_DEBRIS)
				.add(DSBlocks.IRON_DRAGON_DOOR)
				.add(DSBlocks.IRON_SMALL_DOOR)
				.add(DSBlocks.MURDERER_DRAGON_DOOR)
				.add(DSBlocks.MURDERER_SMALL_DOOR)
				.add(DSBlocks.TREASURE_COPPER)
				.add(DSBlocks.TREASURE_IRON)
				.add(DSBlocks.HELMET_BLOCK_1)
				.add(DSBlocks.HELMET_BLOCK_2)
				.add(DSBlocks.HELMET_BLOCK_3);

		tag(BlockTags.NEEDS_IRON_TOOL)
				.add(DSBlocks.DRAGON_BEACON)
				.add(DSBlocks.DRAGON_MEMORY_BLOCK)
				.add(DSBlocks.PEACE_DRAGON_BEACON)
				.add(DSBlocks.MAGIC_DRAGON_BEACON)
				.add(DSBlocks.FIRE_DRAGON_BEACON);

		tag(HUNTER_ABILITY_BLOCKS)
				.addTag(BlockTags.FLOWERS)
				.addTag(BlockTags.SAPLINGS)
				.add(Blocks.WARPED_NYLIUM)
				.add(Blocks.CRIMSON_NYLIUM)
				.add(Blocks.GRASS)
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
		return BlockTags.create(new ResourceLocation(DragonSurvivalMod.MODID, name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Block tags";
	}
}