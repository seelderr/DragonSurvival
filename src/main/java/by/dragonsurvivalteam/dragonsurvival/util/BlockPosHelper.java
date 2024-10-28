package by.dragonsurvivalteam.dragonsurvival.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class BlockPosHelper {
	public static BlockPos get(final Vec3i input) {
		return new BlockPos(input);
	}

	public static BlockPos get(int x, int y, int z) {
		return new BlockPos(x, y, z);
	}

	public static BlockPos get(double x, double y, double z) {
		return get((int) x, (int) y, (int) z);
	}

	public static BlockPos get(float x, float y, float z) {
		return get((int) x, (int) y, (int) z);
	}

	public static BlockPos get(final Vec3 input) {
		return get(input.x(), input.y(), input.z());
	}
}
