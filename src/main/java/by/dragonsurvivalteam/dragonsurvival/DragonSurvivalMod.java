package by.dragonsurvivalteam.dragonsurvival;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks.DS_BLOCKS;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSContainers.DS_CONTAINERS;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSCreativeTabs.DS_CREATIVE_MODE_TABS;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSCreativeTabs.DS_TAB;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes.DS_DAMAGE_TYPES;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSEffects.DS_MOB_EFFECTS;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSEntities.ENTITY_TYPES;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSParticles.DS_PARTICLES;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSSounds.DS_SOUNDS;
import static by.dragonsurvivalteam.dragonsurvival.registry.DSTileEntities.DS_TILE_ENTITIES;

import by.dragonsurvivalteam.dragonsurvival.api.appleskin.AppleSkinEventHandler;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonAltarCommand;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonEditorCommand;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonSizeCommand;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.WingObtainmentController;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLibClient;

@Mod( DragonSurvivalMod.MODID )
public class DragonSurvivalMod{
	public static final String MODID = "dragonsurvival";
	public static final Logger LOGGER = LogManager.getLogger("Dragon Survival");
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MODID, name);
    }

	public DragonSurvivalMod(IEventBus modEventBus, ModContainer modContainer){
		GeckoLibClient.init();
		DragonTypes.registerTypes();
		DragonBodies.registerBodies();

		ConfigHandler.initConfig();
		DragonAbilities.initAbilities();

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::clientSetup);

		DS_MOB_EFFECTS.register(modEventBus);
		DS_BLOCKS.register(modEventBus);
		DS_CONTAINERS.register(modEventBus);
		DS_CREATIVE_MODE_TABS.register(modEventBus);
		DS_PARTICLES.register(modEventBus);
		DS_DAMAGE_TYPES.register(modEventBus);
		DS_SOUNDS.register(modEventBus);
		DS_TILE_ENTITIES.register(modEventBus);
		ENTITY_TYPES.register(modEventBus);
		GLM.register(modEventBus);
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		WingObtainmentController.loadDragonPhrases();
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		if (ModList.get().isLoaded("appleskin")) {
			NeoForge.EVENT_BUS.register(new AppleSkinEventHandler());
		}
	}

	@SubscribeEvent
	public void serverRegisterCommandsEvent(RegisterCommandsEvent event){
		CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
		DragonCommand.register(commandDispatcher);
		DragonEditorCommand.register(commandDispatcher);
		DragonAltarCommand.register(commandDispatcher);
		DragonSizeCommand.register(commandDispatcher);
		LOGGER.info("Registered commands");
	}
	
	@SubscribeEvent
	public void addPackFinders(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.CLIENT_RESOURCES) {
			HashMap<MutableComponent, String> resourcePacks = new HashMap<MutableComponent, String>();
			//resourcePacks.put(Component.literal("- Dragon East"), "ds_east");
			//resourcePacks.put(Component.literal("- Dragon North"), "ds_north");
			//resourcePacks.put(Component.literal("- Dragon South"), "ds_south");
			//resourcePacks.put(Component.literal("- Dragon West"), "ds_west");
			resourcePacks.put(Component.literal("- Old Magic Icons for DS"), "ds_old_magic");
			resourcePacks.put(Component.literal("- Dark GUI for DS"), "ds_dark_gui");
			for (Map.Entry<MutableComponent, String> entry : resourcePacks.entrySet()) {
				registerBuiltinResourcePack(event, entry.getKey(), entry.getValue());
			}
		}
	}

	private static void registerBuiltinResourcePack(AddPackFindersEvent event, MutableComponent name, String folder) {
		LOGGER.info("Registering " + name);
		/*try(PathPackResources pack = new PathPackResources(path, true, file.findResource("resourcepacks/" + folder));) {
			
		} catch (IOException e) {
			if (!DatagenModLoader.isRunningDataGen())
				e.printStackTrace();
			
			
		}*/
		
		/*event.addRepositorySource((constructor) -> {
			String path = res(folder).toString();
			IModFile file = ModList.get().getModFileById(MODID).getFile();
			try(PathPackResources pack = new PathPackResources(path, true, file.findResource("resourcepacks/" + folder));) {
				constructor.create(
					res(folder).toString(),
					name,
					false,
					() -> pack,
					pack.getMetadataSection(PackMetadataSection.TYPE),
					Pack.Position.TOP,
					PackSource.BUILT_IN,
					false);
			};
		});*/ // -> {
			/*String path = res(folder).toString();
			IModFile file = ModList.get().getModFileById(MODID).getFile();
			try(PathPackResources pack = new PathPackResources(path, true, file.findResource("resourcepacks/" + folder));) {
				consumer.accept(constructor.create(
					res(folder).toString(),
					name,
					false,
					() -> pack,
					pack.getMetadataSection(PackMetadataSection.SERIALIZER),
					Pack.Position.TOP,
					PackSource.BUILT_IN,
					false));
			} catch (IOException e) {
				if (!DatagenModLoader.isRunningDataGen())
					e.printStackTrace();
			}
		});*/
	}
}