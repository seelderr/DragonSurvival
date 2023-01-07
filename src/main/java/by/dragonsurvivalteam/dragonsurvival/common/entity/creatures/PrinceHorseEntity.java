package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.DSTrades;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nullable;

public class PrinceHorseEntity extends PrincesHorseEntity{
	public PrinceHorseEntity(EntityType<? extends Villager> entityType, Level world){
		super(entityType, world);
	}

	public PrinceHorseEntity(EntityType<? extends Villager> entityType, Level world, VillagerType villagerType){
		super(entityType, world, villagerType);
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType reason, @Nullable SpawnGroupData livingEntityData, @Nullable CompoundTag compoundNBT){
		setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));
		setVillagerData(getVillagerData().setProfession(DSEntities.PRINCESS_PROFESSION));
		return super.finalizeSpawn(serverWorld, difficultyInstance, reason, livingEntityData, compoundNBT);
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
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this, Shooter.class).setAlertOthers());
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false, living -> living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DragonEffects.ROYAL_CHASE)));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.getAvailableGoals().removeIf(prioritizedGoal -> {
			Goal goal = prioritizedGoal.getGoal();
			return goal instanceof PanicGoal || goal instanceof AvoidEntityGoal;});
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.5));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}

	@Override
	protected int getExperienceReward(Player p_70693_1_){
		return 1 + level.random.nextInt(2);
	}

	@Override
	public void registerControllers(AnimationData data){
		data.addAnimationController(new AnimationController<>(this, "everything", 0, event -> {
			AnimationBuilder animationBuilder = new AnimationBuilder();
			AnimationController<PrinceHorseEntity> animationController = event.getController();
			double movement = getMovementSpeed(this);
			if(swingTime > 0){
				Animation animation = animationController.getCurrentAnimation();
				if(animation != null){
					String name = animation.animationName;
					switch(name){
						case "attack":
							if(animationTimer.getDuration("attack2") <= 0){
								if(random.nextBoolean()){
									animationTimer.putAnimation("attack", 17d, animationBuilder);
								}else{
									animationTimer.putAnimation("attack2", 17d, animationBuilder);
								}
							}
							break;
						case "attack2":
							if(animationTimer.getDuration("attack") <= 0){
								if(random.nextBoolean()){
									animationTimer.putAnimation("attack", 17d, animationBuilder);
								}else{
									animationTimer.putAnimation("attack2", 17d, animationBuilder);
								}
							}
							break;
						default:
							if(random.nextBoolean()){
								animationTimer.putAnimation("attack", 17d, animationBuilder);
							}else{
								animationTimer.putAnimation("attack2", 17d, animationBuilder);
							}
					}
				}
			}
			if(movement > 0.4){
				animationBuilder.addAnimation("run");
			}else if(movement > 0.01){
				animationBuilder.addAnimation("walk");
			}else{
				Animation animation = animationController.getCurrentAnimation();
				if(animation == null){
					animationTimer.putAnimation("idle", 88d, animationBuilder);
				}else{
					String name = animation.animationName;
					switch(name){
						case "idle":
							if(animationTimer.getDuration("idle") <= 0){
								if(random.nextInt(2000) == 0){
									animationTimer.putAnimation("idle_2", 145d, animationBuilder);
								}
							}
							break;
						case "walk":
						case "run":
							animationTimer.putAnimation("idle", 88d, animationBuilder);
							break;
						case "idle_2":
							if(animationTimer.getDuration("idle_2") <= 0){
								animationTimer.putAnimation("idle", 88d, animationBuilder);
							}
							break;
					}
				}
			}
			animationController.setAnimation(animationBuilder);
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public void tick(){
		updateSwingTime();
		super.tick();
	}
}