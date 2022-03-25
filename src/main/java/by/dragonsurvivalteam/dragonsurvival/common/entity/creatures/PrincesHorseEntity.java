package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.AnimationTimer;
import by.dragonsurvivalteam.dragonsurvival.client.render.util.CommonTraits;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.PrincessTrades;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.item.Item;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PrincesHorse extends Villager implements IAnimatable, CommonTraits{
	private static final List<DyeColor> colors = Arrays.asList(DyeColor.RED, DyeColor.YELLOW, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BLACK, DyeColor.WHITE);
	public static EntityDataAccessor<Integer> color = SynchedEntityData.defineId(PrincesHorse.class, EntityDataSerializers.INT);
	AnimationFactory animationFactory = new AnimationFactory(this);
	AnimationTimer animationTimer = new AnimationTimer();

	public PrincesHorse(EntityType<? extends Villager> entityType, Level world){
		super(entityType, world);
	}

	public PrincesHorse(EntityType<? extends Villager> entityType, Level world, VillagerType villagerType){
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
	public boolean removeWhenFarAway(double distance){
		return !this.hasCustomName() && tickCount >= Functions.minutesToTicks(ConfigHandler.COMMON.princessDespawnDelay.get()) && !hasCustomName();
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
		if(level instanceof ServerLevel && !(this instanceof PrinceHorse)){
			Princess princess = DSEntities.PRINCESS.create(level);
			princess.setPos(getX(), getY(), getZ());
			princess.finalizeSpawn((IServerLevel)level, level.getCurrentDifficultyAt(blockPosition()), MobSpawnType.NATURAL, null, null);
			princess.setColor(getColor());
			princess.setUUID(UUID.randomUUID());
			level.addFreshEntity(princess);
		}
		super.die(damageSource);
		//        if (damageSource.getEntity() instanceof Player) {
		//            VillagerRelationsHandler.applyEvilMarker((Player) damageSource.getEntity());
		//        }
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
		setVillagerData(getVillagerData().setProfession(DSEntities.PRINCE_PROFESSION));
		return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData, compoundNBT);
	}

	public void thunderHit(ServerLevel p_241841_1_, LightningBolt p_241841_2_){
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

	protected void pickUpItem(Item p_175445_1_){
	}

	protected void registerGoals(){
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1){
			public boolean canUse(){
				return (!PrincesHorse.this.isTrading() && super.canUse());
			}
		});
		this.goalSelector.addGoal(6, new LookAtGoal(this, LivingEntity.class, 8.0F));
		goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Player.class, 16, 1, 1, living -> {
			return DragonUtils.isDragon(living) && living.hasEffect(DragonEffects.EVIL_DRAGON);
		}));
		goalSelector.addGoal(7, new PanicGoal(this, 1.5));
	}

	protected int getExperienceReward(Player p_70693_1_){
		return 1 + this.level.random.nextInt(2);
	}

	@Override
	public void registerControllers(AnimationData data){
		data.addAnimationController(new AnimationController(this, "everything", 4, event -> {
			AnimationBuilder builder = new AnimationBuilder();
			double speed = getMovementSpeed(this);
			AnimationController controller = event.getController();
			if(speed > 0.6){
				builder.addAnimation("run_princess");
			}else if(speed > 0.1){
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
}