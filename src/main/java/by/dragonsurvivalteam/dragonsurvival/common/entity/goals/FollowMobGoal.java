package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FollowMobGoal<T extends Class<? extends LivingEntity>> extends Goal{
	Class classs;
	Mob follower;
	LivingEntity target;
	int distance;

	public FollowMobGoal(Class classs, Mob follower, int distance){
		this.classs = classs;
		this.follower = follower;
		this.distance = distance;
	}

	public boolean canUse(){
		if(this.target == null){
			List<LivingEntity> list = this.follower.level.getEntitiesOfClass(this.classs, (new AABB(this.follower.blockPosition())).inflate(this.follower.getAttributeValue(Attributes.FOLLOW_RANGE)));
			if(!list.isEmpty()){
				this.target = list.get(this.follower.getRandom().nextInt(list.size()));
			}
		}
		return (this.target != null);
	}

	public void stop(){
		this.target = null;
	}

	public void tick(){
		if(this.follower.distanceToSqr(this.target) > (this.distance * this.distance)){
			this.follower.getNavigation().moveTo(this.target, 1.0D);
		}
	}
}