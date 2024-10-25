package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class Functions{
	public static int minutesToTicks(int minutes){
		return secondsToTicks(minutes) * 60;
	}

	public static int secondsToTicks(double seconds) {
		return (int) (seconds * 20);
	}
	
	public static int secondsToTicks(int seconds){
		return seconds * 20;
	}

	public static double ticksToMinutes(int ticks){
		return ticksToSeconds(ticks) / 60;
	}

	public static double ticksToSeconds(int ticks){
		return ticks / 20d;
	}

	public static double angleDifference(double angle1, double angle2){
		double phi = Math.abs(angle1 - angle2) % 360;
		double dif = phi > 180 ? 360 - phi : phi;
		int sign = angle1 - angle2 >= 0 && angle1 - angle2 <= 180 || angle1 - angle2 <= -180 && angle1 - angle2 >= -360 ? 1 : -1;
		dif *= sign;
		return dif;
	}
	
	
	public static ListTag newDoubleList(double... pNumbers) {
		ListTag listtag = new ListTag();

		for(double d0 : pNumbers) {
			listtag.add(DoubleTag.valueOf(d0));
		}

		return listtag;
	}

	/**
	 * Returns a new NBTTagList filled with the specified floats
	 */
	public static ListTag newFloatList(float... pNumbers) {
		ListTag listtag = new ListTag();

		for(float f : pNumbers) {
			listtag.add(FloatTag.valueOf(f));
		}

		return listtag;
	}
	
	public static int wrap(int value, int min, int max){
		return value < min ? max : value > max ? min : value;
	}
	
	public static Vector3f getDragonCameraOffset(Entity entity){
		Vector3f lookVector = new Vector3f(0, 0, 0);

		if(entity instanceof Player player){
			DragonStateHandler handler = DragonStateProvider.getData(player);
			if(handler.isDragon()){
				float f1 = -(float)handler.getMovementData().bodyYaw * ((float)Math.PI / 180F);

				float f4 = Mth.sin(f1);
				float f5 = Mth.cos(f1);
				AttributeInstance attributeInstance = player.getAttribute(Attributes.SCALE);
				double scale = attributeInstance != null ? attributeInstance.getValue() : 1.0d;
				lookVector.set((float)(f4 * (handler.getSize() * scale / 40)), 0, (float)(f5 * (handler.getSize() * scale / 40)));
			}
		}

		return lookVector;
	}
}