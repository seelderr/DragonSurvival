package by.jackraidenph.dragonsurvival.common.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;

public class AlertGoal<T extends LivingEntity> extends Goal
{
    T owner;
    Class<? extends Mob>[] toAlert;

    public AlertGoal(T owner, Class<? extends Mob>... toAlert) {
        this.owner = owner;
        this.toAlert = toAlert;
    }

    public boolean canUse() {
        return this.owner.getLastHurtByMob() != null && this.owner.getLastHurtByMob().isAlive();
    }

    public void tick() {
        double range = this.owner.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB axisAlignedBB = (new AABB(this.owner.blockPosition())).inflate(range);
        Arrays.stream(this.toAlert).forEach(aClass -> this.owner.level.getEntitiesOfClass(aClass, axisAlignedBB).forEach(mobEntity -> mobEntity.setTarget(owner.getLastHurtByMob())));
    }
}
