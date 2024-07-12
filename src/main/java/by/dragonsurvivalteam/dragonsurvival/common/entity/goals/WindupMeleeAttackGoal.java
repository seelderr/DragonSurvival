package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

/**
 * A goal that has Melee attacks with a windup "charge" time before the attack, rather than attacking instantly.
 */
public class WindupMeleeAttackGoal extends MeleeAttackGoal {
    private int ticksUntilDamage;
    private boolean hasSwung;

    public WindupMeleeAttackGoal(PathfinderMob pMob, double pSpeedModifier) {
        // We set following target even if not seen to true here always, since this system breaks down
        // if the attacker loses sight of the target and then regains it while the windup is still going.
        // This is because this goal stops ticking when the target is not seen and causes the animation to desync from the attack logic.
        super(pMob, pSpeedModifier, true);
    }

    @Override
    public void tick() {
        super.tick();
        this.ticksUntilDamage = Math.max(0, this.ticksUntilDamage - 1);
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.checkAndPerformWindupAttack(target);
        }
    }

    protected void checkAndPerformWindupAttack(@NotNull LivingEntity pTarget) {
        if (this.canPerformAttack(pTarget) || hasSwung) {
            if (!hasSwung) {
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.ticksUntilDamage = this.mob.getCurrentSwingDuration();
                hasSwung = true;
            }

            if (this.ticksUntilDamage <= 0) {
                if(this.canPerformAttack(pTarget)) {
                    this.mob.doHurtTarget(pTarget);
                }
                this.ticksUntilDamage = this.mob.getCurrentSwingDuration();
                hasSwung = false;
            }
        }
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity pTarget) {
        // No-op. The way the class hierarchy works I have to do this (throw out the old checkAndPerformAttack system and overlay the new one on top)
        // if I don't want to use a ton of access transformers.
    }

    @Override
    protected boolean isTimeToAttack() {
        // Needed to prevent canPerformAttack from using the attack timer in the superclass that we don't want.
        return true;
    }
}
