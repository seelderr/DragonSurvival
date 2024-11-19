package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonPart;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DragonPartLoader extends SimpleJsonResourceReloadListener {
    public static final Map<String, Map<EnumSkinLayer, List<DragonPart>>> DRAGON_PARTS = new HashMap<>();
    private static final String DIRECTORY = "skin/parts";

    public DragonPartLoader() {
        super(new Gson(), DIRECTORY);
    }

    @Override
    protected void apply(final @NotNull Map<ResourceLocation, JsonElement> map, @NotNull final ResourceManager manager, @NotNull final ProfilerFiller profiler) {
        DRAGON_PARTS.put(DragonTypes.CAVE.getTypeNameLowerCase(), new HashMap<>());
        DRAGON_PARTS.put(DragonTypes.FOREST.getTypeNameLowerCase(), new HashMap<>());
        DRAGON_PARTS.put(DragonTypes.SEA.getTypeNameLowerCase(), new HashMap<>());

        map.forEach((location, value) -> value.getAsJsonArray().forEach(element -> {
            // Format example: dragonsurvival:skin/parts/cave/base.json
            String[] elements = location.getPath().split("/");

            String dragonType = elements[2];
            EnumSkinLayer layer = EnumSkinLayer.valueOf(elements[3].toUpperCase(Locale.ENGLISH));

            DRAGON_PARTS.get(dragonType).computeIfAbsent(layer, key -> new ArrayList<>()).add(DragonPart.load(element.getAsJsonObject()));
        }));
    }
}
