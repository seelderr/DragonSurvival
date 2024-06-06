// FIXME: Reimplement loot tables once we get compiling
/*package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber
public class LootTableInject{
	private static final String injectFile = "dragon_loots";
	private static final List<String> files = List.of("bastion_bridge",
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
	                                                   "woodland_mansion",
	                                                   "village/village_armorer",
	                                                   "village/village_butcher",
			                                           "village/village_cartographer",
			                                           "village/village_desert_house",
			                                           "village/village_fisher",
			                                           "village/village_fletcher",
			                                           "village/village_mason",
			                                           "village/village_plains_house",
			                                           "village/village_savanna_house",
			                                           "village/village_shepherd",
			                                           "village/village_snowy_house",
			                                           "village/village_taiga_house",
			                                           "village/village_tannery",
			                                           "village/village_temple",
			                                           "village/village_toolsmith",
			                                           "village/village_weaponsmith");

	@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent evt){
		String prefix = "minecraft:chests/";
		String name = evt.getName().toString();

		if(name.startsWith(prefix)){
			String file = name.substring(name.indexOf(prefix) + prefix.length());
			if(files.contains(file)) evt.getTable().addPool(getInjectPool(injectFile));
		}
	}

	public static LootPool getInjectPool(String entryName){
		return LootPool.lootPool().add(getInjectEntry(entryName, 1)).setBonusRolls(UniformGenerator.between(0, 1)).build();
	}

	private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name, int weight){
		ResourceLocation table = new ResourceLocation(DragonSurvivalMod.MODID, "inject/" + name);
		return NestedLootTable.lootTableReference(table).setWeight(weight);
	}
}*/