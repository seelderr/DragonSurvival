package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.emotes.Emote;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DSEmotes {
    public static final ArrayList<Emote> EMOTES = new ArrayList<>();
    private static final ResourceLocation DS_CLIENT_EMOTES = ResourceLocation.fromNamespaceAndPath(MODID, "emotes.json");

    @SubscribeEvent
    public static void clientStart(FMLClientSetupEvent event) {
        DSEmotes.reload(Minecraft.getInstance().getResourceManager());

        if (Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager) {
            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener((ResourceManagerReloadListener) manager -> {
                DSEmotes.EMOTES.clear();
                DSEmotes.reload(Minecraft.getInstance().getResourceManager());
            });
        }
    }

    protected static void reload(ResourceManager manager) {
        try {
            Gson gson = GsonFactory.getDefault();
            Resource resource = manager.getResource(DSEmotes.DS_CLIENT_EMOTES).orElse(null);
            if (resource == null)
                throw new RuntimeException(String.format("Resource '%s' not found!", DSEmotes.DS_CLIENT_EMOTES.getPath()));
            InputStream in = resource.open();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                EmoteDataObject dataObject = gson.fromJson(reader, EmoteDataObject.class);

                if (dataObject != null) {
                    List<Emote> emotes = Arrays.asList(dataObject.emotes);
                    HashMap<String, Integer> nameCount = new HashMap<>();

                    for (Emote emote : emotes) {
                        nameCount.putIfAbsent(emote.name, 0);
                        nameCount.put(emote.name, nameCount.get(emote.name) + 1);
                        emote.id = emote.name + "_" + nameCount.get(emote.name);
                    }

                    EMOTES.addAll(emotes);
                }
            } catch (IOException exception) {
                DragonSurvival.LOGGER.warn("Reader could not be closed", exception);
            }
        } catch (IOException exception) {
            DragonSurvival.LOGGER.error("Resource [" + DSEmotes.DS_CLIENT_EMOTES + "] could not be opened", exception);
        }
    }

    public static class EmoteDataObject {
        public Emote[] emotes;
    }
}