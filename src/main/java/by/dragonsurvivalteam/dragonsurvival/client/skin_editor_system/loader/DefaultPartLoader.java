package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DefaultPartLoader extends SimpleJsonResourceReloadListener {
    public static final Map</* Dragon type */ String, Map</* Dragon level location */ String, HashMap<EnumSkinLayer, /* Dragon part key */ String>>> DEFAULT_PARTS = new HashMap<>();
    public static final String DEFAULT_PART = "none";
    private static final String DIRECTORY = "skin/default_parts";

    public DefaultPartLoader() {
        super(new Gson(), DIRECTORY);
    }

    @Override
    protected void apply(final @NotNull Map<ResourceLocation, JsonElement> map, @NotNull final ResourceManager manager, @NotNull final ProfilerFiller profiler) {
        DEFAULT_PARTS.put(DragonTypes.CAVE.getTypeNameLowerCase(), new HashMap<>());
        DEFAULT_PARTS.put(DragonTypes.FOREST.getTypeNameLowerCase(), new HashMap<>());
        DEFAULT_PARTS.put(DragonTypes.SEA.getTypeNameLowerCase(), new HashMap<>());

        map.forEach((location, value) -> {
            // Location path is without the specified directory
            String dragonType = location.getPath();
            JsonObject dragonLevelMap = value.getAsJsonObject();

            for (String dragonLevel : dragonLevelMap.keySet()) {
                JsonObject partMap = dragonLevelMap.get(dragonLevel).getAsJsonObject();

                for (String part : partMap.keySet()) {
                    DEFAULT_PARTS.get(dragonType).computeIfAbsent(dragonLevel, key -> new HashMap<>()).put(EnumSkinLayer.valueOf(part.toUpperCase(Locale.ENGLISH)), partMap.get(part).getAsString());
                }
            }
        });
    }

    public static String getDefaultPartKey(final AbstractDragonType type, final ResourceKey<DragonLevel> dragonLevel, final EnumSkinLayer layer) {
        HashMap<EnumSkinLayer, String> partMap = DEFAULT_PARTS.get(type.getTypeNameLowerCase()).get(dragonLevel.location().toString());
        return partMap != null ? partMap.getOrDefault(layer, DEFAULT_PART) : DEFAULT_PART;
    }
}
