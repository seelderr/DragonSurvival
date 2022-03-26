package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.DragonHunter;
import net.minecraft.entity.Mob;

public class AlertExceptHunters<T extends Mob> extends AlertGoal<Mob>{
	public AlertExceptHunters(T owner, Class<? extends Mob>... toAlert){
		super(owner, toAlert);
	}

	@Override
	public boolean canUse(){
		return !(owner.getLastHurtByMob() instanceof DragonHunter) && super.canUse();
	}
}