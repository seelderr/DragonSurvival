package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeDataGen {
	@SubscribeEvent
	public static void configureDataGen(final GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		// Client
		generator.addProvider(event.includeClient(), new DataBlockStateProvider(generator.getPackOutput(), DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeClient(), new DataItemModelProvider(generator.getPackOutput(), DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeClient(), new DataSpriteSourceProvider(generator.getPackOutput(), existingFileHelper, DragonSurvivalMod.MODID));

		// Server
		generator.addProvider(event.includeServer(), new DSRegistryProvider(generator.getPackOutput(), event.getLookupProvider()));

		Set<ResourceLocation> blocks = DSBlocks.DS_BLOCKS.keySet().stream().map(key -> new ResourceLocation(DragonSurvivalMod.MODID, "blocks/" + key)).collect(Collectors.toSet());
		generator.addProvider(event.includeServer(), new DataLootTableProvider(generator.getPackOutput(), blocks, List.of(new LootTableProvider.SubProviderEntry(BlockLootTableSubProvider::new, LootContextParamSets.BLOCK))));

		BlockTagsProvider blockTagsProvider = new DataBlockTagProvider(generator.getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID, existingFileHelper);
		generator.addProvider(event.includeServer(), blockTagsProvider);

		generator.addProvider(event.includeServer(), new DataItemTagProvider(generator.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter(), DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeServer(), new DataDamageTypeTagsProvider(generator.getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID, existingFileHelper));
	}
}