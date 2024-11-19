package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/** Loads the saved user customization */
public class CustomizationLoader implements PreparableReloadListener {
    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull final PreparationBarrier barrier, @NotNull final ResourceManager manager, @NotNull final ProfilerFiller preparationsProfiler, @NotNull final ProfilerFiller reloadProfiler, @NotNull final Executor backgroundExecutor, @NotNull final Executor gameExecutor) {



        return null;
    }
}
