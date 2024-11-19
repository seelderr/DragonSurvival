package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SavedSkinPresets;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.gson.Gson;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DragonEditorRegistry {
    private static final String SAVED_CUSTOMIZATIONS = "saved_customizations.json";
    private static final int MAX_SAVE_SLOTS = 9;

    public static File savedFile;

    private static boolean hasInitialized;
    private static SavedSkinPresets savedCustomizations;

    public static SavedSkinPresets getSavedCustomizations(@Nullable final HolderLookup.Provider provider) {
        if (!hasInitialized) {
            loadSavedCustomizations(provider);
        }

        return savedCustomizations;
    }

    @SubscribeEvent
    public static void initializeSavedCustomizations(final EntityJoinLevelEvent event) {
        loadSavedCustomizations(event.getLevel().registryAccess());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") // ignore
    private static void loadSavedCustomizations(@Nullable final HolderLookup.Provider provider) {
        if (hasInitialized) {
            return;
        }

        File directory = new File(FMLPaths.GAMEDIR.get().toFile(), "dragon-survival");
        savedFile = new File(directory, SAVED_CUSTOMIZATIONS);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (savedFile.exists()) {
            try {
                Gson gson = GsonFactory.getDefault();
                InputStream input = new FileInputStream(savedFile);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                    DragonEditorRegistry.savedCustomizations = gson.fromJson(reader, SavedSkinPresets.class);
                    SkinPortingSystem.upgrade(DragonEditorRegistry.savedCustomizations);
                } catch (IOException exception) {
                    DragonSurvival.LOGGER.warn("Reader could not be closed", exception);
                }
            } catch (FileNotFoundException exception) {
                DragonSurvival.LOGGER.error("Saved customization [{}] could not be found", savedFile.getName(), exception);
            }
        } else {
            try {
                savedFile.createNewFile();
                Gson gson = GsonFactory.newBuilder().setPrettyPrinting().create();
                DragonEditorRegistry.savedCustomizations = new SavedSkinPresets();

                for (String dragonType : DragonTypes.getTypes()) {
                    String typeKey = dragonType.toUpperCase(Locale.ENGLISH);
                    DragonEditorRegistry.savedCustomizations.skinPresets.computeIfAbsent(typeKey, key -> new HashMap<>());
                    DragonEditorRegistry.savedCustomizations.current.computeIfAbsent(typeKey, key -> new HashMap<>());

                    for (int slot = 0; slot < MAX_SAVE_SLOTS; slot++) {
                        DragonEditorRegistry.savedCustomizations.skinPresets.get(typeKey).computeIfAbsent(slot, key -> {
                            SkinPreset preset = new SkinPreset();
                            preset.initDefaults(DragonTypes.getStatic(typeKey));
                            return preset;
                        });
                    }

                    for (ResourceKey<DragonLevel> level : DragonLevel.keys(provider)) {
                        DragonEditorRegistry.savedCustomizations.current.get(typeKey).put(level.location().toString(), 0);
                    }
                }

                FileWriter writer = new FileWriter(savedFile);
                gson.toJson(DragonEditorRegistry.savedCustomizations, writer);

                writer.close();
            } catch (IOException exception) {
                DragonSurvival.LOGGER.error(exception);
            }
        }

        hasInitialized = true;
    }
}