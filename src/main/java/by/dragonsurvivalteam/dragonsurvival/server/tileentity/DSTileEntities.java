package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSTileEntities{

	public static TileEntityType<PredatorStarTileEntity> PREDATOR_STAR_TILE_ENTITY_TYPE;
	public static TileEntityType<SourceOfMagicTileEntity> sourceOfMagicTileEntity;
	public static TileEntityType<SourceOfMagicPlaceholder> sourceOfMagicPlaceholder;
	public static TileEntityType<HelmetTileEntity> helmetTile;
	public static TileEntityType<DragonBeaconTileEntity> dragonBeacon;

	@SubscribeEvent
	public static void registerBlockEntities(RegistryEvent.Register<TileEntityType<?>> event){
		sourceOfMagicTileEntity = TileEntityType.Builder.of(() -> new SourceOfMagicTileEntity(sourceOfMagicTileEntity), DSBlocks.caveSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.forestSourceOfMagic).build(null);
		PREDATOR_STAR_TILE_ENTITY_TYPE = TileEntityType.Builder.of(PredatorStarTileEntity::new, DSBlocks.PREDATOR_STAR_BLOCK).build(null);
		IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
		registry.registerAll(sourceOfMagicTileEntity.setRegistryName(DragonSurvivalMod.MODID, "dragon_nest"), PREDATOR_STAR_TILE_ENTITY_TYPE.setRegistryName(DragonSurvivalMod.MODID, "predator_star_te"));
		sourceOfMagicPlaceholder = TileEntityType.Builder.of(() -> new SourceOfMagicPlaceholder(sourceOfMagicPlaceholder), DSBlocks.forestSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.caveSourceOfMagic).build(null);
		registry.register(sourceOfMagicPlaceholder.setRegistryName("placeholder"));
		helmetTile = TileEntityType.Builder.of(HelmetTileEntity::new, DSBlocks.helmet1, DSBlocks.helmet2, DSBlocks.helmet3).build(null);
		helmetTile.setRegistryName(DragonSurvivalMod.MODID, "knight_helmet");
		registry.register(helmetTile);
		dragonBeacon = TileEntityType.Builder.of(DragonBeaconTileEntity::new, DSBlocks.dragonBeacon, DSBlocks.peaceDragonBeacon, DSBlocks.magicDragonBeacon, DSBlocks.fireDragonBeacon).build(null);
		dragonBeacon.setRegistryName(DragonSurvivalMod.MODID, "dragon_beacon");
		registry.register(dragonBeacon);
	}
}