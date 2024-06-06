package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;


import java.util.Arrays;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class AlertGoal<T extends LivingEntity> extends Goal{
	T owner;
	Class<? extends Mob>[] toAlert;

	public AlertGoal(T owner, Class<? extends Mob>... toAlert){
		this.owner = owner;
		this.toAlert = toAlert;
	}

	@Override
	public boolean canUse(){
		return owner.getLastHurtByMob() != null && owner.getLastHurtByMob().isAlive();
	}

	@Override
	public void tick(){
		double range = owner.getAttributeValue(Attributes.FOLLOW_RANGE);
		AABB axisAlignedBB = new AABB(owner.blockPosition()).inflate(range);
		Arrays.stream(toAlert).forEach(aClass -> owner.level().getEntitiesOfClass(aClass, axisAlignedBB).forEach(mob -> mob.setTarget(owner.getLastHurtByMob())));
	}
}