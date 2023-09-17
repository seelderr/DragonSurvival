package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonAltarBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DataBlockTagProvider extends BlockTagsProvider {
	public static final TagKey<Block> WOODEN_DRAGON_DOORS = createKey("wooden_dragon_doors");
	public static final TagKey<Block> DRAGON_ALTARS = createKey("dragon_altars");
	public static final TagKey<Block> DRAGON_TREASURES = createKey("dragon_treasures");

	public DataBlockTagProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final String modId, @Nullable final ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, modId, existingFileHelper);
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

		tag(WOODEN_DRAGON_DOORS)
				.add(DSBlocks.dragon_altar_oak_log)
				.add(DSBlocks.dragon_altar_birch_log)
				.add(DSBlocks.oakDoor)
				.add(DSBlocks.spruceDoor)
				.add(DSBlocks.acaciaDoor)
				.add(DSBlocks.birchDoor)
				.add(DSBlocks.jungleDoor)
				.add(DSBlocks.darkOakDoor)
				.add(DSBlocks.warpedDoor)
				.add(DSBlocks.crimsonDoor)
				.add(DSBlocks.forestDoor)
				.add(DSBlocks.oakSmallDoor)
				.add(DSBlocks.spruceSmallDoor)
				.add(DSBlocks.acaciaSmallDoor)
				.add(DSBlocks.birchSmallDoor)
				.add(DSBlocks.jungleSmallDoor)
				.add(DSBlocks.darkOakSmallDoor)
				.add(DSBlocks.warpedSmallDoor)
				.add(DSBlocks.crimsonSmallDoor)
				/* FIXME :: Should be stone? */.add(DSBlocks.stoneSmallDoor)
				/* FIXME :: Should be stone? */.add(DSBlocks.sleeperSmallDoor)
				/* FIXME :: Should be stone? */.add(DSBlocks.murdererDoor)
				/* FIXME :: Should be stone? */.add(DSBlocks.sleeperDoor)
				/* FIXME :: Should be stone? */.add(DSBlocks.stoneDoor)
				.add(DSBlocks.legacyDoor)
		;

		tag(BlockTags.MINEABLE_WITH_AXE)
				.addTag(WOODEN_DRAGON_DOORS)
				.add(DSBlocks.forestPressurePlate)
		;

		tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.add(DSBlocks.dragon_altar_stone)
				.add(DSBlocks.dragon_altar_sandstone)
				.add(DSBlocks.dragon_altar_red_sandstone)
				.add(DSBlocks.dragon_altar_purpur_block)
				.add(DSBlocks.dragon_altar_nether_bricks)
				.add(DSBlocks.dragon_altar_mossy_cobblestone)
				.add(DSBlocks.dragon_altar_blackstone)
				.add(DSBlocks.caveDoor)
				.add(DSBlocks.seaDoor)
				.add(DSBlocks.ironDoor)
				/*.add(DSBlocks.stoneSmallDoor)*/
				/*.add(DSBlocks.sleeperSmallDoor)*/
				.add(DSBlocks.caveSmallDoor)
				.add(DSBlocks.seaSmallDoor)
				.add(DSBlocks.ironSmallDoor)
				.add(DSBlocks.murdererSmallDoor)
				/*.add(DSBlocks.murdererDoor)*/
				/*.add(DSBlocks.sleeperDoor)*/
				/*.add(DSBlocks.stoneDoor)*/
				.add(DSBlocks.caveSourceOfMagic)
				.add(DSBlocks.forestSourceOfMagic)
				.add(DSBlocks.seaSourceOfMagic)
				.add(DSBlocks.treasureDebris)
				.add(DSBlocks.treasureDiamond)
				.add(DSBlocks.treasureEmerald)
				.add(DSBlocks.treasureCopper)
				.add(DSBlocks.treasureGold)
				.add(DSBlocks.treasureIron)
				.add(DSBlocks.helmet1)
				.add(DSBlocks.helmet2)
				.add(DSBlocks.helmet3)
				.add(DSBlocks.dragonBeacon)
				.add(DSBlocks.dragonMemoryBlock)
				.add(DSBlocks.peaceDragonBeacon)
				.add(DSBlocks.magicDragonBeacon)
				.add(DSBlocks.fireDragonBeacon)
				.add(DSBlocks.dragonPressurePlate)
				.add(DSBlocks.humanPressurePlate)
				.add(DSBlocks.seaPressurePlate)
				.add(DSBlocks.cavePressurePlate)
		;

		tag(BlockTags.NEEDS_STONE_TOOL)
				.add(DSBlocks.ironDoor)
				.add(DSBlocks.ironSmallDoor)
				/*.add(DSBlocks.murdererDoor)*/
				.add(DSBlocks.murdererSmallDoor)
				.add(DSBlocks.treasureCopper)
				.add(DSBlocks.treasureIron)
				.add(DSBlocks.helmet1)
				.add(DSBlocks.helmet2)
				.add(DSBlocks.helmet3)
		;

		tag(BlockTags.NEEDS_IRON_TOOL)
				.add(DSBlocks.treasureDiamond)
				.add(DSBlocks.treasureEmerald)
				.add(DSBlocks.treasureGold)
				.add(DSBlocks.dragonBeacon)
				.add(DSBlocks.dragonMemoryBlock)
				.add(DSBlocks.peaceDragonBeacon)
				.add(DSBlocks.magicDragonBeacon)
				.add(DSBlocks.fireDragonBeacon)
		;

		tag(BlockTags.NEEDS_DIAMOND_TOOL)
				.add(DSBlocks.treasureDebris)
		;
	}

	private static TagKey<Block> createKey(@NotNull final String name) {
		return BlockTags.create(new ResourceLocation(DragonSurvivalMod.MODID, name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Block tags";
	}
}