package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Mixin( LivingEntity.class )
public abstract class MixinLivingEntity extends Entity{
	@Shadow
	protected ItemStack useItem;
	@Shadow
	protected int useItemRemaining;

	public MixinLivingEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_){
		super(p_i48580_1_, p_i48580_2_);
	}


//	/**
//	 * Makes lava act like water for cave dragons
//	 */
//	@Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"))
//	public boolean isInWater(LivingEntity instance){
//		if(instance instanceof Player player){
//			if(DragonUtils.isDragon(player)){
//				if(DragonUtils.getDragonType(player) == DragonType.CAVE){
//					return player.isInLava();
//				}
//			}
//		}
//
//		return instance.isInWater();
//	}

//	/**
//	 * Makes water act like lava for cave dragons
//	 */
//	@Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInLava()Z"))
//	public boolean isInLava(LivingEntity instance){
//		if(instance instanceof Player player){
//			if(DragonUtils.isDragon(player)){
//				if(DragonUtils.getDragonType(player) == DragonType.CAVE){
//					return player.isInWater();
//				}
//			}
//		}
//
//		return instance.isInWater();
//	}

	@Redirect( method = "collectEquipmentChanges", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;" ) )
	private ItemStack getDragonSword(LivingEntity entity, EquipmentSlot slotType){
		ItemStack mainStack = entity.getMainHandItem();

		if(slotType == EquipmentSlot.MAINHAND){
			DragonStateHandler cap = DragonUtils.getHandler(entity);

			if(!(mainStack.getItem() instanceof TieredItem) && cap != null){
				ItemStack sword = cap.getClawInventory().getClawsInventory().getItem(0);

				if(!sword.isEmpty()){
					return sword;
				}
			}

			return entity.getMainHandItem();
		}else if(slotType == EquipmentSlot.OFFHAND){
			return entity.getOffhandItem();
		}else{
			if(slotType.getType() == EquipmentSlot.Type.ARMOR && entity.getArmorSlots() != null && entity.getArmorSlots().iterator().hasNext() && entity.getArmorSlots().spliterator() != null){
				Stream<ItemStack> stream = StreamSupport.stream(entity.getArmorSlots().spliterator(), false);
				ArrayList<ItemStack> list = new ArrayList<>(stream.toList());
				return list.size() > slotType.getIndex() ? list.get(slotType.getIndex()) : ItemStack.EMPTY;
			}else{
				return ItemStack.EMPTY;
			}
		}
	}

	@Inject( at = @At( "HEAD" ), method = "Lnet/minecraft/world/entity/LivingEntity;rideableUnderWater()Z", cancellable = true )
	public void dragonRideableUnderWater(CallbackInfoReturnable<Boolean> ci){
		if(DragonUtils.isDragon(this)){
			ci.setReturnValue(true);
		}
	}

	@Inject( at = @At( "HEAD" ), method = "eat", cancellable = true )
	public void dragonEat(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> ci){
		DragonStateProvider.getCap(this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				if(DragonFoodHandler.isDragonEdible(itemStack.getItem(), dragonStateHandler.getType())){
					level.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(itemStack), SoundSource.NEUTRAL, 1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
					this.addEatEffect(itemStack, level, (LivingEntity)(Object)this);
					if(!((Object)this instanceof Player) || !((Player)(Object)this).getAbilities().instabuild){
						itemStack.shrink(1);
					}
				}
				ci.setReturnValue(itemStack);
			}
		});
	}

	@Shadow
	private void addEatEffect(ItemStack itemStack, Level level, LivingEntity object){
		throw new IllegalStateException("Mixin failed to shadow addEatEffect()");
	}

	@Shadow
	public SoundEvent getEatingSound(ItemStack itemStack){
		throw new IllegalStateException("Mixin failed to shadow getEatingSound()");
	}

	@Inject( at = @At( "HEAD" ), method = "addEatEffect", cancellable = true )
	public void addDragonEatEffect(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfo ci){
		DragonStateProvider.getCap(this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				Item item = itemStack.getItem();
				if(DragonFoodHandler.isDragonEdible(item, dragonStateHandler.getType())){

					for(Pair<MobEffectInstance, Float> pair : DragonFoodHandler.getDragonFoodProperties(item, dragonStateHandler.getType()).getEffects()){
						if(!level.isClientSide && pair.getFirst() != null){
							if(!level.isClientSide && pair.getFirst() != null && pair.getFirst().getEffect() != MobEffects.HUNGER && level.random.nextFloat() < pair.getSecond()){
								livingEntity.addEffect(new MobEffectInstance(pair.getFirst()));
							}
							if(pair.getFirst().getEffect() == MobEffects.HUNGER){
								if(livingEntity.hasEffect(MobEffects.HUNGER)){
									switch(livingEntity.getEffect(MobEffects.HUNGER).getAmplifier()){
										case 0:
											livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, pair.getFirst().getDuration(), pair.getFirst().getAmplifier() + 1));
											if(level.random.nextFloat() < 0.25F){
												livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, pair.getFirst().getDuration(), 0));
											}
											break;
										case 1:
											livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, pair.getFirst().getDuration(), pair.getFirst().getAmplifier() + 2));
											if(level.random.nextFloat() < 0.5F){
												livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, pair.getFirst().getDuration(), 0));
											}
											break;
										default:
											livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, pair.getFirst().getDuration(), pair.getFirst().getAmplifier() + 2));
											livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, pair.getFirst().getDuration(), 0));
									}
								}else if(level.random.nextFloat() < pair.getSecond()){
									livingEntity.addEffect(new MobEffectInstance(pair.getFirst()));
								}
							}
						}
					}
				}
				ci.cancel();
			}
		});
	}

	@Inject( at = @At( "HEAD" ), method = "shouldTriggerItemUseEffects", cancellable = true )
	public void shouldDragonTriggerItemUseEffects(CallbackInfoReturnable<Boolean> ci){
		DragonStateProvider.getCap(this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				int i = this.getUseItemRemainingTicks();
				FoodProperties food = this.useItem.getItem().getFoodProperties();
				boolean flag = food != null && food.isFastFood();
				flag = flag || i <= DragonFoodHandler.getUseDuration(this.useItem, dragonStateHandler.getType()) - 7;
				ci.setReturnValue(flag && i % 4 == 0);
			}
		});
	}

	@Shadow
	public int getUseItemRemainingTicks(){
		throw new IllegalStateException("Mixin failed to shadow getUseItemRemainingTicks()");
	}

	@Inject( at = @At( value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I", shift = Shift.AFTER ), method = "onSyncedDataUpdated" )
	public void onDragonSyncedDataUpdated(EntityDataAccessor<?> data, CallbackInfo ci){
		DragonStateProvider.getCap(this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				this.useItemRemaining = DragonFoodHandler.getUseDuration(this.useItem, dragonStateHandler.getType());
			}
		});
	}

	@Inject( at = @At( value = "HEAD" ), method = "triggerItemUseEffects", cancellable = true )
	public void triggerDragonItemUseEffects(ItemStack stack, int count, CallbackInfo ci){
		DragonStateProvider.getCap(this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && !stack.isEmpty() && this.isUsingItem() && stack.getUseAnimation() == UseAnim.NONE && DragonFoodHandler.isDragonEdible(stack.getItem(), dragonStateHandler.getType())){
				this.spawnItemParticles(stack, count);
				this.playSound(this.getEatingSound(stack), 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				ci.cancel();
			}
		});
	}

	@Shadow
	private void spawnItemParticles(ItemStack stack, int count){
		throw new IllegalStateException("Mixin failed to shadow spawnItemParticles()");
	}

	@Shadow
	public boolean isUsingItem(){
		throw new IllegalStateException("Mixin failed to shadow isUsingItem()");
	}
}