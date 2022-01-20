package by.jackraidenph.dragonsurvival.common.entity.goals;

import by.jackraidenph.dragonsurvival.common.entity.creatures.DragonHunter;
import net.minecraft.world.entity.Mob;

public class AlertExceptHunters<T extends Mob> extends AlertGoal<Mob> {
    public AlertExceptHunters(T owner, Class<? extends Mob>... toAlert) {
        super(owner, toAlert);
    }

    @Override
    public boolean canUse() {
        return !(owner.getLastHurtByMob() instanceof DragonHunter) && super.canUse();
    }
}

