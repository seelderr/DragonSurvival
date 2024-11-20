package by.dragonsurvivalteam.dragonsurvival.util;

import com.google.gson.*;
import net.neoforged.neoforge.common.util.Lazy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/** Get the actual type from the {@link Lazy} supplier to properly read and write the value through JSON */
public class LazyJsonAdapter implements JsonSerializer<Lazy<Object>>, JsonDeserializer<Lazy<Object>> {
    @Override
    public Lazy<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Type actualTypeArgument = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        return Lazy.of(() -> GsonFactory.getDefault().fromJson(json, actualTypeArgument));
    }

    @Override
    public JsonElement serialize(Lazy<Object> src, Type typeOfSrc, JsonSerializationContext context) {
        Type actualTypeArgument = ((ParameterizedType) typeOfSrc).getActualTypeArguments()[0];
        return GsonFactory.getDefault().toJsonTree(src.get(), actualTypeArgument);
    }
}