package by.dragonsurvivalteam.dragonsurvival.util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.common.util.Lazy;

import java.io.IOException;
import java.lang.reflect.Type;

final  public class GsonFactory {
    private static final Gson object = new GsonBuilder().registerTypeAdapter(Lazy.class, new LazyJsonAdapter()).create();
    public static Gson getInstance()
    {
        return object;
    }
}
