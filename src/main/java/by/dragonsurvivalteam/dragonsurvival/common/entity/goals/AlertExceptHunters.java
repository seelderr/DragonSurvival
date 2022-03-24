package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.DragonHunter;
import net.minecraft.entity.MobEntity;

public class AlertExceptHunters<T extends MobEntity> extends AlertGoal<MobEntity>{
	public AlertExceptHunters(T owner, Class<? extends MobEntity>... toAlert){
		super(owner, toAlert);
	}

	@Override
	public boolean canUse(){
		return !(owner.getLastHurtByMob() instanceof DragonHunter) && super.canUse();
	}
}