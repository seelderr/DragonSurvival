package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

	@Inject( method = "isImmobile", at = @At( "HEAD" ), cancellable = true )
	private void castMovement(CallbackInfoReturnable<Boolean> ci){
		DragonStateHandler cap = DragonUtils.getHandler(this);

		if(!isDeadOrDying() && !isSleeping()){
			if(!((Player)(LivingEntity)this).isCreative()){
				if(cap.getMagic().getCurrentlyCasting() != null){
					if(!cap.getMagic().getCurrentlyCasting().requiresStationaryCasting()){
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
}