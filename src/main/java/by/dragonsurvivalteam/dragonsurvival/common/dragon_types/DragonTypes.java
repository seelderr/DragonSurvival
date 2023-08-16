package by.dragonsurvivalteam.dragonsurvival.common.dragon_types;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.CaveDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.ForestDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class DragonTypes {
	private static final HashMap<String, Supplier<AbstractDragonType>> classMappings = new HashMap<>();
	public static final HashMap<String, AbstractDragonType> staticTypes = new HashMap<>();

	public static CaveDragonType CAVE;
	public static SeaDragonType SEA;
	public static ForestDragonType FOREST;

	public static void registerTypes(){
		CAVE = registerType(CaveDragonType::new);
        SEA = registerType(SeaDragonType::new);
		FOREST = registerType(ForestDragonType::new);
	}

	public static <T extends AbstractDragonType> T registerType(Supplier<T> constructor){
		T type = constructor.get();
		classMappings.put(type.getTypeName().toLowerCase(), (Supplier<AbstractDragonType>)constructor);
		staticTypes.put(type.getTypeName().toLowerCase(), type);
		return type;
	}

	public static AbstractDragonType getStatic(String name){
		return staticTypes.get(name.toLowerCase());
	}

	public static List<String> getTypes(){
		return staticTypes.keySet().stream().toList();
	}

	public static AbstractDragonType newDragonTypeInstance(String name){
		return classMappings.containsKey(name.toLowerCase()) ? classMappings.get(name.toLowerCase()).get() : null;
	}
}