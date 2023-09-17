package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.CrossbowAttackGoal;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.HunterEntityCheckProcedure;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.AbstractIllager.IllagerArmPose;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Shooter extends Hunter implements CrossbowAttackMob{
	private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Shooter.class, EntityDataSerializers.BOOLEAN);

	protected int bolasCooldown = Functions.secondsToTicks(30);

	public Shooter(EntityType<? extends PathfinderMob> entityType, Level world){
		super(entityType, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();

		this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this, Shooter.class).setAlertOthers());
		goalSelector.addGoal(3, new CrossbowAttackGoal<>(this, 1.0D, 5.0F));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false, living -> living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DragonEffects.ROYAL_CHASE)));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Monster.class, false, false) {
			@Override
			public boolean canUse() {
				Entity entity = Shooter.this;
				return super.canUse() && HunterEntityCheckProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				Entity entity = Shooter.this;
				return super.canContinueToUse() && HunterEntityCheckProcedure.execute(entity);
			}
		});
		this.targetSelector.addGoal(6, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(7, new FollowMobGoal<>(KnightEntity.class, this, 15));
		this.goalSelector.addGoal(9, new RandomStrollGoal(this, 0.5));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
	}

	/*		@Override
        public void tick(){
            super.tick();
            if(ServerConfig.hunterHasBolas){
                LivingEntity target = getTarget();
                if(target instanceof Player && DragonUtils.isDragon(target)){
                    if(bolasCooldown == 0){
                        performBolasThrow(target);
                        bolasCooldown = Functions.secondsToTicks(60);
                    }else{
                        bolasCooldown--;
                    }
                }
            }
        }

    public void performBolasThrow(LivingEntity target){
            Bolas bolas = new Bolas(this, level);
            double d0 = target.getEyeY() - (double)1.1F;
            double d1 = target.getX() - getX();
            double d2 = d0 - bolas.getY();
            double d3 = target.getZ() - getZ();
            float f = Mth.sqrt((float)(d1 * d1 + d3 * d3)) * 0.2F;
            bolas.shoot(d1, d2 + f, d3, 1.6F, 1.0F);
            playSound(SoundEvents.WITCH_THROW, 1.0F, 0.4F);
            level.addFreshEntity(bolas);
        }
    */
	@Override
	public IllagerArmPose getArmPose(){
		if(isChargingCrossbow()){
			return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
		}else if(isHolding(item -> item.getItem() instanceof CrossbowItem)){
			return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
		}else{
			return isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
		}
	}

	public boolean isChargingCrossbow(){
		return entityData.get(IS_CHARGING_CROSSBOW);
	}

	@Override
	public void setChargingCrossbow(boolean p_213671_1_){
		entityData.set(IS_CHARGING_CROSSBOW, p_213671_1_);
	}

	@Override
	public void shootCrossbowProjectile(LivingEntity p_230284_1_, ItemStack p_230284_2_, Projectile p_230284_3_, float p_230284_4_){
		shootCrossbowProjectile(this, p_230284_1_, p_230284_3_, p_230284_4_, 1.6F);
	}

	@Override
	public void onCrossbowAttackPerformed(){
		noActionTime = 0;
		ItemStack crossbow = getItemInHand(InteractionHand.MAIN_HAND);
		addArrow(crossbow);
	}

	private void addArrow(ItemStack stack){
		CompoundTag compoundNBT = stack.getOrCreateTag();
		ListTag listNBT = compoundNBT.getList("ChargedProjectiles", 10);
		CompoundTag nbt = new CompoundTag();
		new ItemStack(Items.ARROW).save(nbt);
		listNBT.add(nbt);
		compoundNBT.put("ChargedProjectiles", listNBT);
	}

	@Override
	public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_){
		performCrossbowAttack(this, 1.6F);
	}

	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		entityData.define(IS_CHARGING_CROSSBOW, false);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compoundNBT){
		super.addAdditionalSaveData(compoundNBT);
		compoundNBT.putInt("Bolas cooldown", bolasCooldown);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compoundNBT){
		super.readAdditionalSaveData(compoundNBT);
		bolasCooldown = compoundNBT.getInt("Bolas cooldown");
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance p_180481_1_){
		ItemStack stack = new ItemStack(Items.CROSSBOW);
		addArrow(stack);
		setItemSlot(EquipmentSlot.MAINHAND, stack);
	}
	@Override
	public boolean removeWhenFarAway(double distance){
		return !hasCustomName() && tickCount >= Functions.minutesToTicks(ServerConfig.hunterDespawnDelay);
	}
}