package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BiomeDictionaryHelper{

	/**
	 * Converts a List <? extends String> to a {@link Type} array
	 *
	 * @param strings string array containing valid #BiomeDictionary.Types
	 *
	 * @return {@link Type} based on the string input
	 */
	public static Type[] toBiomeTypeArray(List<? extends String> strings){
		Type[] types = new Type[strings.size()];
		for(int i = 0; i < strings.size(); i++){
			String string = strings.get(i);
			types[i] = getType(string);
		}
		return types;
	}

	/**
	 * Retrieves a #BiomeDictionary.Type
	 * Based on {@link Type#getType(String, Type...)}, but doesn't create a new {@link Type} if the input is not already a {@link Type}
	 *
	 * @param name The name of this #BiomeDictionary.Type
	 *
	 * @return An instance of this #BiomeDictionary.Type
	 */
	public static Type getType(String name){
		Map<String, Type> byName = Type.getAll().stream().collect(Collectors.toMap(Type::getName, Function.identity()));
		name = name.toUpperCase();
		return byName.get(name);
	}
}