package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NeoForgedDataGen {
	@SubscribeEvent
	public static void dataGen(final GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		// Client
		generator.addProvider(event.includeClient(), new DataBlockStateProvider(packOutput, existingFileHelper));
		generator.addProvider(event.includeClient(), new DataItemModelProvider(packOutput, existingFileHelper));
		generator.addProvider(event.includeClient(), new DataSpriteSourceProvider(packOutput, lookupProvider, existingFileHelper));

        // Server
        LootTableProvider.SubProviderEntry blockLootTableSubProvider = new LootTableProvider.SubProviderEntry(
                BlockLootTableSubProvider::new,
                LootContextParamSets.BLOCK);
        generator.addProvider(event.includeServer(), (DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(output, Collections.emptySet(), List.of(blockLootTableSubProvider), event.getLookupProvider()));

		DatapackBuiltinEntriesProvider datapackProvider = new DatapackBuiltinEntriesProvider(
				packOutput,
				lookupProvider,
				new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, DSDamageTypes::registerDamageTypes),
				Set.of(DragonSurvivalMod.MODID)
		);

		generator.addProvider(event.includeServer(), datapackProvider);

		// Update the lookup provider with our datapack entries
		lookupProvider = datapackProvider.getRegistryProvider();

		BlockTagsProvider blockTagsProvider = new DSBlockTags(packOutput, lookupProvider, existingFileHelper);
		generator.addProvider(event.includeServer(), blockTagsProvider);

		generator.addProvider(event.includeServer(), new DSItemTags(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
		generator.addProvider(event.includeServer(), new DSDamageTypeTags(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new DSEntityTypeTags(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new DSEffectTags(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new DSPoiTypeTags(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new DSEnchantmentTags(packOutput, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeServer(), new DataBlockModelProvider(packOutput, existingFileHelper));
	}
}