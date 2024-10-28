package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.phys.AABB;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class HurtByTargetGoalExtended extends HurtByTargetGoal {
	@Nullable private Class<? extends Mob>[] toHeedAlert;

	public HurtByTargetGoalExtended(PathfinderMob pMob, Class<?>... pToIgnoreDamage) {
		super(pMob, pToIgnoreDamage);
	}

	@SafeVarargs
	public final HurtByTargetGoal setHeeders(Class<? extends Mob>... pReinforcementTypes) {
		this.toHeedAlert = pReinforcementTypes;
		return this;
	}

	@Override
	protected void alertOthers() {
		if (this.toHeedAlert != null) {
			for (Class<? extends Mob> oclass : this.toHeedAlert) {
				this.alertOthers(oclass);
			}
		}
	}

	// Copied from the original class
	protected void alertOthers(Class<? extends Mob> pType) {
		double d0 = this.getFollowDistance();
		AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0, d0);
		// We now read pType to get the list of mobs instead of using this.mob.getClass()
		List<? extends Mob> list = this.mob.level().getEntitiesOfClass(pType, aabb, EntitySelector.NO_SPECTATORS);
		Iterator iterator = list.iterator();

		while (true) {
			Mob mob;
			while (true) {
				if (!iterator.hasNext()) {
					return;
				}

				mob = (Mob) iterator.next();
				if (this.mob != mob
						&& mob.getTarget() == null
						&& (!(this.mob instanceof TamableAnimal) || ((TamableAnimal) this.mob).getOwner() == ((TamableAnimal) mob).getOwner())
						&& !mob.isAlliedTo(this.mob.getLastHurtByMob())) {
					if (this.toIgnoreAlert == null) {
						break;
					}

					boolean flag = false;

					for (Class<?> oclass : this.toIgnoreAlert) {
						if (mob.getClass() == oclass) {
							flag = true;
							break;
						}
					}

					if (!flag) {
						break;
					}
				}
			}

			this.alertOther(mob, this.mob.getLastHurtByMob());
		}
	}
}
