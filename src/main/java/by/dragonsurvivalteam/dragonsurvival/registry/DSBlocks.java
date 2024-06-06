package by.dragonsurvivalteam.dragonsurvival.registry;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.*;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonPressurePlates.PressurePlateType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import software.bernie.geckolib.util.Color;

@EventBusSubscriber( bus = EventBusSubscriber.Bus.MOD )
public class DSBlocks{
	public static final DeferredRegister<Block> DS_BLOCKS = DeferredRegister.create(
			BuiltInRegistries.BLOCK,
			MODID
	);

	// Dragon Doors

	public static final DeferredHolder<Block, DragonDoor> SPRUCE_DRAGON_DOOR = DS_BLOCKS.register(
			"spruce_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> ACACIA_DRAGON_DOOR = DS_BLOCKS.register(
			"acacia_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.ACACIA_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> BIRCH_DRAGON_DOOR = DS_BLOCKS.register(
			"birch_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.BIRCH_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> JUNGLE_DRAGON_DOOR = DS_BLOCKS.register(
			"jungle_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.JUNGLE_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> OAK_DRAGON_DOOR = DS_BLOCKS.register(
			"oak_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.OAK_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> DARK_OAK_DRAGON_DOOR = DS_BLOCKS.register(
			"dark_oak_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.DARK_OAK_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> CRIMSON_DRAGON_DOOR = DS_BLOCKS.register(
			"crimson_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> WARPED_DRAGON_DOOR = DS_BLOCKS.register(
			"warped_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.WARPED_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> LEGACY_DRAGON_DOOR = DS_BLOCKS.register(
			"legacy_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> IRON_DRAGON_DOOR = DS_BLOCKS.register(
			"iron_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(MapColor.METAL)
					.requiresCorrectToolForDrops()
					.strength(5.0F)
					.sound(SoundType.METAL)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.POWER)
	);

	public static final DeferredHolder<Block, DragonDoor> MURDERER_DRAGON_DOOR = DS_BLOCKS.register(
			"murderer_dragon_door",
			() -> new DragonDoor(OAK_DRAGON_DOOR.get().properties(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> SLEEPER_DRAGON_DOOR = DS_BLOCKS.register(
			"sleeper_dragon_door",
			() -> new DragonDoor(OAK_DRAGON_DOOR.get().properties(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> STONE_DRAGON_DOOR = DS_BLOCKS.register(
			"stone_dragon_door",
			() -> new DragonDoor(OAK_DRAGON_DOOR.get().properties(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, DragonDoor> CAVE_DRAGON_DOOR = DS_BLOCKS.register(
			"cave_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.BLACKSTONE.defaultMapColor())
					.instrument(NoteBlockInstrument.BASEDRUM)
					.requiresCorrectToolForDrops()
					.strength(7.0F)
					.sound(SoundType.GILDED_BLACKSTONE)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.CAVE)
	);

	public static final DeferredHolder<Block, DragonDoor> FOREST_DRAGON_DOOR = DS_BLOCKS.register(
			"forest_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(Blocks.DARK_PRISMARINE.defaultMapColor())
					.ignitedByLava()
					.requiresCorrectToolForDrops()
					.strength(7.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.FOREST)
	);

	public static final DeferredHolder<Block, DragonDoor> SEA_DRAGON_DOOR = DS_BLOCKS.register(
			"sea_dragon_door",
			() -> new DragonDoor(Block.Properties.of()
					.mapColor(MapColor.COLOR_BROWN)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.requiresCorrectToolForDrops()
					.strength(7.0F)
					.sound(SoundType.STONE)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.SEA)
	);

	// Small Dragon Doors
	public static final DeferredHolder<Block, SmallDragonDoor> OAK_SMALL_DOOR = DS_BLOCKS.register(
			"oak_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of()
					.mapColor(Blocks.OAK_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> SPRUCE_SMALL_DOOR = DS_BLOCKS.register(
			"spruce_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of()
					.mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> ACACIA_SMALL_DOOR = DS_BLOCKS.register(
			"acacia_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of()
					.mapColor(Blocks.ACACIA_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> BIRCH_SMALL_DOOR = DS_BLOCKS.register(
			"birch_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of()
					.mapColor(Blocks.BIRCH_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> JUNGLE_SMALL_DOOR = DS_BLOCKS.register(
			"jungle_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of()
					.mapColor(Blocks.JUNGLE_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> DARK_OAK_SMALL_DOOR = DS_BLOCKS.register(
			"dark_oak_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of()
					.mapColor(Blocks.DARK_OAK_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> CRIMSON_SMALL_DOOR = DS_BLOCKS.register(
			"crimson_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of()
					.mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> WARPED_SMALL_DOOR = DS_BLOCKS.register(
			"warped_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of()
					.mapColor(Blocks.WARPED_PLANKS.defaultMapColor())
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> STONE_SMALL_DOOR = DS_BLOCKS.register(
			"stone_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of(/*Material.WOOD*/)
					.mapColor(Blocks.STONE.defaultMapColor())
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(3.0F)
					.sound(SoundType.STONE)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> SLEEPER_SMALL_DOOR = DS_BLOCKS.register(
			"sleeper_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of(/*Material.WOOD*/)
					.mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor())
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(3.0F)
					.sound(SoundType.STONE)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> CAVE_SMALL_DOOR = DS_BLOCKS.register(
			"cave_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of(/*Material.STONE*/)
					.mapColor(Blocks.BLACKSTONE.defaultMapColor())
					.instrument(NoteBlockInstrument.BASEDRUM)
					.requiresCorrectToolForDrops()
					.strength(7.0F)
					.sound(SoundType.GILDED_BLACKSTONE)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.CAVE)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> FOREST_SMALL_DOOR = DS_BLOCKS.register(
			"forest_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of(/*Material.WOOD*/)
					.mapColor(Blocks.DARK_PRISMARINE.defaultMapColor())
					.ignitedByLava()
					.requiresCorrectToolForDrops()
					.strength(7.0F)
					.sound(SoundType.WOOD)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.FOREST)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> SEA_SMALL_DOOR = DS_BLOCKS.register(
			"sea_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of(/*Material.STONE*/)
					.mapColor(MapColor.COLOR_BROWN)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.requiresCorrectToolForDrops()
					.strength(7.0F)
					.sound(SoundType.STONE)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.SEA)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> IRON_SMALL_DOOR = DS_BLOCKS.register(
			"iron_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of(/*Material.METAL*/)
					.mapColor(MapColor.METAL)
					.requiresCorrectToolForDrops()
					.strength(5.0F)
					.sound(SoundType.METAL)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.POWER)
	);

	public static final DeferredHolder<Block, SmallDragonDoor> MURDERER_SMALL_DOOR = DS_BLOCKS.register(
			"murderer_small_dragon_door",
			() -> new SmallDragonDoor(Block.Properties.of(/*Material.METAL*/)
					.mapColor(MapColor.METAL)
					.requiresCorrectToolForDrops()
					.strength(5.0F)
					.sound(SoundType.METAL)
					.noOcclusion(), DragonDoor.DragonDoorOpenRequirement.POWER)
	);

	// Source of Magic Blocks

	public static final DeferredHolder<Block, SourceOfMagicBlock> FOREST_SOURCE_OF_MAGIC = DS_BLOCKS.register(
			"forest_source_of_magic",
			() -> new SourceOfMagicBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.randomTicks()
					.strength(3, 100)
					.noOcclusion().lightLevel(c1 -> 10))
	);

	public static final DeferredHolder<Block, SourceOfMagicBlock> CAVE_SOURCE_OF_MAGIC = DS_BLOCKS.register(
			"cave_source_of_magic",
			() -> new SourceOfMagicBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(3, 100)
					.noOcclusion().lightLevel(c1 -> 10))
	);

	public static final DeferredHolder<Block, SourceOfMagicBlock> SEA_SOURCE_OF_MAGIC = DS_BLOCKS.register(
			"sea_source_of_magic",
			() -> new SourceOfMagicBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(3, 100)
					.noOcclusion()
					.lightLevel(c1 -> 10))
	);

	// Dragon Altar Blocks

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_STONE = DS_BLOCKS.register(
			"dragon_altar_stone",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(1.5f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops())
	);

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_SANDSTONE = DS_BLOCKS.register(
			"dragon_altar_sandstone",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(0.8f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops())
	);

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_RED_SANDSTONE = DS_BLOCKS.register(
			"dragon_altar_red_sandstone",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(0.8f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops())
	);

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_PURPUR_BLOCK = DS_BLOCKS.register(
			"dragon_altar_purpur_block",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(1.5f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops())
	);

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_OAK_LOG = DS_BLOCKS.register(
			"dragon_altar_oak_log",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.WOOD)
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(2f)
					.sound(SoundType.WOOD))
	);

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_BIRCH_LOG = DS_BLOCKS.register(
			"dragon_altar_birch_log",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.WOOD)
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(2f)
					.sound(SoundType.WOOD))
	);

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_NETHER_BRICKS = DS_BLOCKS.register(
			"dragon_altar_nether_bricks",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(0.4f)
					.sound(SoundType.NETHER_BRICKS)
					.requiresCorrectToolForDrops())
	);

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_MOSSY_COBBLESTONE = DS_BLOCKS.register(
			"dragon_altar_mossy_cobblestone",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(2f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops())
	);

	public static final DeferredHolder<Block, Block> DRAGON_ALTAR_BLACKSTONE = DS_BLOCKS.register(
			"dragon_altar_blackstone",
			() -> new DragonAltarBlock(Block.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(1.5f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops())
	);

	// Dragon Memory Blocks

	public static final DeferredHolder<Block, RotatedPillarBlock> DRAGON_MEMORY_BLOCK = DS_BLOCKS.register(
			"dragon_memory_block",
			() -> new RotatedPillarBlock(Block.Properties.of()
					.mapColor(MapColor.METAL)
					.pushReaction(PushReaction.BLOCK)
					.strength(3, 30)
					.requiresCorrectToolForDrops())
	);

	// Dragon Beacons

	public static final DeferredHolder<Block, DragonBeacon> DRAGON_BEACON = DS_BLOCKS.register(
			"empty_dragon_beacon",
			() -> new DragonBeacon(Block.Properties.of()
					.mapColor(MapColor.METAL).
					pushReaction(PushReaction.BLOCK)
					.strength(15, 50)
					.requiresCorrectToolForDrops()
					.noOcclusion()
					.noCollission())
	);

	public static final DeferredHolder<Block, DragonBeacon> PEACE_DRAGON_BEACON = DS_BLOCKS.register(
			"dragon_beacon_peace",
			() -> new DragonBeacon(DRAGON_BEACON.get().properties()
					.lightLevel(value -> value.getValue(DragonBeacon.LIT) ? 15 : 0))
	);

	public static final DeferredHolder<Block, DragonBeacon> MAGIC_DRAGON_BEACON = DS_BLOCKS.register(
			"dragon_beacon_magic",
			() -> new DragonBeacon(DRAGON_BEACON.get().properties()
					.lightLevel(value -> value.getValue(DragonBeacon.LIT) ? 15 : 0))
	);

	public static final DeferredHolder<Block, DragonBeacon> FIRE_DRAGON_BEACON = DS_BLOCKS.register(
			"dragon_beacon_fire",
			() -> new DragonBeacon(DRAGON_BEACON.get().properties()
					.lightLevel(value -> value.getValue(DragonBeacon.LIT) ? 15 : 0))
	);

	// Treasure Blocks

	public static final DeferredHolder<Block, TreasureBlock> TREASURE_DEBRIS = DS_BLOCKS.register(
			"treasure_debris",
			() -> new TreasureBlock(Color.ofRGB(148, 120, 114),
					BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_BROWN)
					.noOcclusion()
					.sound(DSSounds.TREASURE_METAL)
					.strength(0.5F))
	);

	public static final DeferredHolder<Block, TreasureBlock> TREASURE_DIAMOND = DS_BLOCKS.register(
			"treasure_diamond",
			() -> new TreasureBlock(Color.ofRGB(212, 255, 255),
					BlockBehaviour.Properties.of()
					.mapColor(MapColor.DIAMOND)
					.noOcclusion()
					.sound(DSSounds.TREASURE_GEM)
					.strength(0.5F))
	);

	public static final DeferredHolder<Block, TreasureBlock> TREASURE_EMERALD = DS_BLOCKS.register(
			"treasure_emerald",
			() -> new TreasureBlock(Color.ofRGB(57, 240, 94),
					BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_GREEN)
					.noOcclusion()
					.sound(DSSounds.TREASURE_GEM)
					.strength(0.5F))
	);

	public static final DeferredHolder<Block, TreasureBlock> TREASURE_COPPER = DS_BLOCKS.register(
			"treasure_copper",
			() -> new TreasureBlock(Color.ofRGB(255, 255, 208),
					BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_ORANGE)
					.instrument(NoteBlockInstrument.HAT)
					.noOcclusion()
					.sound(DSSounds.TREASURE_METAL)
					.strength(0.5F))
	);

	public static final DeferredHolder<Block, TreasureBlock> TREASURE_GOLD = DS_BLOCKS.register(
			"treasure_gold",
			() -> new TreasureBlock(Color.ofRGB(255, 255, 243),
					BlockBehaviour.Properties.of()
					.mapColor(MapColor.GOLD)
					.noOcclusion()
					.sound(DSSounds.TREASURE_METAL)
					.strength(0.5F))
	);

	public static final DeferredHolder<Block, TreasureBlock> TREASURE_IRON = DS_BLOCKS.register(
			"treasure_iron",
			() -> new TreasureBlock(Color.ofRGB(211, 211, 211),
					BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.noOcclusion()
					.sound(DSSounds.TREASURE_METAL)
					.strength(0.5F))
	);

	// Dragon Pressure Plates

	public static final DeferredHolder<Block, DragonPressurePlates> DRAGON_PRESSURE_PLATE = DS_BLOCKS.register(
			"dragon_pressure_plate",
			() -> new DragonPressurePlates(BlockBehaviour.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(1.5f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops(), PressurePlateType.DRAGON)
	);

	public static final DeferredHolder<Block, DragonPressurePlates> HUMAN_PRESSURE_PLATE = DS_BLOCKS.register(
			"human_pressure_plate",
			() -> new DragonPressurePlates(BlockBehaviour.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(1.5f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops(), PressurePlateType.HUMAN)
	);

	public static final DeferredHolder<Block, DragonPressurePlates> SEA_PRESSURE_PLATE = DS_BLOCKS.register(
			"sea_dragon_pressure_plate",
			() -> new DragonPressurePlates(BlockBehaviour.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(1.5f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops(), PressurePlateType.SEA)
	);

	public static final DeferredHolder<Block, DragonPressurePlates> FOREST_PRESSURE_PLATE = DS_BLOCKS.register(
			"forest_dragon_pressure_plate",
			() -> new DragonPressurePlates(BlockBehaviour.Properties.of()
					.mapColor(MapColor.WOOD)
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.strength(2.0F)
					.sound(SoundType.WOOD)
					.requiresCorrectToolForDrops(), PressurePlateType.FOREST)
	);

	public static final DeferredHolder<Block, DragonPressurePlates> CAVE_PRESSURE_PLATE = DS_BLOCKS.register(
			"cave_dragon_pressure_plate",
			() -> new DragonPressurePlates(BlockBehaviour.Properties.of()
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.strength(1.5f)
					.sound(SoundType.STONE)
					.requiresCorrectToolForDrops(), PressurePlateType.CAVE)
	);

	// TODO: (maybe we need to register items too? not sure)
	// Helmet Blocks

	public static final DeferredHolder<Block, HelmetBlock> HELMET_BLOCK_1 = DS_BLOCKS.register(
			"broken_knight_helmet_1",
			() -> new HelmetBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(5.0F)
					.sound(SoundType.METAL))
	);

	public static final DeferredHolder<Block, HelmetBlock> HELMET_BLOCK_2 = DS_BLOCKS.register(
			"broken_knight_helmet_2",
			() -> new HelmetBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(5.0F)
					.sound(SoundType.METAL)
					.noOcclusion())
	);

	public static final DeferredHolder<Block, HelmetBlock> HELMET_BLOCK_3 = DS_BLOCKS.register(
			"broken_knight_helmet_3",
			() -> new HelmetBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(5.0F)
					.sound(SoundType.METAL)
					.noOcclusion())
	);
}