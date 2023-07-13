package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSTileEntities{

	public static BlockEntityType<SourceOfMagicTileEntity> sourceOfMagicTileEntity;
	public static BlockEntityType<SourceOfMagicPlaceholder> sourceOfMagicPlaceholder;
	public static BlockEntityType<HelmetTileEntity> helmetTile;
	public static BlockEntityType<DragonBeaconTileEntity> dragonBeacon;

	@SubscribeEvent
	public static void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event){
		sourceOfMagicTileEntity = BlockEntityType.Builder.of(SourceOfMagicTileEntity::new, DSBlocks.caveSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.forestSourceOfMagic).build(null);
		IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();

		sourceOfMagicPlaceholder = BlockEntityType.Builder.of(SourceOfMagicPlaceholder::new, DSBlocks.forestSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.caveSourceOfMagic).build(null);
		registry.register(sourceOfMagicPlaceholder.setRegistryName("placeholder"));

		helmetTile = BlockEntityType.Builder.of(HelmetTileEntity::new, DSBlocks.helmet1, DSBlocks.helmet2, DSBlocks.helmet3).build(null);
		helmetTile.setRegistryName(DragonSurvivalMod.MODID, "knight_helmet");
		registry.register(helmetTile);

		dragonBeacon = BlockEntityType.Builder.of(DragonBeaconTileEntity::new, DSBlocks.dragonBeacon, DSBlocks.peaceDragonBeacon, DSBlocks.magicDragonBeacon, DSBlocks.fireDragonBeacon).build(null);
		dragonBeacon.setRegistryName(DragonSurvivalMod.MODID, "dragon_beacon");
		registry.register(dragonBeacon);
	}
}