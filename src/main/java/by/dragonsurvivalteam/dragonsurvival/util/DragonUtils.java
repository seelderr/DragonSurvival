package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import com.google.common.base.Objects;
import net.minecraft.world.entity.Entity;


public class DragonUtils{
	public static DragonStateHandler getHandler(Entity entity){
		return entity != null ? DragonStateProvider.getCap(entity).orElse(new DragonStateHandler()) : new DragonStateHandler();
	}
	
	public static boolean isDragon(Entity entity){
		return DragonStateProvider.getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
	}

	public static AbstractDragonType getDragonType(Entity entity){
		return getHandler(entity).getType();
	}

	public static boolean isDragonType(Entity entity, AbstractDragonType dragonType){
		return Objects.equal(getDragonType(entity), dragonType);
	}
	
	public static boolean isDragonType(DragonStateHandler stateHandler, AbstractDragonType dragonType){
		return (stateHandler != null && stateHandler.isDragon()) == (dragonType != null) && Objects.equal(stateHandler.getType(), dragonType);
	}

	public static DragonLevel getDragonLevel(Entity entity){
		return getHandler(entity).getLevel();
	}
}