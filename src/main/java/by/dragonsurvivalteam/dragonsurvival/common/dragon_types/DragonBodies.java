package by.dragonsurvivalteam.dragonsurvival.common.dragon_types;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.bodies.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class DragonBodies {
	public static final HashMap<String, Supplier<AbstractDragonBody>> bodyMappings = new HashMap<>();
	public static final HashMap<String, AbstractDragonBody> staticBodies = new HashMap<>();

	public static CenterBodyType CENTER;
	public static NorthBodyType NORTH;
	public static EastBodyType EAST;
	public static SouthBodyType SOUTH;
	public static WestBodyType WEST;
	public static final String[] ORDER = {"CENTER", "NORTH", "EAST", "SOUTH", "WEST"}; // TODO: Something more elegant

	public static void registerBodies() {
		CENTER = registerType(CenterBodyType::new);
		NORTH = registerType(NorthBodyType::new);
		EAST = registerType(EastBodyType::new);
		SOUTH = registerType(SouthBodyType::new);
		WEST = registerType(WestBodyType::new);
	}

	public static <T extends AbstractDragonBody> T registerType(Supplier<T> constructor) {
		T body = constructor.get();
		String lcName = body.getBodyNameLowerCase();
		bodyMappings.put(lcName, (Supplier<AbstractDragonBody>) constructor);
		staticBodies.put(lcName, body);
		return body;
	}

	public static AbstractDragonBody getStatic(String name) {
		return staticBodies.get(name.toLowerCase(Locale.ENGLISH));
	}

	public static AbstractDragonBody newDragonBodyInstance(String name) {
		return bodyMappings.getOrDefault(name.toLowerCase(Locale.ENGLISH), () -> null).get();
	}

	public static List<String> getBodies() {
		return staticBodies.keySet().stream().toList();
	}
}
