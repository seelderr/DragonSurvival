package by.jackraidenph.dragonsurvival.common.util;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class DragonUtils
{
	public static DragonStateHandler getHandler(Entity entity){
		return DragonStateProvider.getCap(entity).orElse(new DragonStateHandler());
	}
	
	public static boolean isDragon(Entity entity) {
	    return DragonStateProvider.getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
	}
	
	public static DragonType getDragonType(Entity entity) {
	    DragonStateHandler handler = DragonStateProvider.getCap(entity).orElse(null);
	    return handler != null ? handler.getType() : DragonType.NONE;
	}
	
	public static int wrap(int value, int min, int max) {
		return value < min ? max : value > max ? min : value;
	}
	
	public static Vector3f getCameraOffset(Entity entity){
		Vector3f lookVector = new Vector3f(0,0,0);
		
		if(entity instanceof PlayerEntity){
		    PlayerEntity player = (PlayerEntity)entity;
		    DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
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
