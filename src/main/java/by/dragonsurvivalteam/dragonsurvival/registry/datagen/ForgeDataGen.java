package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot.DragonHeartLootModifier;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot.DragonHeartLootModifierSerializer;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot.DragonOreLootModifier;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot.DragonOreLootModifierSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ForgeDataGen {
	private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<DragonOreLootModifier>> DRAGON_ORE = DragonSurvivalMod.GLM.register("dragon_ore", DragonOreLootModifier.CODEC);
	private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<DragonHeartLootModifier>> DRAGON_HEART = DragonSurvivalMod.GLM.register("dragon_heart", DragonHeartLootModifier.CODEC);

	@SubscribeEvent
	public static void dataGen(final GatherDataEvent event) {
		final DataGenerator generator = event.getGenerator();
		final PackOutput packOutput = generator.getPackOutput();
		final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		// Client

		generator.addProvider(event.includeClient(), new DataBlockStateProvider(packOutput, DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeClient(), new DataItemModelProvider(packOutput, DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeClient(), new DataSpriteSourceProvider(packOutput, lookupProvider, DragonSurvivalMod.MODID, existingFileHelper));

		// Server
		// FIXME: We need a Set<ResourceKey<LootTable>> pRequiredTables and I don't know how to get it
		//Set<ResourceLocation> blocks = DSBlocks.DS_BLOCKS.getEntries().stream().map(key -> ResourceLocation.fromNamespaceAndPath(MODID, "blocks/" + key.getId())).collect(Collectors.toSet());
		//generator.addProvider(event.includeServer(), new DataLootTableProvider(generator.getPackOutput(), blocks, List.of(new LootTableProvider.SubProviderEntry(BlockLootTableSubProvider::new, LootContextParamSets.BLOCK))));

		BlockTagsProvider blockTagsProvider = new DataBlockTagProvider(packOutput, lookupProvider, DragonSurvivalMod.MODID, existingFileHelper);
		generator.addProvider(event.includeServer(), blockTagsProvider);

		generator.addProvider(event.includeServer(), new DataItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeServer(), new DataDamageTypeTagsProvider(packOutput, lookupProvider, DragonSurvivalMod.MODID, existingFileHelper));
	}
}