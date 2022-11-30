package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonType;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;


@Mixin( Player.class )
public abstract class MixinPlayerEntity extends LivingEntity{

	private static final UUID SLOW_FALLING_ID = UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA");
	private static final AttributeModifier SLOW_FALLING = new AttributeModifier(SLOW_FALLING_ID, "Slow falling acceleration reduction", -0.07, AttributeModifier.Operation.ADDITION); // Add -0.07 to 0.08 so we get the vanilla default of 0.01
	@Shadow
	@Final
	private Abilities abilities;
	@Shadow
	@Final
	private Inventory inventory;
	@Shadow
	private int sleepCounter;

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> p_20966_, Level p_20967_){
		super(p_20966_, p_20967_);
	}

	@Inject( method = "isInvulnerableTo", at = @At( "HEAD" ), cancellable = true )
	public void isInvulnerableTo(DamageSource pSource, CallbackInfoReturnable<Boolean> cir){
		if(pSource == DamageSource.IN_WALL && DragonUtils.isDragon(this)){
			if(ServerConfig.disableSuffocation){
				cir.setReturnValue(true);
			}
		}
	}

	@Inject( method = "isImmobile", at = @At( "HEAD" ), cancellable = true )
	private void castMovement(CallbackInfoReturnable<Boolean> ci){
		DragonStateHandler cap = DragonUtils.getHandler(this);

		if(!isDeadOrDying() && !isSleeping()){
			if(!((Player)(LivingEntity)this).isCreative()){
				if(cap.getMagic().getCurrentlyCasting() != null){
					if(cap.getMagic().getCurrentlyCasting().requiresStationaryCasting()){
						if(!ServerConfig.canMoveWhileCasting){
							ci.setReturnValue(true);
						}
					}
				}
			}

			if(Arrays.stream(cap.getEmotes().currentEmotes).noneMatch(Objects::nonNull)){
				if(!ServerConfig.canMoveInEmote){
					ci.setReturnValue(true);
				}
			}
		}
	}

	@Redirect( method = "attack", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;" ) )
	private ItemStack getDragonSword(Player entity){
		ItemStack mainStack = entity.getMainHandItem();
		DragonStateHandler cap = DragonUtils.getHandler(entity);

		if(!(mainStack.getItem() instanceof TieredItem) && cap != null){
			ItemStack sword = cap.getClawInventory().getClawsInventory().getItem(0);

			if(!sword.isEmpty()){
				return sword;
			}
		}

		return mainStack;
	}

	@Redirect( method = "getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;" ), remap = false )
	private ItemStack getDragonTools(Player entity){
		return ClawToolHandler.getDragonTools(entity);
	}

	@Inject( method = "isSleepingLongEnough", at = @At( "HEAD" ), cancellable = true )
	public void isSleepingLongEnough(CallbackInfoReturnable<Boolean> ci){
		if(DragonUtils.isDragon(this)){
			DragonStateProvider.getCap(this).ifPresent(cap -> {
				if(cap.treasureResting && cap.treasureSleepTimer >= 100){
					ci.setReturnValue(true);
				}
			});
		}
	}


	@Inject( at = @At( "HEAD" ), method = "eat", cancellable = true )
	public void dragonEat(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> ci){
		DragonStateProvider.getCap(this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				DragonFoodHandler.dragonEat(this.getFoodData(), itemStack.getItem(), itemStack, dragonStateHandler.getType());
				this.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
				ci.setReturnValue(super.eat(level, itemStack));
			}
		});
	}

	@Shadow
	public FoodData getFoodData(){
		throw new IllegalStateException("Mixin failed to shadow getFoodData()");
	}


	@Shadow
	public void awardStat(Stat<Item> stat){
		throw new IllegalStateException("Mixin failed to shadow awardStat()");
	}

	@Inject( at = @At( "HEAD" ), method = "Lnet/minecraft/world/entity/player/Player;getMyRidingOffset()D", cancellable = true )
	public void dragonRidingOffset(CallbackInfoReturnable<Double> ci){
		DragonStateProvider.getCap(this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				ci.setReturnValue(0.25D);
			}
		});
	}

	@Inject( method = "travel", at = @At( "HEAD" ), cancellable = true )
	public void travel(Vec3 pTravelVector, CallbackInfo ci){
		if(DragonUtils.isDragon(this)){
			double d01 = this.getX();
			double d11 = this.getY();
			double d21 = this.getZ();
			if(DragonStateProvider.getCap(this).isPresent() && ConfigHandler.SERVER.bonuses && ConfigHandler.SERVER.caveLavaSwimming && DragonUtils.getDragonType(this) == DragonType.CAVE && DragonSizeHandler.getOverridePose(this) == Pose.SWIMMING || this.isSwimming() && !this.isPassenger()){
				double d3 = this.getLookAngle().y;
				double d4 = d3 < -0.2D ? 0.185D : 0.06D;
				if(d3 <= 0.0D || this.jumping || !this.level.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()){
					Vec3 vector3d1 = this.getDeltaMovement();
					this.setDeltaMovement(vector3d1.add(0.0D, (d3 - vector3d1.y) * d4, 0.0D));
				}

				if(this.isEffectiveAi() || this.isControlledByLocalInstance()){
					double d0 = 0.08D;
					AttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
					boolean flag = this.getDeltaMovement().y <= 0.0D;
					if(flag && this.hasEffect(MobEffects.SLOW_FALLING)){
						if(!gravity.hasModifier(SLOW_FALLING)){
							gravity.addTransientModifier(SLOW_FALLING);
						}
						this.fallDistance = 0.0F;
					}else if(gravity.hasModifier(SLOW_FALLING)){
						gravity.removeModifier(SLOW_FALLING);
					}
					d0 = gravity.getValue();

					FluidState fluidstate = this.level.getFluidState(this.blockPosition());
					if(this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate)
					   && ServerConfig.bonuses && ServerConfig.caveLavaSwimming && DragonUtils.getDragonType(this) == DragonType.CAVE){
						double d8 = this.getY();
						float f5 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
						float f6 = 0.05F;
						float f7 = Math.min(3, (float)EnchantmentHelper.getDepthStrider(this));

						if(!this.onGround){
							f7 *= 0.5F;
						}

						if(f7 > 0.0F){
							f5 += (0.54600006F - f5) * f7 / 2.5F;
							f6 += (this.getSpeed() - f6) * f7 / 2.5F;
						}

						if(this.hasEffect(MobEffects.DOLPHINS_GRACE)){
							f5 = 0.96F;
						}

						f6 *= (float)this.getAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get()).getValue();
						this.moveRelative(f6, pTravelVector);
						this.move(MoverType.SELF, this.getDeltaMovement());
						Vec3 vector3d6 = this.getDeltaMovement();
						if(this.horizontalCollision && this.onClimbable()){
							vector3d6 = new Vec3(vector3d6.x, 0.2D, vector3d6.z);
						}

						this.setDeltaMovement(vector3d6.multiply(f5, 0.8F, f5));
						Vec3 vector3d2 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
						this.setDeltaMovement(vector3d2);
						if(this.horizontalCollision && this.isFree(vector3d2.x, vector3d2.y + (double)0.6F - this.getY() + d8, vector3d2.z)){
							this.setDeltaMovement(vector3d2.x, 0.3F, vector3d2.z);
						}
					}
				}

				this.calculateEntityAnimation(this, this instanceof FlyingAnimal);
				this.checkMovementStatistics(this.getX() - d01, this.getY() - d11, this.getZ() - d21);
			}
		}
	}



	@Shadow
	public void checkMovementStatistics(double d, double e, double f){
		throw new IllegalStateException("Mixin failed to shadow checkMovementStatistics()");
	}
}