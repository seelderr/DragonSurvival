package by.dragonsurvivalteam.dragonsurvival.common.dragon_types;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.NBTInterface;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractDragonType implements NBTInterface{
	public abstract String getTypeName();
	public abstract void onPlayerUpdate(Player player, DragonStateHandler handler);
	public abstract List<Pair<ItemStack, FoodData>> validFoods(Player player, DragonStateHandler handler);
}