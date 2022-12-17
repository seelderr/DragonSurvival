package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;


public class DragonUtils{
	public static boolean isDragon(Entity entity){
		return DragonStateProvider.getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
	}

	public static AbstractDragonType getDragonType(Entity entity){
		return getHandler(entity).getType();
	}

	public static boolean isType(Entity entity, AbstractDragonType dragonType){
		AbstractDragonType type = getDragonType(entity);
		return type == null && dragonType == null || type != null && dragonType != null && type.equals(dragonType);
	}

	public static DragonStateHandler getHandler(Entity entity){
		return entity != null ?
			DragonStateProvider.getCap(entity).orElse(new DragonStateHandler()) : new DragonStateHandler();
	}

	public static DragonLevel getDragonLevel(Entity entity){
		return getHandler(entity).getLevel();
	}

	public static int wrap(int value, int min, int max){
		return value < min ? max : value > max ? min : value;
	}

	public static Vector3f getCameraOffset(Entity entity){
		Vector3f lookVector = new Vector3f(0, 0, 0);

		if(entity instanceof Player){
			Player player = (Player)entity;
			DragonStateHandler handler = DragonUtils.getHandler(player);
			if(handler.isDragon()){
				float f1 = -(float)handler.getMovementData().bodyYaw * ((float)Math.PI / 180F);

				float f4 = Mth.sin(f1);
				float f5 = Mth.cos(f1);
				lookVector.set((float)(f4 * (handler.getSize() / 40)), 0, (float)(f5 * (handler.getSize() / 40)));
			}
		}

		return lookVector;
	}
}