package by.dragonsurvivalteam.dragonsurvival.api;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

import javax.annotation.Nullable;

public class DragonFood{

	public static boolean isEdible(Item item, Entity entity){
		if(entity != null && DragonUtils.isDragon(entity)){
			return DragonFoodHandler.isDragonEdible(item, DragonStateProvider.getCap(entity).orElseGet(null).getType());
		}
		return item.isEdible();
	}

	@Nullable
	public static Food getEffectiveFoodProperties(Item item, Entity entity){
		if(entity != null && DragonUtils.isDragon(entity)){
			return DragonFoodHandler.getDragonFoodProperties(item, DragonStateProvider.getCap(entity).orElseGet(null).getType());
		}
		return item.getFoodProperties();
	}

	public static DragonType getDragonType(Entity entity){
		if(entity != null && DragonUtils.isDragon(entity)){
			return DragonStateProvider.getCap(entity).orElseGet(null).getType();
		}
		return DragonType.NONE;
	}

	public static boolean isDrawingDragonFood(){
		return DragonFoodHandler.isDrawingOverlay;
	}
}