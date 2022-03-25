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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobSpawnType;
import net.minecraft.entity.SpawnGroupData;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.effect.LightningBolt;
import net.minecraft.entity.item.Item;
import net.minecraft.entity.merchant.villager.Villager;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.datasync.EntityDataAccessor;
import net.minecraft.network.datasync.EntityDataSerializers;
import net.minecraft.network.datasync.SynchedEntityData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
 
 
import net.minecraft.world.DifficultyInstance;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Horseless princess
 */
public class Princess extends Villager{
	private static final List<DyeColor> colors = Arrays.asList(DyeColor.RED, DyeColor.YELLOW, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BLACK, DyeColor.WHITE);
	public static EntityDataAccessor<Integer> color = SynchedEntityData.defineId(Princess.class, EntityDataSerializers.INT);

	public Princess(EntityType<? extends Villager> entityType, Level world){
		super(entityType, world);
	}

	public Princess(EntityType<? extends Villager> entityType, Level world, VillagerType villagerType){
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

	public void refreshBrain(ServerLevel p_213770_1_){
	}

	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(color, 0);
	}

	public void addAdditionalSaveData(CompoundTag compoundNBT){
		super.addAdditionalSaveData(compoundNBT);
		compoundNBT.putInt("Color", getColor());
	}

	public int getColor(){
		return this.entityData.get(color);
	}

	public void readAdditionalSaveData(CompoundTag compoundNBT){
		super.readAdditionalSaveData(compoundNBT);
		setColor(compoundNBT.getInt("Color"));
	}

	public void setColor(int i){
		this.entityData.set(color, i);
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
			level.addFreshEntity(new Item(level, getX(), getY(), getZ(), new ItemStack(flower)));
		}
	}

	public boolean canBreed(){
		return false;
	}

	protected Component getTypeName(){
		return new TranslatableComponent(this.getType().getDescriptionId());
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType reason,
		@Nullable
			SpawnGroupData livingEntityData,
		@Nullable
			CompoundTag compoundNBT){
		setColor(colors.get(this.random.nextInt(6)).getId());
		setVillagerData(getVillagerData().setProfession(DSEntities.PRINCESS_PROFESSION));
		return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData, compoundNBT);
	}

	public void thunderHit(ServerLevel p_241841_1_, LightningBolt p_241841_2_){
	}

	protected void pickUpItem(Item p_175445_1_){
	}

	protected void updateTrades(){
		VillagerData villagerdata = getVillagerData();
		Int2ObjectMap<VillagerTrades.ItemListing[]> int2objectmap = PrincessTrades.colorToTrades.get(getColor());
		if(int2objectmap != null && !int2objectmap.isEmpty()){
			VillagerTrades.ItemListing[] trades = int2objectmap.get(villagerdata.getLevel());
			if(trades != null){
				MerchantOffers merchantoffers = getOffers();
				addOffersFromItemListings(merchantoffers, trades, 2);
			}
		}
	}

	public void gossip(ServerLevel p_242368_1_, Villager p_242368_2_, long p_242368_3_){
	}

	public void startSleeping(BlockPos p_213342_1_){
	}

	protected void registerGoals(){
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.5D){
			public boolean canUse(){
				return (!Princess.this.isTrading() && super.canUse());
			}
		});
		this.goalSelector.addGoal(6, new LookAtGoal(this, LivingEntity.class, 8.0F));
		goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Player.class, 16, 1, 1, living -> {
			return DragonUtils.isDragon(living) && living.hasEffect(DragonEffects.EVIL_DRAGON);
		}));
		goalSelector.addGoal(7, new PanicGoal(this, 1));
	}
}