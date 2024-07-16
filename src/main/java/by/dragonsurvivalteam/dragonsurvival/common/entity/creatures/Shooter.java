package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

/*import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;

public class Shooter extends Hunter implements CrossbowAttackMob{
	private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Shooter.class, EntityDataSerializers.BOOLEAN);

	protected int bolasCooldown;

	public Shooter(EntityType<? extends PathfinderMob> entityType, Level world){
		super(entityType, world);
		// Vary the cooldown per mob so they don't all throw the bolas at the same time
		bolasCooldown = getBolasCooldown();
	}

	protected int getBolasCooldown() {
		return Functions.secondsToTicks(ServerConfig.hunterBolasFrequency + ((random.nextDouble() - 0.5) * ServerConfig.hunterBolasFrequency) / 5.0);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(3, new CrossbowAttackGoal<>(this, 1.0, 8.0F));
		this.goalSelector.addGoal(7, new FollowMobGoal<>(KnightEntity.class, this, 15));
		this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.6));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Shooter.class).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false, living -> living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DSEffects.ROYAL_CHASE)));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, false, false) {
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
	}

	@Override
	public void tick(){
		super.tick();

		if(!ServerConfig.hunterHasBolas) {
			return;
		}

		LivingEntity target = getTarget();
		if(target instanceof Player){
			if(bolasCooldown == 0){
				if(target.hasEffect(DSEffects.TRAPPED)) {
					// Wait to throw the bolas until the player is no longer trapped
					return;
				}
				performBolasThrow(target);
				bolasCooldown = getBolasCooldown();
			} else {
				bolasCooldown--;
			}
		}
	}

    public void performBolasThrow(LivingEntity target){
            Bolas bolas = new Bolas(this, level());
			Vec3 targetPos = target.position();
			if(target instanceof Player player)
			{
				DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

				if (!DragonStateProvider.isDragon(player)) {
					targetPos = targetPos.add(0, player.getEyeHeight(), 0);
				} else {
					targetPos = targetPos.add(0, DragonSizeHandler.calculateDragonEyeHeight(handler.getSize()), 0);
				}
			}

			Vec3 rawShootDirection = targetPos.subtract(bolas.getEyePosition());
			float distance = (float) rawShootDirection.length();

			// Adjust the launch angle to account for gravity as the target is further away (could be much smarter but this works good enough for now)
			targetPos = targetPos.add(0, distance / 10.f, 0);

			// Also lead their shot a bit
			targetPos = targetPos.add(target.getDeltaMovement().scale(distance / 5.f));

			Vec3 shootDirection = targetPos.subtract(bolas.getEyePosition()).normalize();
			// TODO: Maybe add an inaccuracy config option? Or calculate it based off of difficulty level in some way like for skeletons?
            bolas.shoot(shootDirection.x, shootDirection.y, shootDirection.z, 1.6F, 0.98f);
            playSound(SoundEvents.WITCH_THROW, 1.0F, 0.4F);
            level().addFreshEntity(bolas);
	}

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
	public void onCrossbowAttackPerformed(){
		noActionTime = 0;
	}

	@Override
	public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_){
		performCrossbowAttack(this, 1.6F);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder pBuilder){
		super.defineSynchedData(pBuilder);
		pBuilder.define(IS_CHARGING_CROSSBOW, false);
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
		setItemSlot(EquipmentSlot.MAINHAND, stack);
	}
	@Override
	public boolean removeWhenFarAway(double distance){
		return !hasCustomName() && tickCount >= Functions.minutesToTicks(ServerConfig.hunterDespawnDelay);
	}
}*/