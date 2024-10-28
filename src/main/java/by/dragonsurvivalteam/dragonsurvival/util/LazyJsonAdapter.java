package by.dragonsurvivalteam.dragonsurvival.util;

import com.google.gson.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.neoforged.neoforge.common.util.Lazy;

public class LazyJsonAdapter implements JsonSerializer<Lazy<Object>>, JsonDeserializer<Lazy<Object>> {
	@Override
	public Lazy<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Class type = (Class) ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
		return Lazy.of(() -> GsonFactory.getDefault().fromJson(json, type));
	}

	@Override
	public JsonElement serialize(Lazy<Object> src, Type typeOfSrc, JsonSerializationContext context) {
		Class type = (Class) ((ParameterizedType) typeOfSrc).getActualTypeArguments()[0];
		return GsonFactory.getDefault().toJsonTree(src.get(), type);
	}
}