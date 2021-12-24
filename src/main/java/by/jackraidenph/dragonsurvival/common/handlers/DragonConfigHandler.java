package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.config.ConfigUtils;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DragonConfigHandler
{
	public static Map<DragonType, List<Block>> DRAGON_SPEEDUP_BLOCKS;
	public static List<Block> SEA_DRAGON_HYDRATION_BLOCKS;
	public static List<Item> SEA_DRAGON_HYDRATION_USE_ALTERNATIVES;
	public static Map<DragonType, List<Block>> DRAGON_BREATH_BLOCKS;
	
	@SubscribeEvent
	public static void onConfigLoad(ModConfig.Loading event) {
		if (event.getConfig().getType() == Type.SERVER) {
			rebuildSpeedupBlocksMap();
			rebuildSeaHydrationLists();
			rebuildBreathBlocks();
		}
	}
	
	private static void rebuildSpeedupBlocksMap() {
		HashMap<DragonType, List<Block>> speedupMap = new HashMap<>();
		speedupMap.put(DragonType.CAVE, buildDragonSpeedupMap(DragonType.CAVE));
		speedupMap.put(DragonType.FOREST, buildDragonSpeedupMap(DragonType.FOREST));
		speedupMap.put(DragonType.SEA, buildDragonSpeedupMap(DragonType.SEA));
		DRAGON_SPEEDUP_BLOCKS = speedupMap;
	}
	
	public static void rebuildBreathBlocks() {
		HashMap<DragonType, List<Block>> breathMap = new HashMap<>();
		breathMap.put(DragonType.CAVE, buildDragonBreathBlocks(DragonType.CAVE));
		breathMap.put(DragonType.FOREST, buildDragonBreathBlocks(DragonType.FOREST));
		breathMap.put(DragonType.SEA, buildDragonBreathBlocks(DragonType.SEA));
		DRAGON_BREATH_BLOCKS = breathMap;
	}
	
	private static void rebuildSeaHydrationLists() {
		SEA_DRAGON_HYDRATION_BLOCKS = ConfigUtils.parseConfigBlockList(ConfigHandler.SERVER.seaHydrationBlocks.get());
		SEA_DRAGON_HYDRATION_USE_ALTERNATIVES = ConfigUtils.parseConfigItemList(ConfigHandler.SERVER.seaAdditionalWaterUseables.get());
	}
	
	private static List<Block> buildDragonSpeedupMap(DragonType type) {
		List<? extends String> configSpeedups = new ArrayList<>();
		switch (type) {
			case CAVE:
				configSpeedups = ConfigHandler.SERVER.caveSpeedupBlocks.get();
				break;
			case FOREST:
				configSpeedups = ConfigHandler.SERVER.forestSpeedupBlocks.get();
				break;
			case SEA:
				configSpeedups = ConfigHandler.SERVER.seaSpeedupBlocks.get();
				break;
		}
		
		return ConfigUtils.parseConfigBlockList(configSpeedups);
	}
	
	private static List<Block> buildDragonBreathBlocks(DragonType type) {
		List<? extends String> configBlocks = new ArrayList<>();
		switch (type) {
			case CAVE:
				configBlocks = ConfigHandler.SERVER.fireBreathBlockBreaks.get();
				break;
			case FOREST:
				configBlocks = ConfigHandler.SERVER.forestBreathBlockBreaks.get();
				break;
			case SEA:
				configBlocks = ConfigHandler.SERVER.stormBreathBlockBreaks.get();
				break;
		}
		return ConfigUtils.parseConfigBlockList(configBlocks);
	}
}
