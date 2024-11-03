package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.*;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonPressurePlates.PressurePlateType;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.CompoundTagBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSItems.DS_ITEMS;

public class DSBlocks {
    public static final DeferredRegister<Block> DS_BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
    public static final HashMap<String, Pair<DeferredHolder<Block, SkeletonPieceBlock>, DeferredHolder<Item, BlockItem>>> SKELETON_PIECES = new HashMap<>(); // FIXME :: why are these stored in a map if the map is unused

    // --- Dragon Doors --- //

    // TODO :: blocks used description_addition

    @Translation(type = Translation.Type.BLOCK, comments = "Spruce Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> SPRUCE_DRAGON_DOOR = register(
            "spruce_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Acacia Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> ACACIA_DRAGON_DOOR = register(
            "acacia_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.ACACIA_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Birch Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> BIRCH_DRAGON_DOOR = register(
            "birch_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.BIRCH_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Jungle Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> JUNGLE_DRAGON_DOOR = register(
            "jungle_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.JUNGLE_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Oak Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> OAK_DRAGON_DOOR = register(
            "oak_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.OAK_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Dark Oak Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> DARK_OAK_DRAGON_DOOR = register(
            "dark_oak_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.DARK_OAK_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Crimson Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> CRIMSON_DRAGON_DOOR = register(
            "crimson_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Warped Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> WARPED_DRAGON_DOOR = register(
            "warped_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.WARPED_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Legacy Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> LEGACY_DRAGON_DOOR = register(
            "legacy_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Iron Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> IRON_DRAGON_DOOR = register(
            "iron_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.POWER)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Gothic Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> SLEEPER_DRAGON_DOOR = register(
            "sleeper_dragon_door",
            () -> new DragonDoor(OAK_DRAGON_DOOR.get().properties(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Skyrim Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> MURDERER_DRAGON_DOOR = register(
            "murderer_dragon_door",
            () -> new DragonDoor(OAK_DRAGON_DOOR.get().properties(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Stone Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> STONE_DRAGON_DOOR = register(
            "stone_dragon_door",
            () -> new DragonDoor(OAK_DRAGON_DOOR.get().properties(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Cave Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> CAVE_DRAGON_DOOR = register(
            "cave_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.BLACKSTONE.defaultMapColor())
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(7.0F)
                    .sound(SoundType.GILDED_BLACKSTONE)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.CAVE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Forest Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> FOREST_DRAGON_DOOR = register(
            "forest_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(Blocks.DARK_PRISMARINE.defaultMapColor())
                    .ignitedByLava()
                    .requiresCorrectToolForDrops()
                    .strength(7.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.FOREST)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Sea Dragon Door")
    public static final DeferredHolder<Block, DragonDoor> SEA_DRAGON_DOOR = register(
            "sea_dragon_door",
            () -> new DragonDoor(Block.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(7.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.SEA)
    );

    // --- Small Dragon Doors --- //

    @Translation(type = Translation.Type.BLOCK, comments = "Oak Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> OAK_SMALL_DOOR = register(
            "oak_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of()
                    .mapColor(Blocks.OAK_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Spruce Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> SPRUCE_SMALL_DOOR = register(
            "spruce_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of()
                    .mapColor(Blocks.SPRUCE_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Acacia Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> ACACIA_SMALL_DOOR = register(
            "acacia_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of()
                    .mapColor(Blocks.ACACIA_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Birch Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> BIRCH_SMALL_DOOR = register(
            "birch_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of()
                    .mapColor(Blocks.BIRCH_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Jungle Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> JUNGLE_SMALL_DOOR = register(
            "jungle_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of()
                    .mapColor(Blocks.JUNGLE_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Dark Oak Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> DARK_OAK_SMALL_DOOR = register(
            "dark_oak_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of()
                    .mapColor(Blocks.DARK_OAK_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Crimson Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> CRIMSON_SMALL_DOOR = register(
            "crimson_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of()
                    .mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Warped Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> WARPED_SMALL_DOOR = register(
            "warped_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of()
                    .mapColor(Blocks.WARPED_PLANKS.defaultMapColor())
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Stone Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> STONE_SMALL_DOOR = register(
            "stone_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of(/*Material.WOOD*/)
                    .mapColor(Blocks.STONE.defaultMapColor())
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Gothic Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> SLEEPER_SMALL_DOOR = register(
            "sleeper_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of(/*Material.WOOD*/)
                    .mapColor(Blocks.CRIMSON_PLANKS.defaultMapColor())
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.NONE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Cave Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> CAVE_SMALL_DOOR = register(
            "cave_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of(/*Material.STONE*/)
                    .mapColor(Blocks.BLACKSTONE.defaultMapColor())
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(7.0F)
                    .sound(SoundType.GILDED_BLACKSTONE)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.CAVE)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Forest Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> FOREST_SMALL_DOOR = register(
            "forest_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of(/*Material.WOOD*/)
                    .mapColor(Blocks.DARK_PRISMARINE.defaultMapColor())
                    .ignitedByLava()
                    .requiresCorrectToolForDrops()
                    .strength(7.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.FOREST)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Sea Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> SEA_SMALL_DOOR = register(
            "sea_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of(/*Material.STONE*/)
                    .mapColor(MapColor.COLOR_BROWN)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(7.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.SEA)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Iron Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> IRON_SMALL_DOOR = register(
            "iron_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of(/*Material.METAL*/)
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.POWER)
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Skyrim Small Dragon Door")
    public static final DeferredHolder<Block, SmallDragonDoor> MURDERER_SMALL_DOOR = register(
            "murderer_small_dragon_door",
            () -> new SmallDragonDoor(Block.Properties.of(/*Material.METAL*/)
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion(), DragonDoor.DragonDoorOpenRequirement.POWER)
    );

    // --- Source of Magic --- //

    @Translation(type = Translation.Type.BLOCK, comments = "Forest Source of Magic")
    public static final DeferredHolder<Block, SourceOfMagicBlock> FOREST_SOURCE_OF_MAGIC = register(
            "forest_source_of_magic",
            () -> new SourceOfMagicBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .randomTicks()
                    .strength(3, 100)
                    .noOcclusion().lightLevel(c1 -> 10))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Cave Source of Magic")
    public static final DeferredHolder<Block, SourceOfMagicBlock> CAVE_SOURCE_OF_MAGIC = register(
            "cave_source_of_magic",
            () -> new SourceOfMagicBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(3, 100)
                    .noOcclusion().lightLevel(c1 -> 10))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Sea Source of Magic")
    public static final DeferredHolder<Block, SourceOfMagicBlock> SEA_SOURCE_OF_MAGIC = register(
            "sea_source_of_magic",
            () -> new SourceOfMagicBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(3, 100)
                    .noOcclusion()
                    .lightLevel(c1 -> 10))
    );

    // --- Dragon Altars --- //

    @Translation(type = Translation.Type.BLOCK, comments = "Stone Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_STONE = register(
            "dragon_altar_stone",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(1.5f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Sandstone Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_SANDSTONE = register(
            "dragon_altar_sandstone",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(0.8f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Red Sandstone Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_RED_SANDSTONE = register(
            "dragon_altar_red_sandstone",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(0.8f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Purpur Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_PURPUR_BLOCK = register(
            "dragon_altar_purpur_block",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(1.5f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Oak Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_OAK_LOG = register(
            "dragon_altar_oak_log",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2f)
                    .sound(SoundType.WOOD))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Birch Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_BIRCH_LOG = register(
            "dragon_altar_birch_log",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2f)
                    .sound(SoundType.WOOD))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Nether Brick Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_NETHER_BRICKS = register(
            "dragon_altar_nether_bricks",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(0.4f)
                    .sound(SoundType.NETHER_BRICKS)
                    .requiresCorrectToolForDrops())
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Mossy Cobblestone Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_MOSSY_COBBLESTONE = register(
            "dragon_altar_mossy_cobblestone",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(2f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Blackstone Dragon Altar")
    public static final DeferredHolder<Block, Block> DRAGON_ALTAR_BLACKSTONE = register(
            "dragon_altar_blackstone",
            () -> new DragonAltarBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(1.5f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    // --- Dragon Beacons --- //

    @Translation(type = Translation.Type.BLOCK, comments = "Dragon Memory for Beacons")
    public static final DeferredHolder<Block, RotatedPillarBlock> DRAGON_MEMORY_BLOCK = register(
            "dragon_memory_block",
            () -> new RotatedPillarBlock(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .pushReaction(PushReaction.BLOCK)
                    .strength(3, 30)
                    .requiresCorrectToolForDrops())
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Empty Dragon Beacon")
    public static final DeferredHolder<Block, DragonBeacon> DRAGON_BEACON = register(
            "empty_dragon_beacon",
            () -> new DragonBeacon(Block.Properties.of()
                    .mapColor(MapColor.METAL).
                    pushReaction(PushReaction.BLOCK)
                    .strength(15, 50)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .noCollission())
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Sea Dragon Beacon")
    public static final DeferredHolder<Block, DragonBeacon> PEACE_DRAGON_BEACON = register(
            "dragon_beacon_peace",
            () -> new DragonBeacon(DRAGON_BEACON.get().properties()
                    .lightLevel(value -> value.getValue(DragonBeacon.LIT) ? 15 : 0))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Forest Dragon Beacon")
    public static final DeferredHolder<Block, DragonBeacon> MAGIC_DRAGON_BEACON = register(
            "dragon_beacon_magic",
            () -> new DragonBeacon(DRAGON_BEACON.get().properties()
                    .lightLevel(value -> value.getValue(DragonBeacon.LIT) ? 15 : 0))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Cave Dragon Beacon")
    public static final DeferredHolder<Block, DragonBeacon> FIRE_DRAGON_BEACON = register(
            "dragon_beacon_fire",
            () -> new DragonBeacon(DRAGON_BEACON.get().properties()
                    .lightLevel(value -> value.getValue(DragonBeacon.LIT) ? 15 : 0))
    );

    // --- Treasures --- //

    @Translation(type = Translation.Type.BLOCK, comments = "Debris Dragon Treasure")
    public static final DeferredHolder<Block, TreasureBlock> TREASURE_DEBRIS = register(
            "treasure_debris",
            () -> new TreasureBlock(FastColor.ARGB32.color(255, 148, 120, 114),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BROWN)
                            .noOcclusion()
                            .sound(DSSounds.TREASURE_METAL)
                            .strength(0.5F))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Diamond Dragon Treasure")
    public static final DeferredHolder<Block, TreasureBlock> TREASURE_DIAMOND = register(
            "treasure_diamond",
            () -> new TreasureBlock(FastColor.ARGB32.color(255, 212, 255, 255),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DIAMOND)
                            .noOcclusion()
                            .sound(DSSounds.TREASURE_GEM)
                            .strength(0.5F))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Emerald Dragon Treasure")
    public static final DeferredHolder<Block, TreasureBlock> TREASURE_EMERALD = register(
            "treasure_emerald",
            () -> new TreasureBlock(FastColor.ARGB32.color(255, 57, 240, 94),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GREEN)
                            .noOcclusion()
                            .sound(DSSounds.TREASURE_GEM)
                            .strength(0.5F))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Copper Dragon Treasure")
    public static final DeferredHolder<Block, TreasureBlock> TREASURE_COPPER = register(
            "treasure_copper",
            () -> new TreasureBlock(FastColor.ARGB32.color(255, 255, 255, 208),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_ORANGE)
                            .instrument(NoteBlockInstrument.HAT)
                            .noOcclusion()
                            .sound(DSSounds.TREASURE_METAL)
                            .strength(0.5F))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Gold Dragon Treasure")
    public static final DeferredHolder<Block, TreasureBlock> TREASURE_GOLD = register(
            "treasure_gold",
            () -> new TreasureBlock(FastColor.ARGB32.color(255, 255, 255, 243),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.GOLD)
                            .noOcclusion()
                            .sound(DSSounds.TREASURE_METAL)
                            .strength(0.5F))
    );

    @Translation(type = Translation.Type.BLOCK, comments = "Iron Dragon Treasure")
    public static final DeferredHolder<Block, TreasureBlock> TREASURE_IRON = register(
            "treasure_iron",
            () -> new TreasureBlock(FastColor.ARGB32.color(255, 211, 211, 211),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .noOcclusion()
                            .sound(DSSounds.TREASURE_METAL)
                            .strength(0.5F))
    );

    // --- Dragon Treasure Plates --- //

    public static final DeferredHolder<Block, DragonPressurePlates> DRAGON_PRESSURE_PLATE = DS_BLOCKS.register(
            "dragon_pressure_plate",
            () -> new DragonPressurePlates(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(1.5f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops(), PressurePlateType.DRAGON)
    );

    public static final Holder<Item> DRAGON_PRESSURE_PLATE_ITEM = DS_ITEMS.register(
            "dragon_pressure_plate",
            () -> new BlockItem(DRAGON_PRESSURE_PLATE.get(), new Item.Properties())
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

    public static final Holder<Item> HUMAN_PRESSURE_PLATE_ITEM = DS_ITEMS.register(
            "human_pressure_plate",
            () -> new BlockItem(HUMAN_PRESSURE_PLATE.get(), new Item.Properties())
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

    public static final Holder<Item> SEA_PRESSURE_PLATE_ITEM = DS_ITEMS.register(
            "sea_dragon_pressure_plate",
            () -> new BlockItem(SEA_PRESSURE_PLATE.get(), new Item.Properties())
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

    public static final Holder<Item> FOREST_PRESSURE_PLATE_ITEM = DS_ITEMS.register(
            "forest_dragon_pressure_plate",
            () -> new BlockItem(FOREST_PRESSURE_PLATE.get(), new Item.Properties())
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

    public static final Holder<Item> CAVE_PRESSURE_PLATE_ITEM = DS_ITEMS.register(
            "cave_dragon_pressure_plate",
            () -> new BlockItem(CAVE_PRESSURE_PLATE.get(), new Item.Properties())
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

    public static final DeferredHolder<Item, BlockItem> HELMET_BLOCK_1_ITEM = DS_ITEMS.register(
            "broken_knight_helmet_1",
            () -> new BlockItem(HELMET_BLOCK_1.get(), new Item.Properties())
    );

    public static final DeferredHolder<Block, HelmetBlock> HELMET_BLOCK_2 = DS_BLOCKS.register(
            "broken_knight_helmet_2",
            () -> new HelmetBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion())
    );

    public static final DeferredHolder<Item, BlockItem> HELMET_BLOCK_2_ITEM = DS_ITEMS.register(
            "broken_knight_helmet_2",
            () -> new BlockItem(HELMET_BLOCK_2.get(), new Item.Properties())
    );

    public static final DeferredHolder<Block, HelmetBlock> HELMET_BLOCK_3 = DS_BLOCKS.register(
            "broken_knight_helmet_3",
            () -> new HelmetBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion())
    );

    public static final DeferredHolder<Item, BlockItem> HELMET_BLOCK_3_ITEM = DS_ITEMS.register(
            "broken_knight_helmet_3",
            () -> new BlockItem(HELMET_BLOCK_3.get(), new Item.Properties())
    );

    private static final CompoundTag GOOD_VAULT_TAG = CompoundTagBuilder.tag()
            .putTag("config", CompoundTagBuilder.tag()
                    .putTag("key_item", CompoundTagBuilder.tag()
                            .putInt("count", 1)
                            .putString("id", location(DSItems.GOOD_DRAGON_KEY_ID)).build()
                    ).putString("loot_table", location("generic/dragon_vault_friendly")).build()
            ).build();

    private static final CompoundTag EVIL_VAULT_TAG = CompoundTagBuilder.tag()
            .putTag("config", CompoundTagBuilder.tag()
                    .putTag("key_item", CompoundTagBuilder.tag()
                            .putInt("count", 1)
                            .putString("id", location(DSItems.EVIL_DRAGON_KEY_ID)).build()
                    ).putString("loot_table", location("generic/dragon_vault_angry")).build()
            ).build();

    private static final CompoundTag HUNTER_VAULT_TAG = CompoundTagBuilder.tag()
            .putTag("config", CompoundTagBuilder.tag()
                    .putTag("key_item", CompoundTagBuilder.tag()
                            .putInt("count", 1)
                            .putString("id", location(DSItems.HUNTER_KEY_ID)).build()
                    ).putString("loot_table", location("generic/dragon_vault_hunter")).build()
            ).build();

    public static String location(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path).toString();
    }

    // Copied from "vault" entry for Blocks.java
    private static final BlockBehaviour.Properties vaultBlockProperties = BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .noOcclusion()
            .sound(SoundType.VAULT)
            .lightLevel(p_323402_ -> p_323402_.getValue(VaultBlock.STATE).lightLevel())
            .strength(50.0F)
            .isViewBlocking((a, b, c) -> false);

    public static final DeferredHolder<Block, VaultBlock> DRAGON_VAULT_FRIENDLY = DS_BLOCKS.register(
            "dragon_vault_friendly",
            () -> new VaultBlock(vaultBlockProperties)
    );

    public static final DeferredHolder<Item, BlockItem> DRAGON_VAULT_FRIENDLY_ITEM = DS_ITEMS.register(
            "dragon_vault_friendly",
            () -> new BlockItem(DRAGON_VAULT_FRIENDLY.get(), new Item.Properties()
                    .component(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(GOOD_VAULT_TAG)))
    );

    public static final DeferredHolder<Block, VaultBlock> DRAGON_VAULT_ANGRY = DS_BLOCKS.register(
            "dragon_vault_angry",
            () -> new VaultBlock(vaultBlockProperties)
    );

    public static final DeferredHolder<Item, BlockItem> DRAGON_VAULT_ANGRY_ITEM = DS_ITEMS.register(
            "dragon_vault_angry",
            () -> new BlockItem(DRAGON_VAULT_ANGRY.get(), new Item.Properties()
                    .component(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(EVIL_VAULT_TAG)))
    );

    public static final DeferredHolder<Block, VaultBlock> DRAGON_VAULT_HUNTER = DS_BLOCKS.register(
            "dragon_vault_hunter",
            () -> new VaultBlock(vaultBlockProperties)
    );

    public static final DeferredHolder<Item, BlockItem> DRAGON_VAULT_HUNTER_ITEM = DS_ITEMS.register(
            "dragon_vault_hunter",
            () -> new BlockItem(DRAGON_VAULT_HUNTER.get(), new Item.Properties()
                    .component(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(HUNTER_VAULT_TAG)))
    );

    public static final DeferredHolder<Block, Block> DRAGON_RIDER_WORKBENCH = DS_BLOCKS.register(
            "dragon_rider_workbench",
            () -> new DragonRiderWorkbenchBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.TRIAL_SPAWNER)
                    .mapColor(MapColor.WOOD)
            )
    );

    public static final DeferredHolder<Item, BlockItem> DRAGON_RIDER_WORKBENCH_ITEM = DS_ITEMS.register(
            "dragon_rider_workbench",
            () -> new BlockItem(DRAGON_RIDER_WORKBENCH.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                    pTooltipComponents.add(Component.translatable("ds.description.dragon_rider_workbench"));
                }
            }
    );

    private static <B extends Block> DeferredHolder<Block, B> register(final String name, final Supplier<B> supplier) {
        DeferredHolder<Block, B> holder = DS_BLOCKS.register(name, supplier);
        DS_ITEMS.register(name, () -> new BlockItem(holder.value(), new Item.Properties()));
        return holder;
    }

    static {
        for (int i = 1; i < 9; i++) { // FIXME :: what does he 9 indicate
            for (SkeletonPieceBlock.Type type : SkeletonPieceBlock.Types.values()) {
                DeferredHolder<Block, SkeletonPieceBlock> block = DS_BLOCKS.register(type.getSerializedName() + "_skin" + i,
                        () -> new SkeletonPieceBlock(type, BlockBehaviour.Properties.of()
                                .mapColor(MapColor.CLAY)
                                .strength(1.0F)
                                .sound(SoundType.BONE_BLOCK)));

                DeferredHolder<Item, BlockItem> item = DS_ITEMS.register(type.getSerializedName() + "_skin" + i,
                        () -> new BlockItem(block.value(), new Item.Properties()));

                SKELETON_PIECES.put(type.getSerializedName(), new Pair<>(block, item));
            }
        }
    }
}