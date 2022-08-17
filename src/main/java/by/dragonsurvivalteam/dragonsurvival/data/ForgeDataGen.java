package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeDataGen {
	@SubscribeEvent
	public static void configureDataGen(GatherDataEvent event){
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		BlockTagsProvider provider = new ForgeBlockTagsProvider(generator, existingFileHelper);

		generator.addProvider(new DataLanguageProvider(generator, DragonSurvivalMod.MODID, "en_us"));
		generator.addProvider(new DataSoundProvider(generator, DragonSurvivalMod.MODID, existingFileHelper));

		generator.addProvider(new DataRecipeProvider(generator));
		generator.addProvider(new DataLootTableProvider(generator, existingFileHelper));

		generator.addProvider(new DataItemTagProvider(generator, provider, DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(new DataBlockTagProvider(generator, DragonSurvivalMod.MODID, existingFileHelper));

		generator.addProvider(new DataBlockModelProvider(generator, DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(new DataItemModelProvider(generator, DragonSurvivalMod.MODID, existingFileHelper));
		generator.addProvider(new DataBlockStateProvider(generator, DragonSurvivalMod.MODID, existingFileHelper));
	}
}