package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.data.loot.DragonHeartLootModifier;
import by.dragonsurvivalteam.dragonsurvival.data.loot.DragonHeartLootModifierSerializer;
import by.dragonsurvivalteam.dragonsurvival.data.loot.DragonOreLootModifier;
import by.dragonsurvivalteam.dragonsurvival.data.loot.DragonOreLootModifierSerializer;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
		// TODO: This might not work.
		Set<ResourceLocation> blocks = DSBlocks.DS_BLOCKS.getEntries().stream().map(key -> new ResourceLocation(DragonSurvivalMod.MODID, "blocks/" + key.getId())).collect(Collectors.toSet());
		generator.addProvider(event.includeServer(), new DataLootTableProvider(generator.getPackOutput(), blocks, List.of(new LootTableProvider.SubProviderEntry(BlockLootTableSubProvider::new, LootContextParamSets.BLOCK))));

		BlockTagsProvider blockTagsProvider = new DataBlockTagProvider(generator.getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID, existingFileHelper);
		generator.addProvider(event.includeServer(), blockTagsProvider);

		generator.addProvider(event.includeServer(), new DataItemTagProvider(generator.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter(), DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(event.includeServer(), new DataDamageTypeTagsProvider(generator.getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID, existingFileHelper));
		event.getGenerator().addProvider(event.includeServer(), new DragonOreLootModifierSerializer(event.getGenerator().getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID));
		event.getGenerator().addProvider(event.includeServer(), new DragonHeartLootModifierSerializer(event.getGenerator().getPackOutput(), event.getLookupProvider(), DragonSurvivalMod.MODID));
	}
}