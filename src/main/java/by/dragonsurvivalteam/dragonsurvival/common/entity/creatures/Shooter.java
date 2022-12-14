package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.AlertExceptHunters;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.CrossbowAttackGoal;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.Bolas;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.AbstractIllager.IllagerArmPose;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
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
	protected void registerGoals(){
		super.registerGoals();
		goalSelector.addGoal(3, new CrossbowAttackGoal<>(this, 1.0D, 8.0F));
		goalSelector.addGoal(8, new AlertExceptHunters<>(this, HunterHoundEntity.class, KnightEntity.class, SquireEntity.class));
	}

	public void tick(){
		super.tick();
		if(ServerConfig.hunterHasBolas){
			LivingEntity target = getTarget();
			if(target instanceof Player && DragonUtils.isDragon(target)){
				if(this.bolasCooldown == 0){
					performBolasThrow(target);
					this.bolasCooldown = Functions.secondsToTicks(15);
				}else{
					this.bolasCooldown--;
				}
			}
		}
	}

	public void performBolasThrow(LivingEntity target){
		Bolas bolas = new Bolas(this, this.level);
		double d0 = target.getEyeY() - (double)1.1F;
		double d1 = target.getX() - this.getX();
		double d2 = d0 - bolas.getY();
		double d3 = target.getZ() - this.getZ();
		float f = Mth.sqrt((float)(d1 * d1 + d3 * d3)) * 0.2F;
		bolas.shoot(d1, d2 + f, d3, 1.6F, 12.0F);
		playSound(SoundEvents.WITCH_THROW, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
		this.level.addFreshEntity(bolas);
	}

	public IllagerArmPose getArmPose(){
		if(this.isChargingCrossbow()){
			return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
		}else if(this.isHolding(item -> item.getItem() instanceof CrossbowItem)){
			return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
		}else{
			return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
		}
	}

	public boolean isChargingCrossbow(){
		return this.entityData.get(IS_CHARGING_CROSSBOW);
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

	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(IS_CHARGING_CROSSBOW, false);
	}

	public void addAdditionalSaveData(CompoundTag compoundNBT){
		super.addAdditionalSaveData(compoundNBT);
		compoundNBT.putInt("Bolas cooldown", this.bolasCooldown);
	}

	public void readAdditionalSaveData(CompoundTag compoundNBT){
		super.readAdditionalSaveData(compoundNBT);
		this.bolasCooldown = compoundNBT.getInt("Bolas cooldown");
	}

	protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_){
		ItemStack stack = new ItemStack(Items.CROSSBOW);
		addArrow(stack);
		this.setItemSlot(EquipmentSlot.MAINHAND, stack);
	}
}