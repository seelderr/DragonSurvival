package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.AnimationTimer;
import by.dragonsurvivalteam.dragonsurvival.client.render.util.CommonTraits;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BannerPattern;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

public class KnightEntity extends PathfinderMob implements IAnimatable, DragonHunter, CommonTraits{
	AnimationFactory animationFactory = new AnimationFactory(this);
	AnimationTimer animationTimer = new AnimationTimer();

	public KnightEntity(EntityType<? extends PathfinderMob> p_i48576_1_, Level world){
		super(p_i48576_1_, world);
	}

	@Override
	public void registerControllers(AnimationData data){
		data.addAnimationController(new AnimationController<>(this, "everything", 3, event -> {
			AnimationBuilder animationBuilder = new AnimationBuilder();

			AnimationController animationController = event.getController();
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
			}else if(movement > 0.1){
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
	public AnimationFactory getFactory(){
		return animationFactory;
	}

	@Override
	protected void registerGoals(){
		super.registerGoals();
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, true));
		targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false, living -> {
			return living.hasEffect(MobEffects.BAD_OMEN) || living.hasEffect(DragonEffects.EVIL_DRAGON);
		}));
		targetSelector.addGoal(6, new HurtByTargetGoal(this, Shooter.class).setAlertOthers());
	}

	protected int getExperienceReward(Player p_70693_1_){
		return 5 + this.level.random.nextInt(5);
	}

	@Override
	public void tick(){
		updateSwingTime();
		super.tick();
	}

	@Override
	public boolean removeWhenFarAway(double distance){
		return !this.hasCustomName() && tickCount >= Functions.minutesToTicks(ServerConfig.hunterDespawnDelay);
	}

	@Override
	public boolean isBlocking(){
		if(getOffhandItem().getItem() == Items.SHIELD){
			return random.nextBoolean();
		}
		return false;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficultyInstance, MobSpawnType spawnReason,
		@Nullable
			SpawnGroupData entityData,
		@Nullable
			CompoundTag nbt){
		populateDefaultEquipmentSlots(difficultyInstance);
		return super.finalizeSpawn(serverWorld, difficultyInstance, spawnReason, entityData, nbt);
	}

	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance){
		setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
		if(random.nextDouble() < ServerConfig.knightShieldChance){
			ItemStack itemStack = new ItemStack(Items.SHIELD);
			ListTag listNBT = Functions.createRandomPattern(new BannerPattern.Builder(), 16);
			CompoundTag compoundNBT = new CompoundTag();
			compoundNBT.putInt("Base", DyeColor.values()[this.random.nextInt((DyeColor.values()).length)].getId());
			compoundNBT.put("Patterns", listNBT);
			itemStack.addTagElement("BlockEntityTag", compoundNBT);
			setItemInHand(InteractionHand.OFF_HAND, itemStack);
		}
	}
}