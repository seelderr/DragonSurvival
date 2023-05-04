package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSTileEntities{

	public static BlockEntityType<PredatorStarTileEntity> PREDATOR_STAR_TILE_ENTITY_TYPE;
	public static BlockEntityType<SourceOfMagicTileEntity> sourceOfMagicTileEntity;
	public static BlockEntityType<SourceOfMagicPlaceholder> sourceOfMagicPlaceholder;
	public static BlockEntityType<HelmetTileEntity> helmetTile;
	public static BlockEntityType<DragonBeaconTileEntity> dragonBeacon;

	@SubscribeEvent
	public static void register(RegisterEvent event)
	{
		if (!event.getRegistryKey().equals(Registry.BLOCK_ENTITY_TYPE_REGISTRY))
			return;

		sourceOfMagicTileEntity = BlockEntityType.Builder.of(SourceOfMagicTileEntity::new, DSBlocks.caveSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.forestSourceOfMagic).build(null);
		PREDATOR_STAR_TILE_ENTITY_TYPE = BlockEntityType.Builder.of(PredatorStarTileEntity::new, DSBlocks.PREDATOR_STAR_BLOCK).build(null);

		event.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY,new ResourceLocation(MODID, "dragon_nest"), ()->sourceOfMagicTileEntity);
		event.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY,new ResourceLocation(MODID,"predator_star_te"), ()->PREDATOR_STAR_TILE_ENTITY_TYPE);

		sourceOfMagicPlaceholder = BlockEntityType.Builder.of(SourceOfMagicPlaceholder::new, DSBlocks.forestSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.caveSourceOfMagic).build(null);
		event.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY,new ResourceLocation(MODID,"placeholder"), ()->sourceOfMagicPlaceholder);

		helmetTile = BlockEntityType.Builder.of(HelmetTileEntity::new, DSBlocks.helmet1, DSBlocks.helmet2, DSBlocks.helmet3).build(null);
		event.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY,new ResourceLocation(MODID,"knight_helmet"), ()->helmetTile);

		dragonBeacon = BlockEntityType.Builder.of(DragonBeaconTileEntity::new, DSBlocks.dragonBeacon, DSBlocks.peaceDragonBeacon, DSBlocks.magicDragonBeacon, DSBlocks.fireDragonBeacon).build(null);
		event.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY,new ResourceLocation(MODID,"dragon_beacon"), ()->dragonBeacon);
	}
}