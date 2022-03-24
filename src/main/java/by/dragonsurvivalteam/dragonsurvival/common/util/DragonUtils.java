package by.dragonsurvivalteam.dragonsurvival.common.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class DragonUtils{
	public static boolean isDragon(Entity entity){
		return DragonStateProvider.getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
	}

	public static DragonType getDragonType(Entity entity){
		return getHandler(entity).getType();
	}

	public static DragonStateHandler getHandler(Entity entity){
		return entity != null ? DragonStateProvider.getCap(entity).orElse(new DragonStateHandler()) : new DragonStateHandler();
	}

	public static DragonLevel getDragonLevel(Entity entity){
		return getHandler(entity).getLevel();
	}

	public static int wrap(int value, int min, int max){
		return value < min ? max : value > max ? min : value;
	}

	public static Vector3f getCameraOffset(Entity entity){
		Vector3f lookVector = new Vector3f(0, 0, 0);

		if(entity instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)entity;
			DragonStateHandler handler = DragonUtils.getHandler(player);
			if(handler != null && handler.isDragon()){
				float f1 = -(float)handler.getMovementData().bodyYaw * ((float)Math.PI / 180F);

				float f4 = MathHelper.sin(f1);
				float f5 = MathHelper.cos(f1);
				lookVector.set((float)(f4 * (handler.getSize() / 40)), 0, (float)(f5 * (handler.getSize() / 40)));
			}
		}

		return lookVector;
	}
}