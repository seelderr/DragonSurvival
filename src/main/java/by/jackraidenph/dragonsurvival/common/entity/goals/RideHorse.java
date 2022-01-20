package by.jackraidenph.dragonsurvival.common.entity.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.pathfinder.Path;

@SuppressWarnings("unused")
public class RideHorse<E extends Mob> extends Goal
{
    protected E mob;

    public RideHorse(E mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return mob.getVehicle() instanceof Horse;
    }

    @Override
    public void tick() {
        Horse horseEntity = (Horse) mob.getVehicle();
        horseEntity.setYRot(mob.getYRot());
        horseEntity.yBodyRot = mob.yBodyRot;
        Path path = mob.getNavigation().getPath();
        horseEntity.getNavigation().moveTo(path, 2.5);
    }
}
