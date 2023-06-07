package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.EntityStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import com.google.common.base.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

public class DragonUtils{
	public static DragonStateHandler getHandler(Entity entity){
		if (entity == null) {
			return new DragonStateHandler();
		}

		LazyOptional<DragonStateHandler> cap = DragonStateProvider.getCap(entity);

		return cap.orElse(new DragonStateHandler());
	}

	public static EntityStateHandler getEntityHandler(Entity entity){
		if (entity == null) {
			return null;
		}

		LazyOptional<EntityStateHandler> cap = (LazyOptional<EntityStateHandler>) DragonStateProvider.getEntityCap(entity);

		return cap.orElse(new EntityStateHandler());
	}

	public static boolean isDragon(Entity entity){
		if (!(entity instanceof Player)) {
			return false;
		}

		return DragonStateProvider.getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
	}

	public static AbstractDragonType getDragonType(Entity entity){
		return getHandler(entity).getType();
	}

	public static boolean isDragonType(Entity entity, AbstractDragonType dragonType){
		if (!(entity instanceof Player)) {
			return false;
		}

		return isDragonType(getHandler(entity), dragonType);
	}
	
	public static boolean isDragonType(DragonStateHandler stateHandler, AbstractDragonType dragonType){
		return (stateHandler != null && stateHandler.isDragon()) == (dragonType != null) && Objects.equal(stateHandler.getType(), dragonType);
	}

	public static DragonLevel getDragonLevel(Entity entity){
		return getHandler(entity).getLevel();
	}
}