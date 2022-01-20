package by.jackraidenph.dragonsurvival;

import by.jackraidenph.dragonsurvival.client.particles.DSParticles;
import by.jackraidenph.dragonsurvival.client.sounds.SoundRegistry;
import by.jackraidenph.dragonsurvival.commands.CustomizationCommand;
import by.jackraidenph.dragonsurvival.commands.DragonCommand;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.handlers.DragonFoodHandler;
import by.jackraidenph.dragonsurvival.common.handlers.WingObtainmentController;
import by.jackraidenph.dragonsurvival.common.handlers.magic.ClawToolHandler.Event_busHandler;
import by.jackraidenph.dragonsurvival.common.items.DragonSurvivalCreativeTab;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.util.BiomeDictionaryHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Mod(DragonSurvivalMod.MODID)
public class DragonSurvivalMod {
    public static final String MODID = "dragonsurvival";
    public static final Logger LOGGER = LogManager.getLogger("Dragon Survival");
    
	public static DragonSurvivalCreativeTab items = new DragonSurvivalCreativeTab("dragon.survival.blocks");
	
	public DragonSurvivalMod() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHandler.serverSpec);

        DSParticles.REGISTRY.register(modEventBus);
        SoundRegistry.SOUNDS.register(modEventBus);
        
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DragonFoodHandler());
        MinecraftForge.EVENT_BUS.register(new Event_busHandler());
        
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::biomeLoadingEvent);
        MinecraftForge.EVENT_BUS.addListener(this::serverRegisterCommandsEvent);
    }
    
    private void setup(final FMLCommonSetupEvent event) {
    	WingObtainmentController.loadDragonPhrases();
        DragonAbilities.initAbilities();
        NetworkHandler.setup();
        LOGGER.info("Successfully registered packets!");
    }

    @SubscribeEvent
    public void biomeLoadingEvent(BiomeLoadingEvent event) {
        if (ConfigHandler.COMMON.predatorSpawnWeight.get() > 0) {
            List<BiomeDictionary.Type> includeList = Arrays.asList(BiomeDictionaryHelper.toBiomeTypeArray(ConfigHandler.COMMON.predatorBiomesInclude.get()));
            List<BiomeDictionary.Type> excludeList = Arrays.asList(BiomeDictionaryHelper.toBiomeTypeArray(ConfigHandler.COMMON.predatorBiomesExclude.get()));
            List<MobSpawnSettings.SpawnerData> spawns = event.getSpawns().getSpawner(MobCategory.MONSTER);
            ResourceLocation biomeName = event.getName();
            if (biomeName == null) return;
            ResourceKey<Biome> biome = ResourceKey.create(ForgeRegistries.Keys.BIOMES, biomeName);
            Set<BiomeDictionary.Type> biomeTypes = BiomeDictionary.getTypes(biome);
            if (spawns.stream().anyMatch(x -> x.type.getCategory() == MobCategory.MONSTER)
                    && biomeTypes.stream().anyMatch(x -> includeList.contains(x)
                    && biomeTypes.stream().noneMatch(excludeList::contains))) {
                spawns.add(new SpawnerData(DSEntities.MAGICAL_BEAST, ConfigHandler.COMMON.predatorSpawnWeight.get(), ConfigHandler.COMMON.minPredatorSpawn.get(), ConfigHandler.COMMON.maxPredatorSpawn.get()));
            }
        }
    }

    @SubscribeEvent
    public void serverRegisterCommandsEvent(RegisterCommandsEvent event) {
    	CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        DragonCommand.register(commandDispatcher);
        CustomizationCommand.register(commandDispatcher);
        LOGGER.info("Registered commands");
    }
}
