package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.emotes.Emote;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@EventBusSubscriber( bus = EventBusSubscriber.Bus.MOD )
public class DSEmotes {
	public static final ResourceLocation DS_CLIENT_EMOTES = ResourceLocation.fromNamespaceAndPath(MODID, "emotes.json");
	public static final ArrayList<Emote> EMOTES = new ArrayList<>();

	private static boolean hasStarted = false;

	@SubscribeEvent
	public static void clientStart(FMLClientSetupEvent event){
		if(FMLEnvironment.dist  == Dist.CLIENT) {
			DSEmotes.reload(Minecraft.getInstance().getResourceManager(), DSEmotes.DS_CLIENT_EMOTES);

			if(Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager){
				((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener((ResourceManagerReloadListener)manager -> {
					DSEmotes.EMOTES.clear();
					DSEmotes.reload(Minecraft.getInstance().getResourceManager(), DSEmotes.DS_CLIENT_EMOTES);
				});
			}
		}
	}

	protected static void reload(ResourceManager manager, ResourceLocation location){
		try{
			Gson gson = GsonFactory.getDefault();
			Resource resource = manager.getResource(location).orElse(null);
			if (resource == null)
				throw new RuntimeException(String.format("Resource '%s' not found!", location.getPath()));
			InputStream in = resource.open();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
				EmoteRegistryClass je = gson.fromJson(reader, EmoteRegistryClass.class);

				if (je != null) {
					List<Emote> emts = Arrays.asList(je.emotes);
					HashMap<String, Integer> nameCount = new HashMap<>();

					for (Emote emt : emts) {
						nameCount.putIfAbsent(emt.name, 0);
						nameCount.put(emt.name, nameCount.get(emt.name) + 1);
						emt.id = emt.name + "_" + nameCount.get(emt.name);
					}

					EMOTES.addAll(emts);
				}
			} catch (IOException exception) {
				DragonSurvivalMod.LOGGER.warn("Reader could not be closed", exception);
			}
		} catch (IOException exception) {
			DragonSurvivalMod.LOGGER.error("Resource [" + location + "] could not be opened", exception);
		}
	}

	@EventBusSubscriber( Dist.CLIENT )
	public static class clientStart{
		@OnlyIn( Dist.CLIENT )
		@SubscribeEvent
		public static void clientStart(EntityJoinLevelEvent event){
			if(!hasStarted){
				hasStarted = true;
			}
		}
	}

	public static class EmoteRegistryClass{
		public Emote[] emotes;
	}
}