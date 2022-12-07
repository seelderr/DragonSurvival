package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SeaDragonType extends AbstractDragonType{
	public double timeWithoutWater;

	@Override
	public CompoundTag writeNBT(){
		CompoundTag tag = new CompoundTag();
		tag.putDouble("timeWithoutWater", timeWithoutWater);
		return tag;
	}

	@Override
	public void readNBT(CompoundTag base){
		timeWithoutWater = base.getDouble("timeWithoutWater");
	}

	@Override
	public String getTypeName(){
		return "sea_dragon";
	}

	@Override
	public void onPlayerUpdate(Player player, DragonStateHandler handler){

	}

	@Override
	public List<Pair<ItemStack, FoodData>> validFoods(Player player, DragonStateHandler handler){
		return null;
	}
}