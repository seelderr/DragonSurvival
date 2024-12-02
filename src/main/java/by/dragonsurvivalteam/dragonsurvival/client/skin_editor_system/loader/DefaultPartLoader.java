package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonTypes;
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
    public static final Map</* Dragon type */ ResourceKey<DragonType>, Map</* Dragon level location */ String, HashMap<EnumSkinLayer, /* Dragon part key */ String>>> DEFAULT_PARTS = new HashMap<>();
    public static final String NO_PART = "none";
    private static final String DIRECTORY = "skin/default_parts";

    public DefaultPartLoader() {
        super(new Gson(), DIRECTORY);
    }

    // TODO: This probably doesn't actually work
    @Override
    protected void apply(final @NotNull Map<ResourceLocation, JsonElement> map, @NotNull final ResourceManager manager, @NotNull final ProfilerFiller profiler) {
        DEFAULT_PARTS.put(DragonTypes.CAVE, new HashMap<>());
        //DEFAULT_PARTS.put(DragonTypes.FOREST.getTypeNameLowerCase(), new HashMap<>());
        //DEFAULT_PARTS.put(DragonTypes.SEA.getTypeNameLowerCase(), new HashMap<>());

        map.forEach((location, value) -> {
            // Location path is without the specified directory
            ResourceKey<DragonType> dragonType = ResourceKey.create(DragonType.REGISTRY, location);
            JsonObject dragonStageMap = value.getAsJsonObject();

            for (String dragonStage : dragonStageMap.keySet()) {
                JsonObject partMap = dragonStageMap.get(dragonStage).getAsJsonObject();

                for (String part : partMap.keySet()) {
                    DEFAULT_PARTS.get(dragonType).computeIfAbsent(dragonStage, key -> new HashMap<>()).put(EnumSkinLayer.valueOf(part.toUpperCase(Locale.ENGLISH)), partMap.get(part).getAsString());
                }
            }
        });
    }

    public static String getDefaultPartKey(final ResourceKey<DragonType> type, final ResourceLocation dragonStage, final EnumSkinLayer layer) {
        HashMap<EnumSkinLayer, String> partMap = DEFAULT_PARTS.get(type).get(dragonStage.toString());
        String partKey = partMap != null ? partMap.getOrDefault(layer, NO_PART) : NO_PART;

        if (layer == EnumSkinLayer.BASE && partKey.equals(NO_PART)) {
            // Without a base the dragon will be invisible
            return DragonPartLoader.DRAGON_PARTS.get(type).get(layer).getFirst().key();
        }

        return partKey;
    }
}
