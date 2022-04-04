package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Arrays;
import java.util.List;

@EventBusSubscriber
public class LootTableInject{
	private static final String[] files = new String[]{"bastion_bridge",
	                                                   "bastion_hoglin_stable",
	                                                   "bastion_other",
	                                                   "bastion_treasure",
	                                                   "buried_treasure",
	                                                   "desert_pyramid",
	                                                   "end_city_treasure",
	                                                   "igloo_chest",
	                                                   "injected_loot",
	                                                   "jungle_temple",
	                                                   "nether_bridge",
	                                                   "pillager_outpost",
	                                                   "ruined_portal",
	                                                   "shipwreck_map",
	                                                   "shipwreck_supply",
	                                                   "shipwreck_treasure",
	                                                   "simple_dungeon",
	                                                   "stronghold_corridor",
	                                                   "stronghold_crossing",
	                                                   "stronghold_library",
	                                                   "underwater_ruin_big",
	                                                   "underwater_ruin_small",
	                                                   "woodland_mansion"};

	@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent evt){
		String prefix = "minecraft:chests/";
		String name = evt.getName().toString();

		List<String> ls = Arrays.asList(files);

		if(name.startsWith(prefix)){
			String file = name.substring(name.indexOf(prefix) + prefix.length());
			if(ls.contains(file)){
				evt.getTable().addPool(getInjectPool("dust_and_bones"));
			}
		}
	}

	public static LootPool getInjectPool(String entryName){
		return LootPool.lootPool().add(getInjectEntry(entryName, 1)).setRolls(RandomValueRange.between(0, 1)).build();
	}

	private static LootEntry.Builder<?> getInjectEntry(String name, int weight){
		ResourceLocation table = new ResourceLocation(DragonSurvivalMod.MODID, "inject/" + name);
		return TableLootEntry.lootTableReference(table).setWeight(weight);
	}
}