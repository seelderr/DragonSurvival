package by.dragonsurvivalteam.dragonsurvival.util;

import com.google.gson.*;
import net.neoforged.neoforge.common.util.Lazy;

final  public class GsonFactory {
    private static final Gson object;
    static{
        object = newBuilder().create();
    }
    public static Gson getDefault()
    {
        return object;
    }
    public static GsonBuilder newBuilder()
    {
        return new GsonBuilder().registerTypeAdapter(Lazy.class, new LazyJsonAdapter());
    }
}
