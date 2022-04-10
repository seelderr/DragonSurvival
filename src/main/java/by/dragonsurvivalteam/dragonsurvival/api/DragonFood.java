package by.dragonsurvivalteam.dragonsurvival.api;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public class DragonFood{

	public static boolean isEdible(Item item, Entity entity){
		if(entity != null && DragonUtils.isDragon(entity))
			return DragonFoodHandler.isDragonEdible(item, DragonUtils.getHandler(entity).getType());
		return item.isEdible();
	}

	@Nullable
	public static FoodProperties getEffectiveFoodProperties(Item item, Entity entity){
		if(entity != null && DragonUtils.isDragon(entity))
			return DragonFoodHandler.getDragonFoodProperties(item, DragonUtils.getHandler(entity).getType());
		return item.getFoodProperties();
	}

	public static DragonType getDragonType(Entity entity){
		if(entity != null && DragonUtils.isDragon(entity))
			return DragonUtils.getHandler(entity).getType();
		return DragonType.NONE;
	}

	public static boolean isDrawingDragonFood(){
		return DragonFoodHandler.isDrawingOverlay;
	}
}