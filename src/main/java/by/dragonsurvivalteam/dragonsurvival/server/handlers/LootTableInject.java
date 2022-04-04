package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE )
public class LootTableInject{
	private static final String injectFile = "dust_and_bones";
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
	                                                   "woodland_mansion");

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
		return LootTableReference.lootTableReference(table).setWeight(weight);
	}
}