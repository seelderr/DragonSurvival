package by.dragonsurvivalteam.dragonsurvival.util;

import com.google.gson.*;
import net.minecraftforge.common.util.Lazy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
public class LazyJsonAdapter implements JsonSerializer<Lazy<Object>>, JsonDeserializer<Lazy<Object>>{
    @Override
    public Lazy<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Class type = (Class)((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        return Lazy.of(()->new Gson().fromJson(json, type));
    }

    @Override
    public JsonElement serialize(Lazy<Object> src, Type typeOfSrc, JsonSerializationContext context) {
        return new Gson().toJsonTree(src.get());
    }
}