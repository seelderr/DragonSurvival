package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.NetherBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active.ForestBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.StormBreathAbility;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DragonConfigHandler{
	public static List<Block> SEA_DRAGON_HYDRATION_BLOCKS = List.of();
	public static List<Item> SEA_DRAGON_HYDRATION_USE_ALTERNATIVES = List.of();

	public static List<Block> FOREST_DRAGON_BREATH_GROW_BLACKLIST = List.of();

	public static Map<String, List<Block>> DRAGON_SPEEDUP_BLOCKS;
	public static Map<String, List<Block>> DRAGON_BREATH_BLOCKS;
	public static Map<String, List<Block>> DRAGON_MANA_BLOCKS;

	public static HashMap<String, List<Material>> DRAGON_SPEED_MATERIALS = new HashMap<>();

	static {
		//Which materials should speed up dragons when using the athletics ability
		DRAGON_SPEED_MATERIALS.put(DragonTypes.CAVE.getTypeName(), List.of(Material.STONE, Material.METAL, Material.LAVA, Material.FIRE));
		DRAGON_SPEED_MATERIALS.put(DragonTypes.FOREST.getTypeName(), List.of(Material.GRASS, Material.LEAVES, Material.WOOD, Material.PLANT, Material.CACTUS));
		DRAGON_SPEED_MATERIALS.put(DragonTypes.SEA.getTypeName(), List.of(Material.SAND, Material.WATER, Material.WATER_PLANT, Material.SPONGE, Material.TOP_SNOW, Material.SNOW, Material.POWDER_SNOW));
	}

	@SubscribeEvent
	public static void onConfigLoad(final ModConfigEvent event) {
		if (event.getConfig().getSpec() == ConfigHandler.serverSpec) {
			DragonSurvivalMod.LOGGER.info("Rebuilding configuration...");

			rebuildSpeedupBlocksMap();
			rebuildSeaDragonConfigs();
			rebuildBreathBlocks();
			rebuildManaBlocks();
			rebuildForestDragonConfigs();
		}
	}

	public static void rebuildSpeedupBlocksMap(){
		HashMap<String, List<Block>> speedupMap = new HashMap<>();
		speedupMap.put(DragonTypes.CAVE.getTypeName(), ConfigHandler.getResourceElements(Block.class, ServerConfig.caveSpeedupBlocks));
		speedupMap.put(DragonTypes.FOREST.getTypeName(), ConfigHandler.getResourceElements(Block.class, ServerConfig.forestSpeedupBlocks));
		speedupMap.put(DragonTypes.SEA.getTypeName(), ConfigHandler.getResourceElements(Block.class, ServerConfig.seaSpeedupBlocks));
		DRAGON_SPEEDUP_BLOCKS = speedupMap;
	}

	public static void rebuildBreathBlocks(){
		HashMap<String, List<Block>> breathMap = new HashMap<>();
		breathMap.put(DragonTypes.CAVE.getTypeName(), ConfigHandler.getResourceElements(Block.class, NetherBreathAbility.fireBreathBlockBreaks));
		breathMap.put(DragonTypes.FOREST.getTypeName(), ConfigHandler.getResourceElements(Block.class, ForestBreathAbility.forestBreathBlockBreaks));
		breathMap.put(DragonTypes.SEA.getTypeName(), ConfigHandler.getResourceElements(Block.class, StormBreathAbility.stormBreathBlockBreaks));
		DRAGON_BREATH_BLOCKS = breathMap;
	}

	public static void rebuildManaBlocks(){
		HashMap<String, List<Block>> map = new HashMap<>();
		map.put(DragonTypes.CAVE.getTypeName(), ConfigHandler.getResourceElements(Block.class, ServerConfig.caveDragonManaBlocks));
		map.put(DragonTypes.FOREST.getTypeName(), ConfigHandler.getResourceElements(Block.class, ServerConfig.forestDragonManaBlocks));
		map.put(DragonTypes.SEA.getTypeName(), ConfigHandler.getResourceElements(Block.class, ServerConfig.seaDragonManaBlocks));
		DRAGON_MANA_BLOCKS = map;
	}

	public static void rebuildSeaDragonConfigs(){
		SEA_DRAGON_HYDRATION_BLOCKS = ConfigHandler.getResourceElements(Block.class, ServerConfig.seaHydrationBlocks);
		SEA_DRAGON_HYDRATION_USE_ALTERNATIVES = ConfigHandler.getResourceElements(Item.class, ServerConfig.seaAdditionalWaterUseables);
	}

	public static void rebuildForestDragonConfigs(){
		FOREST_DRAGON_BREATH_GROW_BLACKLIST = ConfigHandler.getResourceElements(Block.class, ForestBreathAbility.forestBreathGrowBlacklist);
	}
}