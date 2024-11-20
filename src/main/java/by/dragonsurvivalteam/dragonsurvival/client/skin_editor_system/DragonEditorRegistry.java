package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SavedSkinPresets;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.json.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import javax.annotation.Nullable;

@EventBusSubscriber(value = Dist.CLIENT)
public class DragonEditorRegistry {
    private static final String SAVED_CUSTOMIZATIONS = "saved_customizations.json";
    private static final int MAX_SAVE_SLOTS = 9;

    private static File savedFile;
    private static SavedSkinPresets savedCustomizations;
    private static boolean hasInitialized;

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

    // TODO :: Move away from GSON class parsing to have more control on which fields are stored and in which way
    @SuppressWarnings("ResultOfMethodCallIgnored") // ignore
    public static void loadSavedCustomizations(@Nullable final HolderLookup.Provider provider) {
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
                    savedCustomizations = gson.fromJson(reader, SavedSkinPresets.class);

                    if (savedCustomizations == null) {
                        throw new IOException("Customization file [" + SAVED_CUSTOMIZATIONS + "] file could not be read");
                    }

                    SkinPortingSystem.upgrade(savedCustomizations);
                    hasInitialized = true;
                    return;
                } catch (IOException | JsonSyntaxException exception) {
                    DragonSurvival.LOGGER.warn("An error occurred while processing the [" + SAVED_CUSTOMIZATIONS + "] file", exception);
                }
            } catch (FileNotFoundException exception) {
                DragonSurvival.LOGGER.error("Saved customization [{}] could not be found", savedFile.getName(), exception);
            }
        }

        // File either doesn't exist or is in a broken state

        try {
            if (savedFile.exists()) {
                savedFile.delete();
            }

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

        hasInitialized = true;
    }

    public static void save(final SavedSkinPresets savedCustomizations) {
        try {
            Gson gson = GsonFactory.newBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(DragonEditorRegistry.savedFile);
            gson.toJson(savedCustomizations, writer);
            writer.close();
        } catch (IOException exception) {
            DragonSurvival.LOGGER.error("An error occurred while trying to save the dragon skin", exception);
        }
    }
}