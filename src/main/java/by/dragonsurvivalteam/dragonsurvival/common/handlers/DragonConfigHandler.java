package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DragonConfigHandler{
	public static List<Block> SEA_DRAGON_HYDRATION_BLOCKS;
	public static List<Item> SEA_DRAGON_HYDRATION_USE_ALTERNATIVES;

	public static List<Block> FOREST_DRAGON_BREATH_GROW_BLACKLIST;

	public static Map<DragonType, List<Block>> DRAGON_SPEEDUP_BLOCKS;
	public static Map<DragonType, List<Block>> DRAGON_BREATH_BLOCKS;
	public static Map<DragonType, List<Block>> DRAGON_MANA_BLOCKS;

	@SubscribeEvent
	public static void onConfigLoad(ModConfigEvent.Loading event){
		if(event.getConfig().getType() == Type.SERVER){
			rebuildSpeedupBlocksMap();
			rebuildSeaDragonConfigs();
			rebuildBreathBlocks();
			rebuildManaBlocks();
			rebuildForestDragonConfigs();
		}
	}

	private static void rebuildSpeedupBlocksMap(){
		HashMap<DragonType, List<Block>> speedupMap = new HashMap<>();
		speedupMap.put(DragonType.CAVE, ServerConfig.caveSpeedupBlocks);
		speedupMap.put(DragonType.FOREST, ServerConfig.forestSpeedupBlocks);
		speedupMap.put(DragonType.SEA, ServerConfig.seaSpeedupBlocks);
		DRAGON_SPEEDUP_BLOCKS = speedupMap;
	}

	public static void rebuildBreathBlocks(){
		HashMap<DragonType, List<Block>> breathMap = new HashMap<>();
		breathMap.put(DragonType.CAVE, ServerConfig.fireBreathBlockBreaks);
		breathMap.put(DragonType.FOREST, ServerConfig.forestBreathBlockBreaks);
		breathMap.put(DragonType.SEA, ServerConfig.stormBreathBlockBreaks);
		DRAGON_BREATH_BLOCKS = breathMap;
	}

	public static void rebuildManaBlocks(){
		HashMap<DragonType, List<Block>> map = new HashMap<>();
		map.put(DragonType.CAVE, ServerConfig.caveDragonManaBlocks);
		map.put(DragonType.FOREST, ServerConfig.forestDragonManaBlocks);
		map.put(DragonType.SEA, ServerConfig.seaDragonManaBlocks);
		DRAGON_MANA_BLOCKS = map;
	}

	private static void rebuildSeaDragonConfigs(){
		SEA_DRAGON_HYDRATION_BLOCKS = ServerConfig.seaHydrationBlocks;
		SEA_DRAGON_HYDRATION_USE_ALTERNATIVES = ServerConfig.seaAdditionalWaterUseables;
	}

	private static void rebuildForestDragonConfigs(){
		FOREST_DRAGON_BREATH_GROW_BLACKLIST = ServerConfig.forestBreathGrowBlacklist;
	}
}