package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SkinLoader extends SimpleJsonResourceReloadListener {
    private static final ResourceLocation DEFAULT_PARTS = ResourceLocation.fromNamespaceAndPath(MODID, "skin/default_parts.json");
    private static final ResourceLocation CUSTOMIZATION = ResourceLocation.fromNamespaceAndPath(MODID, "skin/customization.json");

    public SkinLoader(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    protected void apply(final @NotNull Map<ResourceLocation, JsonElement> json, @NotNull final ResourceManager manager, @NotNull final ProfilerFiller profiler) {
        Optional<Resource> customizations = manager.getResource(CUSTOMIZATION);

        if (customizations.isEmpty()) {
            throw new IllegalStateException("The customization file [" + CUSTOMIZATION + "] is missing");
        }

    }
}
