package by.dragonsurvivalteam.dragonsurvival.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public class DragonTypeTypeAdapter implements JsonSerializer<DragonType>, JsonDeserializer<DragonType>{
	@Override
	public JsonElement serialize(DragonType src, Type typeOfSrc, JsonSerializationContext context){
		return context.serialize(src.name);
	}

	@Override
	public DragonType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
		return DragonType.valueOf(json.getAsString());
	}
}