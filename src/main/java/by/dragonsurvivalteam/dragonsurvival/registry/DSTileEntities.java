package by.dragonsurvivalteam.dragonsurvival.registry;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DragonBeaconTileEntity;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.HelmetTileEntity;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicPlaceholder;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DSTileEntities {
	public static DeferredRegister<BlockEntityType<?>> DS_TILE_ENTITIES = DeferredRegister.create(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			MODID
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SourceOfMagicTileEntity>> SOURCE_OF_MAGIC_TILE_ENTITY = DS_TILE_ENTITIES.register(
			"dragon_nest", () -> BlockEntityType.Builder.of(
							SourceOfMagicTileEntity::new,
							DSBlocks.CAVE_SOURCE_OF_MAGIC.get(),
							DSBlocks.SEA_SOURCE_OF_MAGIC.get(),
							DSBlocks.FOREST_SOURCE_OF_MAGIC.get())
					.build(null)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SourceOfMagicPlaceholder>> SOURCE_OF_MAGIC_PLACEHOLDER = DS_TILE_ENTITIES.register(
			"placeholder", () -> BlockEntityType.Builder.of(
							SourceOfMagicPlaceholder::new,
							DSBlocks.FOREST_SOURCE_OF_MAGIC.get(),
							DSBlocks.SEA_SOURCE_OF_MAGIC.get(),
							DSBlocks.CAVE_SOURCE_OF_MAGIC.get())
					.build(null)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HelmetTileEntity>> HELMET_TILE = DS_TILE_ENTITIES.register(
			"knight_helmet", () -> BlockEntityType.Builder.of(
							HelmetTileEntity::new,
							DSBlocks.HELMET_BLOCK_1.get(),
							DSBlocks.HELMET_BLOCK_2.get(),
							DSBlocks.HELMET_BLOCK_3.get())
					.build(null)
	);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DragonBeaconTileEntity>> DRAGON_BEACON = DS_TILE_ENTITIES.register(
			"dragon_beacon", () -> BlockEntityType.Builder.of(
							DragonBeaconTileEntity::new,
							DSBlocks.DRAGON_BEACON.get(),
							DSBlocks.PEACE_DRAGON_BEACON.get(),
							DSBlocks.MAGIC_DRAGON_BEACON.get(),
							DSBlocks.FIRE_DRAGON_BEACON.get())
					.build(null)
	);
}