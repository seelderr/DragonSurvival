package by.dragonsurvivalteam.dragonsurvival.common.capability;

import net.minecraft.world.phys.Vec3;

public class EntityStateHandler {
    // Last entity this entity recieved a debuff from
    public int lastAfflicted = -1;

    // Amount of times the last chain attack has chained
    public int chainCount = 0;

    public Vec3 lastPos;
}
