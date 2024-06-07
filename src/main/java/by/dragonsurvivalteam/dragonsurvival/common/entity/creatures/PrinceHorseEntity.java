package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.DSTrades;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import javax.annotation.Nullable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PrinceHorseEntity extends PrincessHorseEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public PrinceHorseEntity(final EntityType<? extends Villager> entityType, final Level level) {
		super(entityType, level);
	}

	@Override
	@Nullable public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType reason, @Nullable SpawnGroupData livingEntityData){
		setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));
		setVillagerData(getVillagerData().setProfession(DSEntities.PRINCESS_PROFESSION));
		return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData);
	}
	
	@Override
	protected void updateTrades(){
		VillagerData villagerdata = getVillagerData();
		Int2ObjectMap<ItemListing[]> int2objectmap = DSTrades.princeTrades.get(getColor());
		if(int2objectmap != null && !int2objectmap.isEmpty()){
			VillagerTrades.ItemListing[] trades = int2objectmap.get(villagerdata.getLevel());
			if(trades != null){
				MerchantOffers merchantoffers = getOffers();
				addOffersFromItemListings(merchantoffers, trades, 4);
			}
		}
	}

	@Override
	protected void registerGoals(){
		super.registerGoals();
		this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this, Hunter.class).setAlertOthers());
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false, living -> living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DSEffects.ROYAL_CHASE)));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 2.0D, true));
		this.goalSelector.getAvailableGoals().removeIf(prioritizedGoal -> {
			Goal goal = prioritizedGoal.getGoal();
			return goal instanceof PanicGoal || goal instanceof AvoidEntityGoal;
		});
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.5));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}

	@Override
	public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "everything", 0, state -> {
			AnimationController<PrinceHorseEntity> controller = state.getController();
			double movement = getMovementSpeed(this);

			if (swingTime > 0) {
				AnimationProcessor.QueuedAnimation currentAnimation = controller.getCurrentAnimation();

				if (currentAnimation != null) {
					switch (currentAnimation.animation().name()) {
						case "attack" -> {
							if (animationTimer.getDuration("attack2") <= 0) {
								if (random.nextBoolean()) {
									animationTimer.putAnimation("attack", 17d);
									return state.setAndContinue(ATTACK);
								} else {
									animationTimer.putAnimation("attack2", 17d);
									return state.setAndContinue(ATTACK_2);
								}
							}
						}
						case "attack2" -> {
							if (animationTimer.getDuration("attack") <= 0) {
								if (random.nextBoolean()) {
									animationTimer.putAnimation("attack", 17d);
									return state.setAndContinue(ATTACK);
								} else {
									animationTimer.putAnimation("attack2", 17d);
									return state.setAndContinue(ATTACK_2);
								}
							}
						}
						default -> {
							if (random.nextBoolean()) {
								animationTimer.putAnimation("attack", 17d);
								return state.setAndContinue(ATTACK);
							} else {
								animationTimer.putAnimation("attack2", 17d);
								return state.setAndContinue(ATTACK_2);
							}
						}
					}
				}
			}

			// TODO 1.20 :: AnimationUtils.createAnimation
			if (movement > 0.4) {
				return state.setAndContinue(RUN);
			} else if(movement > 0.05) {
				return state.setAndContinue(WALK);
			} else {
				AnimationProcessor.QueuedAnimation currentAnimation = controller.getCurrentAnimation();

				if (currentAnimation == null) {
					animationTimer.putAnimation("idle", 88d);
					return state.setAndContinue(IDLE);
				} else {
					switch (currentAnimation.animation().name()) {
						case "idle" -> {
							if (animationTimer.getDuration("idle") <= 0) {
								if (random.nextInt(2000) == 0) {
									animationTimer.putAnimation("idle_2", 145d);
									return state.setAndContinue(IDLE_2);
								}
							}
						}
						case "walk", "run" -> {
							animationTimer.putAnimation("idle", 88d);
							return state.setAndContinue(IDLE);
						}
						case "idle_2" -> {
							if (animationTimer.getDuration("idle_2") <= 0) {
								animationTimer.putAnimation("idle", 88d);
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
	public void tick() {
		updateSwingTime();
		super.tick();
	}

	private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
	private static final RawAnimation ATTACK_2 = RawAnimation.begin().thenPlay("attack2");
	private static final RawAnimation RUN = RawAnimation.begin().thenPlay("run");
	private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
	private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
	private static final RawAnimation IDLE_2 = RawAnimation.begin().thenPlay("idle_2");
}