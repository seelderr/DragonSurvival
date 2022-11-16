package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@JsonAdapter(DragonTypeTypeAdapter.class)
public class DragonType{
	public static ConcurrentHashMap<String, DragonType> TYPES = new ConcurrentHashMap<>();

	public static final DragonType CAVE = new DragonType("cave", BlockTags.MINEABLE_WITH_PICKAXE, ServerConfig.caveDragonFoods);
	public static final DragonType FOREST = new DragonType("forest", BlockTags.MINEABLE_WITH_AXE, ServerConfig.forestDragonFoods);
	public static final DragonType SEA = new DragonType("sea", BlockTags.MINEABLE_WITH_SHOVEL, ServerConfig.seaDragonFoods);
	public static final DragonType NONE = new DragonType("none");

	static {
		TYPES.put(CAVE.name, CAVE);
		TYPES.put(FOREST.name, FOREST);
		TYPES.put(SEA.name, SEA);
		TYPES.put(NONE.name, NONE);
	}


	public String name;
	public int index;
	public TagKey<Block> mineable;
	public List<String> foods;

	public static DragonType[] values(){
		return TYPES.values().toArray(new DragonType[0]);
	}

	public static DragonType valueOf(String value){
		return TYPES.getOrDefault(value.toLowerCase(), NONE);
	}


	public DragonType(String name){
		this.name = name;
		this.index = TYPES.size();
	}

	public DragonType(String name, TagKey<Block> mineable){
		this.name = name;
		this.index = TYPES.size();
		this.mineable = mineable;
	}

	public DragonType(String name, TagKey<Block> mineable, List<String> foods){
		this.name = name;
		this.mineable = mineable;
		this.foods = foods;
	}

	public String name(){
		return name;
	}

	public int ordinal(){
		return index;
	}

	public boolean equals(Object o){
		if(this == o){
			return true;
		}

		if(!(o instanceof DragonType type)){
			return false;
		}

		return new EqualsBuilder().append(name, type.name).isEquals();
	}
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(name).toHashCode();
	}
	public String toString(){
		return "DragonType{" + "name='" + name + '\'' + '}';
	}
}