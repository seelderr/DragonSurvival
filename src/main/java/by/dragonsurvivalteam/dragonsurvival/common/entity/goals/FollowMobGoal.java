package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;


import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class FollowMobGoal<T extends Class<? extends LivingEntity>> extends Goal{
	Class type;
	Mob follower;
	LivingEntity target;
	int distance;

	public FollowMobGoal(Class<? extends LivingEntity> type, Mob follower, int distance){
		this.type = type;
		this.follower = follower;
		this.distance = distance;
	}

	@Override
	public boolean canUse(){
		if(target == null){
			List<LivingEntity> list = follower.level().getEntitiesOfClass(type, new AABB(follower.blockPosition()).inflate(follower.getAttributeValue(Attributes.FOLLOW_RANGE)));
			if(!list.isEmpty()){
				target = list.get(follower.getRandom().nextInt(list.size()));
			}
		}
		return target != null;
	}

	@Override
	public void stop(){
		target = null;
	}

	@Override
	public void tick(){
		if(follower.distanceToSqr(target) > distance * distance){
			follower.getNavigation().moveTo(target, 1.0D);
		}
	}
}