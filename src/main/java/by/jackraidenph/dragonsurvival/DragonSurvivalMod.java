package by.jackraidenph.dragonsurvival;

import by.jackraidenph.dragonsurvival.capability.Capabilities;
import by.jackraidenph.dragonsurvival.commands.DragonCommand;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.emotes.EmoteRegistry;
import by.jackraidenph.dragonsurvival.handlers.DragonFoodHandler;
import by.jackraidenph.dragonsurvival.handlers.Magic.ClawToolHandler.Event_busHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.handlers.SpecificsHandler;
import by.jackraidenph.dragonsurvival.handlers.WingObtainmentController;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import by.jackraidenph.dragonsurvival.registration.ParticleRegistry;
import by.jackraidenph.dragonsurvival.util.BiomeDictionaryHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
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
    
    public DragonSurvivalMod() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHandler.serverSpec);

        ParticleRegistry.REGISTRY.register(modEventBus);
    
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EmoteRegistry());
        MinecraftForge.EVENT_BUS.register(new DragonFoodHandler());
        MinecraftForge.EVENT_BUS.register(new SpecificsHandler());
        MinecraftForge.EVENT_BUS.register(new Event_busHandler());
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::biomeLoadingEvent);
        MinecraftForge.EVENT_BUS.addListener(this::serverRegisterCommandsEvent);
    }
    
    private void setup(final FMLCommonSetupEvent event) {
    	WingObtainmentController.loadDragonPhrases();
        Capabilities.register();
        LOGGER.info("Successfully registered capabilities!");
        DragonAbilities.initAbilities();
        NetworkHandler.setup();
        LOGGER.info("Successfully registered packets!");
    }

    @SubscribeEvent
    public void biomeLoadingEvent(BiomeLoadingEvent event) {
        if (ConfigHandler.COMMON.predatorSpawnWeight.get() > 0) {
            List<BiomeDictionary.Type> includeList = Arrays.asList(BiomeDictionaryHelper.toBiomeTypeArray(ConfigHandler.COMMON.predatorBiomesInclude.get()));
            List<BiomeDictionary.Type> excludeList = Arrays.asList(BiomeDictionaryHelper.toBiomeTypeArray(ConfigHandler.COMMON.predatorBiomesExclude.get()));
            List<MobSpawnInfo.Spawners> spawns = event.getSpawns().getSpawner(EntityClassification.MONSTER);
            ResourceLocation biomeName = event.getName();
            if (biomeName == null) return;
            RegistryKey<Biome> biome = RegistryKey.create(ForgeRegistries.Keys.BIOMES, biomeName);
            Set<BiomeDictionary.Type> biomeTypes = BiomeDictionary.getTypes(biome);
            if (spawns.stream().anyMatch(x -> x.type.getCategory() == EntityClassification.MONSTER)
                    && biomeTypes.stream().anyMatch(x -> includeList.contains(x)
                    && biomeTypes.stream().noneMatch(excludeList::contains))) {
                spawns.add(new MobSpawnInfo.Spawners(EntityTypesInit.MAGICAL_BEAST, ConfigHandler.COMMON.predatorSpawnWeight.get(), ConfigHandler.COMMON.minPredatorSpawn.get(), ConfigHandler.COMMON.maxPredatorSpawn.get()));
            }
        }
    }

    @SubscribeEvent
    public void serverRegisterCommandsEvent(RegisterCommandsEvent event) {
    	CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
        DragonCommand.register(commandDispatcher);
        LOGGER.info("Registered commands");
    }
}
