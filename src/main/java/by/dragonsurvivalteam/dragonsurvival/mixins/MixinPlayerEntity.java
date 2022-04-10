package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
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
	public Abilities abilities;
	@Shadow
	@Final
	public Inventory inventory;
	@Shadow
	private int sleepCounter;

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> p_20966_, Level p_20967_){
		super(p_20966_, p_20967_);
	}

	@Inject( method = "isImmobile", at = @At( "HEAD" ), cancellable = true )
	private void castMovement(CallbackInfoReturnable<Boolean> ci){
		DragonStateHandler cap = DragonUtils.getHandler(this);

		if(!isDeadOrDying() && !isSleeping()){
			if(!((Player)(LivingEntity)this).isCreative()){
				if(cap.getMagic().getCurrentlyCasting() != null){
					if(!cap.getMagic().getCurrentlyCasting().canMoveWhileCasting()){
						if(!ConfigHandler.SERVER.canMoveWhileCasting.get()){
							ci.setReturnValue(true);
						}
					}
				}
			}

			if(Arrays.stream(cap.getEmotes().currentEmotes).noneMatch(Objects::nonNull)){
				if(!ConfigHandler.SERVER.canMoveInEmote.get()){
					ci.setReturnValue(true);
				}
			}
		}
	}

	@Redirect( method = "attack", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V" ) )
	public void setItemInHand(Player instance, InteractionHand interactionHand, ItemStack itemStack){
		ItemStack handStack = instance.getItemInHand(interactionHand);

		if(ClawToolHandler.destroyedItems.containsKey(instance.getId())){
			if(!ItemStack.matches(handStack, ClawToolHandler.destroyedItems.get(instance.getId()))){
				ClawToolHandler.destroyedItems.remove(instance.getId());
				return;
			}
		}

		instance.setItemInHand(interactionHand, itemStack);
	}

	@Redirect( method = "attack", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;" ) )
	private ItemStack getDragonSword(Player entity){
		ItemStack mainStack = entity.getMainHandItem();
		DragonStateHandler cap = DragonUtils.getHandler(entity);

		if(!(mainStack.getItem() instanceof TieredItem) && cap != null){
			ItemStack sword = cap.getClawInventory().getClawsInventory().getItem(0);

			if(sword != null && !sword.isEmpty()){
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

	@Inject( method = "travel", at = @At( "HEAD" ), cancellable = true )
	public void travel(Vec3 p_213352_1_, CallbackInfo ci){
		if(DragonUtils.isDragon(this)){
			double d0 = this.getX();
			double d1 = this.getY();
			double d2 = this.getZ();
			if((DragonStateProvider.getCap(this).isPresent() && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimming.get() && DragonUtils.getHandler(this).getType() == DragonType.CAVE && DragonSizeHandler.getOverridePose(this) == Pose.SWIMMING) || this.isSwimming() && !this.isPassenger()){
				double d3 = this.getLookAngle().y;
				double d4 = d3 < -0.2D ? 0.085D : 0.06D;
				if(d3 <= 0.0D || this.jumping || !this.level.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()){
					Vec3 vector3d1 = this.getDeltaMovement();
					this.setDeltaMovement(vector3d1.add(0.0D, (d3 - vector3d1.y) * d4, 0.0D));
				}
			}

			if(this.abilities.flying && !this.isPassenger()){
				double d5 = this.getDeltaMovement().y;
				float f = this.flyingSpeed;
				this.flyingSpeed = this.abilities.getFlyingSpeed() * (float)(this.isSprinting() ? 2 : 1);
				dragonTravel(p_213352_1_);
				Vec3 vector3d = this.getDeltaMovement();
				this.setDeltaMovement(vector3d.x, d5 * 0.6D, vector3d.z);
				this.flyingSpeed = f;
				this.fallDistance = 0.0F;
				this.setSharedFlag(7, false);
			}else{
				dragonTravel(p_213352_1_);
			}
			this.checkMovementStatistics(this.getX() - d0, this.getY() - d1, this.getZ() - d2);
			ci.cancel();
		}
	}

	public void dragonTravel(Vec3 p_213352_1_){
		if(!DragonUtils.isDragon(this)){
			super.travel(p_213352_1_);
			return;
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
			if(this.isInWater() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate)){
				double d8 = this.getY();
				float f5 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
				float f6 = 0.02F;
				float f7 = (float)EnchantmentHelper.getDepthStrider(this);
				if(f7 > 3.0F){
					f7 = 3.0F;
				}

				if(!this.onGround){
					f7 *= 0.5F;
				}

				if(f7 > 0.0F){
					f5 += (0.54600006F - f5) * f7 / 3.0F;
					f6 += (this.getSpeed() - f6) * f7 / 3.0F;
				}

				if(this.hasEffect(MobEffects.DOLPHINS_GRACE)){
					f5 = 0.96F;
				}

				f6 *= (float)this.getAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get()).getValue();
				this.moveRelative(f6, p_213352_1_);
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
			}else if(this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate) && (ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimming.get() && DragonStateProvider.getCap(this).isPresent() && DragonUtils.getHandler(this).getType() == DragonType.CAVE)){
				double d8 = this.getY();
				float f5 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
				float f6 = 0.02F;
				float f7 = (float)EnchantmentHelper.getDepthStrider(this);
				if(f7 > 3.0F){
					f7 = 3.0F;
				}

				if(!this.onGround){
					f7 *= 0.5F;
				}

				if(f7 > 0.0F){
					f5 += (0.54600006F - f5) * f7 / 3.0F;
					f6 += (this.getSpeed() - f6) * f7 / 3.0F;
				}

				if(this.hasEffect(MobEffects.DOLPHINS_GRACE)){
					f5 = 0.96F;
				}

				f6 *= (float)this.getAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get()).getValue();
				this.moveRelative(f6, p_213352_1_);
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
			}else if(this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate)){
				double d7 = this.getY();
				this.moveRelative(0.02F, p_213352_1_);
				this.move(MoverType.SELF, this.getDeltaMovement());
				if(this.getFluidHeight(FluidTags.LAVA) <= this.getFluidJumpThreshold()){
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.8F, 0.5D));
					Vec3 vector3d3 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
					this.setDeltaMovement(vector3d3);
				}else{
					this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
				}

				if(!this.isNoGravity()){
					this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -d0 / 4.0D, 0.0D));
				}

				Vec3 vector3d4 = this.getDeltaMovement();
				if(this.horizontalCollision && this.isFree(vector3d4.x, vector3d4.y + (double)0.6F - this.getY() + d7, vector3d4.z)){
					this.setDeltaMovement(vector3d4.x, 0.3F, vector3d4.z);
				}
			}else if(this.isFallFlying()){
				Vec3 vector3d = this.getDeltaMovement();
				if(vector3d.y > -0.5D){
					this.fallDistance = 1.0F;
				}

				Vec3 vector3d1 = this.getLookAngle();
				float f = this.xRot * ((float)Math.PI / 180F);
				double d1 = Math.sqrt(vector3d1.x * vector3d1.x + vector3d1.z * vector3d1.z);
				double d3 = Math.sqrt(vector3d.horizontalDistanceSqr());
				double d4 = vector3d1.length();
				float f1 = Mth.cos(f);
				f1 = (float)((double)f1 * (double)f1 * Math.min(1.0D, d4 / 0.4D));
				vector3d = this.getDeltaMovement().add(0.0D, d0 * (-1.0D + (double)f1 * 0.75D), 0.0D);
				if(vector3d.y < 0.0D && d1 > 0.0D){
					double d5 = vector3d.y * -0.1D * (double)f1;
					vector3d = vector3d.add(vector3d1.x * d5 / d1, d5, vector3d1.z * d5 / d1);
				}

				if(f < 0.0F && d1 > 0.0D){
					double d9 = d3 * (double)(-Mth.sin(f)) * 0.04D;
					vector3d = vector3d.add(-vector3d1.x * d9 / d1, d9 * 3.2D, -vector3d1.z * d9 / d1);
				}

				if(d1 > 0.0D){
					vector3d = vector3d.add((vector3d1.x / d1 * d3 - vector3d.x) * 0.1D, 0.0D, (vector3d1.z / d1 * d3 - vector3d.z) * 0.1D);
				}

				this.setDeltaMovement(vector3d.multiply(0.99F, 0.98F, 0.99F));
				this.move(MoverType.SELF, this.getDeltaMovement());
				if(this.horizontalCollision && !this.level.isClientSide){
					double d10 = Math.sqrt(this.getDeltaMovement().horizontalDistanceSqr());
					double d6 = d3 - d10;
					float f2 = (float)(d6 * 10.0D - 3.0D);
					if(f2 > 0.0F){
						this.playSound((int)f2 > 4 ? this.getFallSounds().big() : this.getFallSounds().small(), 1.0F, 1.0F);
						this.hurt(DamageSource.FLY_INTO_WALL, f2);
					}
				}

				if(this.onGround && !this.level.isClientSide){
					this.setSharedFlag(7, false);
				}
			}else{
				BlockPos blockpos = this.getBlockPosBelowThatAffectsMyMovement();
				float f3 = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getFriction(level, this.getBlockPosBelowThatAffectsMyMovement(), this);
				float f4 = this.onGround ? f3 * 0.91F : 0.91F;
				Vec3 vector3d5 = this.handleRelativeFrictionAndCalculateMovement(p_213352_1_, f3);
				double d2 = vector3d5.y;
				if(this.hasEffect(MobEffects.LEVITATION)){
					d2 += (0.05D * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vector3d5.y) * 0.2D;
					this.fallDistance = 0.0F;
				}else if(this.level.isClientSide && !this.level.hasChunkAt(blockpos)){
					if(this.getY() > 0.0D){
						d2 = -0.1D;
					}else{
						d2 = 0.0D;
					}
				}else if(!this.isNoGravity()){
					d2 -= d0;
				}

				this.setDeltaMovement(vector3d5.x * (double)f4, d2 * (double)0.98F, vector3d5.z * (double)f4);
			}
		}

		this.calculateEntityAnimation(this, this instanceof FlyingAnimal);
	}

	@Shadow
	public void checkMovementStatistics(double d, double e, double f){
		throw new IllegalStateException("Mixin failed to shadow checkMovementStatistics()");
	}

	@Inject( at = @At( "HEAD" ), method = "eat", cancellable = true )
	public void dragonEat(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> ci){
		DragonStateProvider.getCap(this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				DragonFoodHandler.dragonEat(this.getFoodData(), itemStack.getItem(), itemStack, dragonStateHandler.getType());
				this.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
				if((Player)(Object)this instanceof ServerPlayer){
					CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)(Object)this, itemStack);
				}

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
}