package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.AlertExceptHunters;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.HunterEntityCheckProcedure;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SquireEntity extends Hunter{
	public SquireEntity(EntityType<? extends PathfinderMob> entityType, Level world){
		super(entityType, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();

		this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this, Shooter.class).setAlertOthers());
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 2.0, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return (double) (4.0 + entity.getBbWidth() * entity.getBbWidth());
			}
		});
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false, living -> living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DragonEffects.ROYAL_CHASE)));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Monster.class, false, false) {
			@Override
			public boolean canUse() {
				double x = SquireEntity.this.getX();
				double y = SquireEntity.this.getY();
				double z = SquireEntity.this.getZ();
				Entity entity = SquireEntity.this;
				Level world = SquireEntity.this.level;
				return super.canUse() && HunterEntityCheckProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = SquireEntity.this.getX();
				double y = SquireEntity.this.getY();
				double z = SquireEntity.this.getZ();
				Entity entity = SquireEntity.this;
				Level world = SquireEntity.this.level;
				return super.canContinueToUse() && HunterEntityCheckProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return (double) (4.0 + entity.getBbWidth() * entity.getBbWidth());
			}
		});
		this.targetSelector.addGoal(7, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(8, new FollowMobGoal<>(KnightEntity.class, this, 15));
		this.goalSelector.addGoal(9, new RandomStrollGoal(this, 0.5));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
	}

	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance){
		setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
	}
}