package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.AnimationTimer;
import by.dragonsurvivalteam.dragonsurvival.client.render.util.CommonTraits;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.FollowMobGoal;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.DSTrades;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PrincessHorseEntity extends Villager implements GeoEntity, CommonTraits {
	private static final List<DyeColor> COLORS = Arrays.asList(DyeColor.RED, DyeColor.YELLOW, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BLACK, DyeColor.WHITE);
	public static EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(PrincessHorseEntity.class, EntityDataSerializers.INT);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	protected final AnimationTimer animationTimer = new AnimationTimer();

	public PrincessHorseEntity(EntityType<? extends Villager> entityType, Level world){
		super(entityType, world);
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();

		Player player = getTradingPlayer();

		if (player != null) {
			if (getTradingPlayer() == null) {
				setTradingPlayer(player);
			}
		}
	}

	@Override
	public @NotNull SoundEvent getNotifyTradeSound() {
		return SoundEvents.EMPTY;
	}

	@Override
	protected @NotNull SoundEvent getTradeUpdatedSound(boolean p_213721_1_) {
		return SoundEvents.EMPTY;
	}

	@Override
	public void playCelebrateSound() {}

	@Override
	protected @NotNull Brain<?> makeBrain(@NotNull final Dynamic<?> p_213364_1_) {
		return brainProvider().makeBrain(p_213364_1_);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
		super.defineSynchedData(pBuilder);
		pBuilder.define(COLOR, 0);
	}

	@Override
	public void addAdditionalSaveData(@NotNull final CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Color", getColor());
	}

	public int getColor() {
		return entityData.get(COLOR);
	}

	@Override
	public void readAdditionalSaveData(@NotNull final CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
        setColor(compoundTag.getInt("Color"));
	}

	public void setColor(int color) {
		entityData.set(COLOR, color);
	}

	@Override
	@Nullable protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	protected SoundEvent getHurtSound(@NotNull final DamageSource damageSource) {
		return SoundEvents.GENERIC_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.PLAYER_DEATH;
	}

	@Override
	public void playWorkSound() {
	}

	@Override
	public boolean canBreed() {
		return false;
	}

	@Override
	protected @NotNull Component getTypeName() {
		return Component.translatable(getType().getDescriptionId());
	}

	@Override
	@Nullable public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType reason, @Nullable SpawnGroupData livingEntityData){
		setColor(COLORS.get(random.nextInt(6)).getId());
		setVillagerData(getVillagerData().setProfession(DSEntities.PRINCE_PROFESSION));
		return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData);
	}

	@Override
	public void thunderHit(@NotNull final ServerLevel level, @NotNull final LightningBolt bolt) { }

	@Override
	protected void updateTrades() {
		VillagerData villagerdata = getVillagerData();
		Int2ObjectMap<VillagerTrades.ItemListing[]> int2objectmap = DSTrades.princessColorCodes.get(getColor());

        if (int2objectmap != null && !int2objectmap.isEmpty()) {
			VillagerTrades.ItemListing[] trades = int2objectmap.get(villagerdata.getLevel());

            if (trades != null) {
				MerchantOffers merchantoffers = getOffers();
				addOffersFromItemListings(merchantoffers, trades, 4);
			}
		}
	}

	@Override
	public void gossip(@NotNull final ServerLevel level, @NotNull Villager villager, long p_242368_3_) {}

	@Override
	public void startSleeping(@NotNull final BlockPos position) {}

	@Override
	protected void pickUpItem(@NotNull final ItemEntity ignored) { }

	@Override
	protected void registerGoals(){
		super.registerGoals();
		this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
		this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6, 1, 1, living -> DragonStateProvider.isDragon(living) && living.hasEffect(DSEffects.ROYAL_CHASE)));
		this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
		this.targetSelector.addGoal(4, new HurtByTargetGoal(this, Hunter.class).setAlertOthers());
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
		this.goalSelector.addGoal(7, new FollowMobGoal<>(PrinceHorseEntity.class, this, 15));
		this.goalSelector.addGoal(9, new RandomStrollGoal(this, 0.5));
		this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
	}

	@Override // TODO 1.20 :: setAndContinue or add animations?
	public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "everything", 10, state -> {
			double speed = getMovementSpeed(this);
			AnimationController<PrincessHorseEntity> controller = state.getController();

			if (speed > 0.4) {
				return state.setAndContinue(WALK);
			} else if (speed > 0.05) {
				return state.setAndContinue(RUN);
			} else {
				AnimationProcessor.QueuedAnimation currentAnimation = controller.getCurrentAnimation();

				if (currentAnimation == null) {
					animationTimer.putAnimation("idle_princess", 88d);
					return state.setAndContinue(IDLE);
				} else {
					switch (currentAnimation.animation().name()) {
						case "idle_princess" -> {
							if (animationTimer.getDuration("idle_princess") <= 0) {
								if (random.nextInt(2000) == 1) {
									animationTimer.putAnimation("idle_princess_2", 145d);
									return state.setAndContinue(IDLE_2);
								}
							}
						}
						case "walk_princess", "run_princess" -> {
                            animationTimer.putAnimation("idle_princess", 88d);
                            return state.setAndContinue(IDLE);
                        }
						case "idle_princess_2" -> {
							if (animationTimer.getDuration("idle_princess_2") <= 0) {
								animationTimer.putAnimation("idle_princess", 88d);
                                return state.setAndContinue(IDLE);
							}
						}
					}
				}
			}

			return PlayState.CONTINUE;
		}));
	}

	@Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

	@Override
	public boolean removeWhenFarAway(double distance) {
		return !hasCustomName() && tickCount >= Functions.minutesToTicks(ServerConfig.hunterDespawnDelay);
	}

	private static final RawAnimation RUN = RawAnimation.begin().thenPlay("run_princess");
	private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk_princess");
	private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle_princess");
	private static final RawAnimation IDLE_2 = RawAnimation.begin().thenPlay("idle_princess_2");
}