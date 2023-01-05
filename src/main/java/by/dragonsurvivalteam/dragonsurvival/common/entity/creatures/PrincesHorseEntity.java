package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.AnimationTimer;
import by.dragonsurvivalteam.dragonsurvival.client.render.util.CommonTraits;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.DSTrades;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PrincesHorseEntity extends Villager implements IAnimatable, CommonTraits{
	private static final List<DyeColor> colors = Arrays.asList(DyeColor.RED, DyeColor.YELLOW, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BLACK, DyeColor.WHITE);
	public static EntityDataAccessor<Integer> color = SynchedEntityData.defineId(PrincesHorseEntity.class, EntityDataSerializers.INT);
	AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);
	AnimationTimer animationTimer = new AnimationTimer();

	public PrincesHorseEntity(EntityType<? extends Villager> entityType, Level world){
		super(entityType, world);
	}

	public PrincesHorseEntity(EntityType<? extends Villager> entityType, Level world, VillagerType villagerType){
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

	@Override
	public void playCelebrateSound(){
	}

	@Override
	protected Brain<?> makeBrain(Dynamic<?> p_213364_1_){
		return brainProvider().makeBrain(p_213364_1_);
	}

	@Override
	public void refreshBrain(ServerLevel p_213770_1_){
	}

	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		entityData.define(color, 0);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compoundNBT){
		super.addAdditionalSaveData(compoundNBT);
		compoundNBT.putInt("Color", getColor());
	}

	public int getColor(){
		return entityData.get(color);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compoundNBT){
		super.readAdditionalSaveData(compoundNBT);
		setColor(compoundNBT.getInt("Color"));
	}

	public void setColor(int i){
		entityData.set(color, i);
	}

	//@Override
	//public boolean removeWhenFarAway(double distance){
	//	return !hasCustomName() && tickCount >= Functions.minutesToTicks(ServerConfig.princessDespawnDelay) && !hasCustomName();
	//}

	@Override
	@Nullable
	protected SoundEvent getAmbientSound(){
		return null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_){
		return SoundEvents.GENERIC_HURT;
	}

	@Override
	protected SoundEvent getDeathSound(){
		return SoundEvents.PLAYER_DEATH;
	}

	@Override
	public void playWorkSound(){
	}

/*	@Override
	public void die(DamageSource damageSource){
		if(level instanceof ServerLevel && !(this instanceof PrinceHorseEntity)){
			Princess princess = DSEntities.PRINCESS.create(level);
			princess.setPos(getX(), getY(), getZ());
			princess.finalizeSpawn((ServerLevelAccessor)level, level.getCurrentDifficultyAt(blockPosition()), MobSpawnType.NATURAL, null, null);
			princess.setColor(getColor());
			princess.setUUID(UUID.randomUUID());
			level.addFreshEntity(princess);
		}
		super.die(damageSource);

        if (damageSource.getEntity() instanceof Player) {
            VillagerRelationsHandler.applyEvilMarker((Player) damageSource.getEntity());
        }
}*/

	@Override
	public boolean canBreed(){
		return false;
	}

	@Override
	protected Component getTypeName(){
		return new TranslatableComponent(getType().getDescriptionId());
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType reason,
		@Nullable
			SpawnGroupData livingEntityData,
		@Nullable
			CompoundTag compoundNBT){
		setColor(colors.get(random.nextInt(6)).getId());
		setVillagerData(getVillagerData().setProfession(DSEntities.PRINCE_PROFESSION));
		return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData, compoundNBT);
	}

	@Override
	public void thunderHit(ServerLevel p_241841_1_, LightningBolt p_241841_2_){
	}

	@Override
	protected void updateTrades(){
		VillagerData villagerdata = getVillagerData();
		Int2ObjectMap<VillagerTrades.ItemListing[]> int2objectmap = DSTrades.princessColorCodes.get(getColor());
		if(int2objectmap != null && !int2objectmap.isEmpty()){
			VillagerTrades.ItemListing[] trades = int2objectmap.get(villagerdata.getLevel());
			if(trades != null){
				MerchantOffers merchantoffers = getOffers();
				addOffersFromItemListings(merchantoffers, trades, 2);
			}
		}
	}

	@Override
	public void gossip(ServerLevel p_242368_1_, Villager p_242368_2_, long p_242368_3_){
	}

	@Override
	public void startSleeping(BlockPos p_213342_1_){
	}

	protected void pickUpItem(Item p_175445_1_){
	}

	@Override
	protected void registerGoals(){
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1){
			@Override
			public boolean canUse(){
				return !isTrading() && super.canUse();
			}
		});
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
		goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Player.class, 16, 1, 1, living -> {
			return DragonUtils.isDragon(living) && living.hasEffect(DragonEffects.EVIL_DRAGON);
		}));
		goalSelector.addGoal(7, new PanicGoal(this, 2.0));
	}

	@Override
	protected int getExperienceReward(Player p_70693_1_){
		return 1 + level.random.nextInt(2);
	}

	@Override
	public void registerControllers(AnimationData data){
		data.addAnimationController(new AnimationController(this, "everything", 4, event -> {
			AnimationBuilder builder = new AnimationBuilder();
			double speed = getMovementSpeed(this);
			AnimationController controller = event.getController();
			if(speed > 0.4){
				builder.addAnimation("run_princess");
			}else if(speed > 0.01){
				builder.addAnimation("walk_princess");
			}else{
				Animation animation = controller.getCurrentAnimation();
				if(animation == null){
					animationTimer.putAnimation("idle_princess", 88d, builder);
				}else{
					String name = animation.animationName;
					switch(name){
						case "idle_princess":
							if(animationTimer.getDuration("idle_princess") <= 0){
								if(random.nextInt(2000) == 1){
									animationTimer.putAnimation("idle_princess_2", 145d, builder);
								}
							}
							break;
						case "walk_princess":
						case "run_princess":
							animationTimer.putAnimation("idle_princess", 88d, builder);
							break;
						case "idle_princess_2":
							if(animationTimer.getDuration("idle_princess_2") <= 0){
								animationTimer.putAnimation("idle_princess", 88d, builder);
							}
							break;
					}
				}
			}
			controller.setAnimation(builder);
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory(){
		return animationFactory;
	}

/*private static final TargetingConditions KNIGHT_RANGE = TargetingConditions.forNonCombat().range(32);
@Override
public void checkDespawn(){
	super.checkDespawn();

	if(isRemoved() || hasCustomName()) return;

	Entity entity1 = level.getNearestEntity(KnightEntity.class, KNIGHT_RANGE, this, getX(), getY(), getZ(), getBoundingBox().inflate(32));

	if(entity1 == null){
		Entity entity = level.getNearestPlayer(this, -1.0D);
		if (entity != null) {
			double d0 = entity.distanceToSqr(this);
			int i = getType().getCategory().getDespawnDistance();
			if (d0 > (double)(i * 4)) {
				discard();
				}
		}
		}
	}*/
}