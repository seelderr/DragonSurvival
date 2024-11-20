package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader.DefaultPartLoader;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader.DragonPartLoader;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonLevelCustomization;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonPart;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SavedSkinPresets;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SkinPortingSystem {
    public static void upgrade(SavedSkinPresets presets) {
        if (presets.version == 0) {
            upgrade0to3(presets);
        }
    }

    public static void upgrade0to3(SavedSkinPresets presets) {
        for (String type : presets.skinPresets.keySet()) {
            for (int saveSlot : presets.skinPresets.get(type).keySet()) {
                for (ResourceKey<DragonLevel> dragonLevel : DragonLevel.keys(null)) {
                    Lazy<DragonLevelCustomization> lazy = presets.skinPresets.get(type).get(saveSlot).get(dragonLevel);

                    if (lazy == null) {
                        lazy = Lazy.of(() -> new DragonLevelCustomization(dragonLevel));
                    }

                    DragonLevelCustomization customization = lazy.get();

                    for (EnumSkinLayer layer : customization.settings.keySet()) {
                        Map<EnumSkinLayer, List<DragonPart>> partMap = DragonPartLoader.DRAGON_PARTS.get(type.toLowerCase(Locale.ENGLISH));

                        // Convert the numbered 'EXTRA' layer to the generic 'EXTRA' layer
                        EnumSkinLayer actualLayer = EnumSkinLayer.valueOf(layer.getNameUpperCase());
                        List<DragonPart> parts = partMap.get(actualLayer);

                        if (parts != null) {
                            String partKey = DefaultPartLoader.getDefaultPartKey(DragonTypes.getStatic(type), dragonLevel, layer);
                            LayerSettings settings = customization.settings.get(layer).get();

                            for (DragonPart part : parts) {
                                if (part.key().equals(partKey)) {
                                    settings.hue = settings.isColorModified ? part.averageHue() - settings.hue - 0.5f : part.averageHue();

                                    if (settings.hue > 1) {
                                        settings.hue -= 1;
                                    } else if (settings.hue < 0) {
                                        settings.hue += 1;
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        presets.version = 3;
    }
}
