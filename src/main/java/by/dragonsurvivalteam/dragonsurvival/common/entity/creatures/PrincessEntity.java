package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.PrincessTrades;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Horseless princess
 */
public class PrincessEntity extends VillagerEntity{
	private static final List<DyeColor> colors = Arrays.asList(DyeColor.RED, DyeColor.YELLOW, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BLACK, DyeColor.WHITE);
	public static DataParameter<Integer> color = EntityDataManager.defineId(PrincessEntity.class, DataSerializers.INT);

	public PrincessEntity(EntityType<? extends VillagerEntity> entityType, World world){
		super(entityType, world);
	}

	public PrincessEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType villagerType){
		super(entityType, world, villagerType);
	}

	@Override
	public SoundEvent getNotifyTradeSound(){
		return null;
	}

	@Override
	protected SoundEvent getTradeUpdatedSound(boolean p_213721_1_){
		return null;
	}

	public void playCelebrateSound(){
	}

	@Override
	protected Brain<?> makeBrain(Dynamic<?> p_213364_1_){
		return brainProvider().makeBrain(p_213364_1_);
	}

	public void refreshBrain(ServerWorld p_213770_1_){
	}

	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(color, 0);
	}

	public void addAdditionalSaveData(CompoundNBT compoundNBT){
		super.addAdditionalSaveData(compoundNBT);
		compoundNBT.putInt("Color", getColor());
	}

	public void readAdditionalSaveData(CompoundNBT compoundNBT){
		super.readAdditionalSaveData(compoundNBT);
		setColor(compoundNBT.getInt("Color"));
	}

	@Override
	public boolean removeWhenFarAway(double p_213397_1_){
		return !this.hasCustomName() && tickCount >= Functions.minutesToTicks(ConfigHandler.COMMON.princessDespawnDelay.get());
	}

	@Nullable
	protected SoundEvent getAmbientSound(){
		return null;
	}

	protected SoundEvent getHurtSound(DamageSource p_184601_1_){
		return SoundEvents.GENERIC_HURT;
	}

	protected SoundEvent getDeathSound(){
		return SoundEvents.PLAYER_DEATH;
	}

	public void playWorkSound(){
	}

	@Override
	public void die(DamageSource damageSource){
		super.die(damageSource);
		Item flower = Items.AIR;
		DyeColor dyeColor = DyeColor.byId(getColor());
		switch(dyeColor){
			case BLUE:
				flower = Items.BLUE_ORCHID;
				break;
			case RED:
				flower = Items.RED_TULIP;
				break;
			case BLACK:
				flower = Items.WITHER_ROSE;
				break;
			case YELLOW:
				flower = Items.DANDELION;
				break;
			case PURPLE:
				flower = Items.LILAC;
				break;
			case WHITE:
				flower = Items.LILY_OF_THE_VALLEY;
				break;
		}
		if(!level.isClientSide){
			level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), new ItemStack(flower)));
		}
	}

	public boolean canBreed(){
		return false;
	}

	protected ITextComponent getTypeName(){
		return new TranslationTextComponent(this.getType().getDescriptionId());
	}

	@Nullable
	public ILivingEntityData finalizeSpawn(IServerWorld serverWorld, DifficultyInstance difficultyInstance, SpawnReason reason,
		@Nullable
			ILivingEntityData livingEntityData,
		@Nullable
			CompoundNBT compoundNBT){
		setColor(colors.get(this.random.nextInt(6)).getId());
		setVillagerData(getVillagerData().setProfession(DSEntities.PRINCESS_PROFESSION));
		return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData, compoundNBT);
	}

	public void setColor(int i){
		this.entityData.set(color, i);
	}

	public void thunderHit(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_){
	}

	protected void pickUpItem(ItemEntity p_175445_1_){
	}

	protected void updateTrades(){
		VillagerData villagerdata = getVillagerData();
		Int2ObjectMap<VillagerTrades.ITrade[]> int2objectmap = PrincessTrades.colorToTrades.get(getColor());
		if(int2objectmap != null && !int2objectmap.isEmpty()){
			VillagerTrades.ITrade[] trades = int2objectmap.get(villagerdata.getLevel());
			if(trades != null){
				MerchantOffers merchantoffers = getOffers();
				addOffersFromItemListings(merchantoffers, trades, 2);
			}
		}
	}

	public void gossip(ServerWorld p_242368_1_, VillagerEntity p_242368_2_, long p_242368_3_){
	}

	public void startSleeping(BlockPos p_213342_1_){
	}

	public int getColor(){
		return this.entityData.get(color);
	}

	protected void registerGoals(){
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.5D){
			public boolean canUse(){
				return (!PrincessEntity.this.isTrading() && super.canUse());
			}
		});
		this.goalSelector.addGoal(6, new LookAtGoal(this, LivingEntity.class, 8.0F));
		goalSelector.addGoal(6, new AvoidEntityGoal<>(this, PlayerEntity.class, 16, 1, 1, livingEntity -> {
			return DragonUtils.isDragon(livingEntity) && livingEntity.hasEffect(DragonEffects.EVIL_DRAGON);
		}));
		goalSelector.addGoal(7, new PanicGoal(this, 1));
	}
}