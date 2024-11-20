package by.dragonsurvivalteam.dragonsurvival.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.neoforge.common.util.Lazy;

final public class GsonFactory {
    private static final Gson GSON = newBuilder().create();

    public static Gson getDefault() {
        return GSON;
    }

    public static GsonBuilder newBuilder() {
        return new GsonBuilder().registerTypeAdapter(Lazy.class, new LazyJsonAdapter());
    }
}
