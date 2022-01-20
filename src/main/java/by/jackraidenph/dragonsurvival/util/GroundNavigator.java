package by.jackraidenph.dragonsurvival.util;


import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GroundNavigator extends GroundPathNavigation
{
    public GroundNavigator(Mob p_i45875_1_, Level p_i45875_2_) {
        super(p_i45875_1_, p_i45875_2_);
    }

    @Override
    protected void doStuckDetection(Vec3 v) {
        if (tick - lastStuckCheck > 60 && v.distanceToSqr(this.lastStuckCheckPos) < 5) {
            this.stop();
        }
        super.doStuckDetection(v);
    }
}
