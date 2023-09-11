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
	// MAGIC PREDATOR
	// public static BlockEntityType<PredatorStarTileEntity> PREDATOR_STAR_TILE_ENTITY_TYPE;
	//
	public static BlockEntityType<SourceOfMagicTileEntity> sourceOfMagicTileEntity;
	public static BlockEntityType<SourceOfMagicPlaceholder> sourceOfMagicPlaceholder;
	public static BlockEntityType<HelmetTileEntity> helmetTile;
	public static BlockEntityType<DragonBeaconTileEntity> dragonBeacon;

	@SubscribeEvent
	public static void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event){
		IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();
		sourceOfMagicTileEntity = BlockEntityType.Builder.of(SourceOfMagicTileEntity::new, DSBlocks.caveSourceOfMagic, DSBlocks.seaSourceOfMagic, DSBlocks.forestSourceOfMagic).build(null);
		registry.register(sourceOfMagicTileEntity.setRegistryName(DragonSurvivalMod.MODID, "dragon_nest"));
		// registry.register(PREDATOR_STAR_TILE_ENTITY_TYPE.setRegistryName(DragonSurvivalMod.MODID, "predator_star_te"));

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
/*
* In the realm of Dragon's Kingdom, where dragonkind and humans coexisted peacefully, sharing their wisdom and experiences, a new threat
* emerged that shook the very foundations of their world.
* This threat was none other than the Magical Predators, a malevolent species with a dark magic that coveted the accumulated knowledge
* and magical prowess of both dragons and humans to augment their own power.
* The presence of the Magical Predators brought chaos and turmoil to Dragon's Kingdom. In response to this dire situation, the dragons
* and humans decided to unite their forces and expel the Magical Predators from their land.
* They convened a grand assembly, where dragon elders, human leaders, and a diverse assembly of beings gathered to strategize their
* response to this newfound threat.
* Within a mystical sanctuary deep within Dragon's Kingdom, they conducted a powerful ritual, harnessing the ancient magic of their
* world, uniting the will of every dragon and human in the kingdom.
* This extraordinary force manifested as a brilliant beam of light that illuminated the entire realm.
* This radiant beam of light symbolized the unwavering unity, justice, and peace sought by the dragons and humans.
* It traversed Dragon's Kingdom, shining a light upon the lair of the Magical Predators.
* These malevolent beings, despite their dark magic, found themselves unable to withstand the immense pressure of this force.
* In the end, they were forced to yield and depart from Dragon's Kingdom.
* The dragons and humans celebrated their triumph, their collaborative efforts having successfully vanquished the nefarious
* Magical Predators and preserving the peace and harmony of their cherished Dragon's Kingdom.
* The Magical Predators will stay far away from the Dragon's Kingdom for a very, very long time.
* They may return in the future, bringing new threats, or they may disappear forever from the Dragon's Kingdom.
* */