package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot.DragonHeartLootModifier;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot.DragonHeartLootModifierSerializer;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot.DragonOreLootModifier;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot.DragonOreLootModifierSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ForgeDataGen {
	private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<DragonOreLootModifier>> DRAGON_ORE = DragonSurvivalMod.GLM.register("dragon_ore", DragonOreLootModifier.CODEC);
	private static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<DragonHeartLootModifier>> DRAGON_HEART = DragonSurvivalMod.GLM.register("dragon_heart", DragonHeartLootModifier.CODEC);
	@SubscribeEvent
	public static void configureDataGen(final GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		// Client
		generator.addProvider(event.includeClient(), new DataBlockStateProvider(generator.getPackOutput(), DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeClient(), new DataItemModelProvider(generator.getPackOutput(), DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeClient(), new DataSpriteSourceProvider(generator.getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID, existingFileHelper));

		// Server
		// FIXME: We need a Set<ResourceKey<LootTable>> pRequiredTables and I don't know how to get it
		//Set<ResourceLocation> blocks = DSBlocks.DS_BLOCKS.getEntries().stream().map(key -> ResourceLocation.fromNamespaceAndPath(MODID, "blocks/" + key.getId())).collect(Collectors.toSet());
		//generator.addProvider(event.includeServer(), new DataLootTableProvider(generator.getPackOutput(), blocks, List.of(new LootTableProvider.SubProviderEntry(BlockLootTableSubProvider::new, LootContextParamSets.BLOCK))));

		BlockTagsProvider blockTagsProvider = new DataBlockTagProvider(generator.getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID, existingFileHelper);
		generator.addProvider(event.includeServer(), blockTagsProvider);

		generator.addProvider(event.includeServer(), new DataItemTagProvider(generator.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter(), DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeServer(), new DataDamageTypeTagsProvider(generator.getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID, existingFileHelper));
		event.getGenerator().addProvider(event.includeServer(), new DragonOreLootModifierSerializer(event.getGenerator().getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID));
		event.getGenerator().addProvider(event.includeServer(), new DragonHeartLootModifierSerializer(event.getGenerator().getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID));
	}
}