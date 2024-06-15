package by.dragonsurvivalteam.dragonsurvival.common.entity.goals;


import java.util.EnumSet;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public class CrossbowAttackGoal<T extends Mob&RangedAttackMob&CrossbowAttackMob> extends Goal{
	public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(20, 40);
	private final T mob;
	private final double speedModifier;
	private final float attackRadiusSqr;
	private CrossbowAttackGoal.CrossbowState crossbowState = CrossbowAttackGoal.CrossbowState.UNCHARGED;
	private int seeTime;
	private int attackDelay;
	private int updatePathDelay;

	enum CrossbowState{
		UNCHARGED,
		CHARGING,
		CHARGED,
		READY_TO_ATTACK
	}

	public CrossbowAttackGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_){
		mob = p_i50322_1_;
		speedModifier = p_i50322_2_;
		attackRadiusSqr = p_i50322_4_ * p_i50322_4_;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canContinueToUse(){
		return isValidTarget() && (canUse() || !mob.getNavigation().isDone()) && isHoldingCrossbow();
	}

	@Override
	public boolean canUse(){
		return isValidTarget() && isHoldingCrossbow();
	}

	private boolean isHoldingCrossbow(){
		return mob.isHolding(item -> item.getItem() instanceof CrossbowItem);
	}

	private boolean isValidTarget(){
		return mob.getTarget() != null && mob.getTarget().isAlive();
	}

	@Override
	public void stop(){
		super.stop();
		mob.setAggressive(false);
		mob.setTarget(null);
		seeTime = 0;
		if(mob.isUsingItem()){
			mob.stopUsingItem();
			mob.setChargingCrossbow(false);
		}
	}

	@Override
	public void tick(){
		LivingEntity livingentity = mob.getTarget();
		if(livingentity != null){
			boolean flag = mob.getSensing().hasLineOfSight(livingentity);
			boolean flag1 = seeTime > 0;
			if(flag != flag1){
				seeTime = 0;
			}

			if(flag){
				++seeTime;
			}else{
				--seeTime;
			}

			double d0 = mob.distanceToSqr(livingentity);
			boolean flag2 = (d0 > (double)attackRadiusSqr || seeTime < 5) && attackDelay == 0;
			if(flag2){
				--updatePathDelay;
				if(updatePathDelay <= 0){
					mob.getNavigation().moveTo(livingentity, canRun() ? speedModifier : speedModifier * 0.5D);
					updatePathDelay = PATHFINDING_DELAY_RANGE.sample(mob.getRandom());
				}
			}else{
				updatePathDelay = 0;
				mob.getNavigation().stop();
			}

			mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
			if(crossbowState == CrossbowAttackGoal.CrossbowState.UNCHARGED){
				if(!flag2){
					mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(mob, item -> item instanceof CrossbowItem));
					crossbowState = CrossbowAttackGoal.CrossbowState.CHARGING;
					mob.setChargingCrossbow(true);
				}
			}else if(crossbowState == CrossbowAttackGoal.CrossbowState.CHARGING){
				if(!mob.isUsingItem()){
					crossbowState = CrossbowAttackGoal.CrossbowState.UNCHARGED;
				}

				int i = mob.getTicksUsingItem();
				ItemStack itemstack = mob.getUseItem();
				if(i >= CrossbowItem.getChargeDuration(itemstack, mob)){
					mob.releaseUsingItem();
					crossbowState = CrossbowAttackGoal.CrossbowState.CHARGED;
					attackDelay = 20 + mob.getRandom().nextInt(20);
					mob.setChargingCrossbow(false);
				}
			}else if(crossbowState == CrossbowAttackGoal.CrossbowState.CHARGED){
				--attackDelay;
				if(attackDelay == 0){
					crossbowState = CrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
				}
			}else if(crossbowState == CrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && flag){
				mob.performRangedAttack(livingentity, 1.0F);
				ItemStack itemstack1 = mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(mob, item -> item instanceof CrossbowItem));
				mob.setChargingCrossbow(false);
				crossbowState = CrossbowAttackGoal.CrossbowState.UNCHARGED;
			}
		}
	}

	private boolean canRun(){
		return crossbowState == CrossbowAttackGoal.CrossbowState.UNCHARGED;
	}
}