package by.jackraidenph.dragonsurvival.handlers.Magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@EventBusSubscriber
public class ClawToolHandler
{
	
	public static class clawDamageSource extends EntityDamageSource{
		
		public clawDamageSource(@Nullable Entity p_i1567_2_)
		{
			super("player", p_i1567_2_);
		}
	}
	
	@SubscribeEvent
	public static void playerAttack(LivingAttackEvent event){
		PlayerEntity player = event.getSource().getEntity() instanceof PlayerEntity ? ((PlayerEntity)event.getSource().getEntity()) : null;
		LivingEntity target = event.getEntityLiving();
		
		if(event.getSource() instanceof clawDamageSource) return;
		if(player == null) return;
		if(!target.isAttackable()) return;
		if(!DragonStateProvider.isDragon(player)) return;
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			ItemStack mainStack = player.getMainHandItem();
			ItemStack sword = cap.getClawInventory().getClawsInventory().getItem(0);
			
			ModifiableAttributeInstance baseAttacks = player.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
			ModifiableAttributeInstance instance = new ModifiableAttributeInstance(Attributes.ATTACK_DAMAGE, (s) -> {});
			
			instance.addTransientModifier(new AttributeModifier("REMOVE_BASE_DMG", -1.0, Operation.ADDITION));
			
			if(!mainStack.isEmpty()){
				mainStack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach((c) -> {
					baseAttacks.removeModifier(c.getId());
				});
			}
			
			baseAttacks.getModifiers().forEach((mod) -> {
				if(!instance.hasModifier(mod)){
					instance.addTransientModifier(mod);
				}
			});
			
			float baseDamage = (float)instance.getValue();
			
			float mainDamage = baseDamage;
			float swordDamage = baseDamage;
			
			if (!mainStack.isEmpty()) {
				ModifiableAttributeInstance itemInstance = new ModifiableAttributeInstance(Attributes.ATTACK_DAMAGE, (s) -> {});
				mainStack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach((c) -> {
					if(!itemInstance.hasModifier(c)) {
						itemInstance.addTransientModifier(c);
					}
				});
				
				mainDamage += EnchantmentHelper.getDamageBonus(mainStack, target.getMobType()) + itemInstance.getValue();
			}
			
			if (!sword.isEmpty()) {
				ModifiableAttributeInstance itemInstance = new ModifiableAttributeInstance(Attributes.ATTACK_DAMAGE, (s) -> {});
				sword.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach((c) -> {
					if(!itemInstance.hasModifier(c)) {
						itemInstance.addTransientModifier(c);
					}
				});
				
				swordDamage += EnchantmentHelper.getDamageBonus(sword, target.getMobType()) + itemInstance.getValue();
			}
			
			//This is mostly reused code from PlayerEntity.attack(Entity)
			if (swordDamage >= mainDamage) {
				float f2 = player.getAttackStrengthScale(0.5F);
				float f1 = (float)player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
				f1 += (float)EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, sword);
				
				//TODO This needs to be fixed
				//swordDamage = swordDamage * (0.2F + f2 * f2 * 0.8F);
				f1 = f1 * f2;
				
				boolean flag = f2 > 0.9F;
				boolean flag1 = false;
				if (player.isSprinting() && flag) {
					player.level.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
					++f1;
					flag1 = true;
				}
				
				int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, sword);
				boolean flag4 = false;
				float f4 = target.getHealth();
				
				if(i > 0 && !target.isOnFire()) {
					flag4 = true;
					target.setSecondsOnFire(i * 4);
				}
				
				boolean flag2 = flag && player.fallDistance > 0.0F && !player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(Effects.BLINDNESS) && !player.isPassenger();
				flag2 = flag2 && !player.isSprinting();
				net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, target, flag2, flag2 ? 1.5F : 1.0F);
			
				flag2 = hitResult != null;
				
				if (flag2) {
					swordDamage *= hitResult.getDamageModifier();
				}
				
				boolean flag3 = false;
				double d0 = (double)(player.walkDist - player.walkDistO);
				if (flag && !flag2 && !flag1 && player.isOnGround() && d0 < (double)player.getSpeed()) {
					if (sword.getItem() instanceof SwordItem) {
						flag3 = true;
					}
				}
				Vector3d vector3d = target.getDeltaMovement();
				
				if (target.hurt(new clawDamageSource(player), swordDamage)) {
					if (f1 > 0.0F) {
						target.knockback(f1 * 0.5F, (double)MathHelper.sin(player.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(player.yRot * ((float)Math.PI / 180F))));
						player.setDeltaMovement(player.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
						player.setSprinting(false);
					}
					
					if (flag3) {
						float sweap = EnchantmentHelper.getSweepingDamageRatio(player);
						
						if(sweap > 0) {
							float f3 = 1.0F + sweap * swordDamage;
							
							for (LivingEntity livingentity : player.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
								if (livingentity != player && livingentity != target && !player.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingentity).isMarker()) && player.distanceToSqr(livingentity) < 9.0D) {
									livingentity.knockback(0.4F, (double)MathHelper.sin(player.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(player.yRot * ((float)Math.PI / 180F))));
									livingentity.hurt(DamageSource.playerAttack(player), f3);
								}
							}
							
							player.level.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
							player.sweepAttack();
						}
					}
					
					if (target instanceof ServerPlayerEntity && target.hurtMarked) {
						((ServerPlayerEntity)target).connection.send(new SEntityVelocityPacket(target));
						target.hurtMarked = false;
						target.setDeltaMovement(vector3d);
					}
					
					if (flag2) {
						player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
						player.crit(target);
					}
					
					if (!flag2 && !flag3) {
						if (flag) {
							player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, player.getSoundSource(), 1.0F, 1.0F);
						} else {
							player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
						}
					}
					
					if (f1 > 0.0F) {
						player.magicCrit(target);
					}
					
					player.setLastHurtMob(target);
					
					EnchantmentHelper.doPostHurtEffects(target, player);
					EnchantmentHelper.doPostDamageEffects(player, target);
					
					sword.getItem().onLeftClickEntity(sword, player, target);
					sword.getItem().hurtEnemy(sword, player, target);
					
					if(!player.level.isClientSide){
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncDragonClawsMenu(player.getId(), cap.getClawInventory().isClawsMenuOpen(), cap.getClawInventory().getClawsInventory()));
					}
					
					float f5 = f4 - target.getHealth();
					player.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
					
					if (i > 0) {
						target.setSecondsOnFire(i * 4);
					}
					
					if (player.level instanceof ServerWorld && f5 > 2.0F) {
						int k = (int)((double)f5 * 0.5D);
						((ServerWorld)player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY(0.5D), player.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
					}
					
					player.causeFoodExhaustion(0.1F);
				}
				
				event.setCanceled(true);
			}
		});
	}
	
	private static final UUID CLAW_ATTACK_SPEED = UUID.fromString("9dc963f3-e0fb-45ca-9206-15db030a481a");
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		if(event.phase == Phase.START) return;
		
		PlayerEntity player = event.player;
		
		ModifiableAttributeInstance attackSpeedInstance = player.getAttribute(Attributes.ATTACK_SPEED);
		
		if(player.getMainHandItem().getItem() instanceof SwordItem || player.getMainHandItem().getItem() instanceof AxeItem || player.getMainHandItem().getToolTypes().contains(ToolType.AXE)){
			if(attackSpeedInstance.getModifier(CLAW_ATTACK_SPEED) != null){
				attackSpeedInstance.removeModifier(CLAW_ATTACK_SPEED);
			}
			return;
		}
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if (!cap.isDragon()) return;
			ItemStack sword = cap.getClawInventory().getClawsInventory().getItem(0);
			
			if(sword.isEmpty()){
				if(attackSpeedInstance.getModifier(CLAW_ATTACK_SPEED) != null){
					attackSpeedInstance.removeModifier(CLAW_ATTACK_SPEED);
				}
			}else{
				Collection<AttributeModifier> attackSpeed = sword.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED);
				AttributeModifier modifier = attackSpeed.stream().findFirst().orElse(null);
				
				if(modifier != null){
					AttributeModifier attack_speed = new AttributeModifier(CLAW_ATTACK_SPEED, "CLAW_ATTACK_SPEED", modifier.getAmount(), modifier.getOperation());
					
					if(!attackSpeedInstance.hasModifier(attack_speed)){
						attackSpeedInstance.addTransientModifier(attack_speed);
					}
				}
			}
		});
	}
	
	@SubscribeEvent
	public static void playerHitEntity(CriticalHitEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
			});
		}
	}
	
	@SubscribeEvent
	public static void experiencePickup(PickupXp event){
		PlayerEntity player = event.getPlayer();
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			ArrayList<ItemStack> stacks = new ArrayList<>();
			
			for(int i = 0; i < 4; i++){
				ItemStack clawStack = cap.getClawInventory().getClawsInventory().getItem(i);
				int mending = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, clawStack);
				
				if(mending > 0 && clawStack.isDamaged()){
					stacks.add(clawStack);
				}
			}
			
			if(stacks.size() > 0) {
				ItemStack repairTime = stacks.get(player.level.random.nextInt(stacks.size()));
				if (!repairTime.isEmpty() && repairTime.isDamaged()) {
					
					int i = Math.min((int)(event.getOrb().value * repairTime.getXpRepairRatio()), repairTime.getDamageValue());
					event.getOrb().value -= i * 2;
					repairTime.setDamageValue(repairTime.getDamageValue() - i);
				}
			}
			
			event.getOrb().value = Math.max(0, event.getOrb().value);
		});
	}
	
	@SubscribeEvent
	public static void playerDieEvent(LivingDropsEvent event){
		Entity ent = event.getEntity();
		
		if(ent instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)ent;
			
			if(!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !ConfigHandler.SERVER.keepClawItems.get()){
				DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
				
				if(handler != null){
					for(int i = 0; i < handler.getClawInventory().getClawsInventory().getContainerSize(); i++){
						ItemStack stack = handler.getClawInventory().getClawsInventory().getItem(i);
						
						if(!stack.isEmpty()) {
							event.getDrops().add(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack));
							handler.getClawInventory().getClawsInventory().setItem(i, ItemStack.EMPTY);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void blockBroken(BlockEvent.BreakEvent breakEvent) {
		if(breakEvent.isCanceled()) return;
		
		PlayerEntity playerEntity = breakEvent.getPlayer();
		
		if(playerEntity.isCreative()) return;
		
		
		DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon()) {
				BlockState blockState = breakEvent.getState();
				float newSpeed = 0F;
				ItemStack hotbarItem = null;
				
				for(int i = 1; i < 4; i++){
					if(blockState.getHarvestTool() == DragonStateHandler.CLAW_TOOL_TYPES[i]){
						ItemStack breakingItem = dragonStateHandler.getClawInventory().getClawsInventory().getItem(i);
						if(!breakingItem.isEmpty()){
							float tempSpeed = breakingItem.getDestroySpeed(blockState) * 0.7F;
							
							if(tempSpeed > newSpeed){
								newSpeed = tempSpeed;
								hotbarItem = breakingItem;
							}
						}
					}
				}
				
				if(!playerEntity.getMainHandItem().isEmpty()){
					float tempSpeed = playerEntity.getMainHandItem().getDestroySpeed(blockState);
					if(tempSpeed > newSpeed){
						hotbarItem = null;
					}
				}
				
				if(hotbarItem != null) {
					hotbarItem.mineBlock(playerEntity.level, blockState, breakEvent.getPos(), playerEntity);
					//breakEvent.setCanceled(true);
					if (!playerEntity.level.isClientSide) {
						int exp = breakEvent.getExpToDrop();
						breakEvent.getState().getBlock().playerDestroy((World)breakEvent.getWorld(), playerEntity, breakEvent.getPos(), breakEvent.getState(), breakEvent.getWorld().getBlockEntity(breakEvent.getPos()), hotbarItem);
						
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new SyncDragonClawsMenu(playerEntity.getId(), dragonStateHandler.getClawInventory().isClawsMenuOpen(), dragonStateHandler.getClawInventory().getClawsInventory()));
						
						int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, hotbarItem);
						
						if (breakEvent.getWorld().setBlock(breakEvent.getPos(), Blocks.AIR.defaultBlockState(), 3) && exp > 0 && i <= 0) {
							if (!((World)breakEvent.getWorld()).isClientSide) {
								breakEvent.getState().getBlock().popExperience((ServerWorld)breakEvent.getWorld(), breakEvent.getPos(), exp);
							}
						}
					}
				}
			}
		});
	}
	
	@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Event_busHandler{
		@SubscribeEvent
		public void modifyBreakSpeed(PlayerEvent.BreakSpeed breakSpeedEvent) {
			if (!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.clawsAreTools.get())
				return;
			PlayerEntity playerEntity = breakSpeedEvent.getPlayer();
			
			DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
				if (dragonStateHandler.isDragon()) {
					ItemStack mainStack = playerEntity.getMainHandItem();
					BlockState blockState = breakSpeedEvent.getState();
					Item item = mainStack.getItem();
					
					float originalSpeed = breakSpeedEvent.getOriginalSpeed();
					float speed = originalSpeed;
					
					{
						float tempSpeed = playerEntity.inventory.getDestroySpeed(blockState);
						int efficiency = EnchantmentHelper.getBlockEfficiency(playerEntity);
						ItemStack itemstack = playerEntity.getMainHandItem();
						if (efficiency > 0 && !itemstack.isEmpty()) {
							tempSpeed += (float)(efficiency * efficiency + 1);
						}
						
						speed -= tempSpeed;
					}
					
					float newSpeed = 0F;
					ItemStack harvestTool = null;
					
					for (int i = 1; i < 4; i++) {
						if (blockState.getHarvestTool() == DragonStateHandler.CLAW_TOOL_TYPES[i]) {
							ItemStack breakingItem = dragonStateHandler.getClawInventory().getClawsInventory().getItem(i);
							if (!breakingItem.isEmpty()) {
								int effLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, breakingItem);
								float tempSpeed = breakingItem.getDestroySpeed(blockState);
								
								if(effLevel > 0){
									tempSpeed += (float)(effLevel * effLevel + 1);
								}
								
								if (tempSpeed > newSpeed) {
									newSpeed = tempSpeed;
									harvestTool = breakingItem;
								}
							}
						}
					}
					
					speed += newSpeed;
					
					if(newSpeed > originalSpeed){
						speed = newSpeed;
					}else if(speed <= 0){
						speed = originalSpeed;
					}
					
					if(newSpeed > originalSpeed && harvestTool != null){
						item = harvestTool.getItem();
					}
					
					if (!(item instanceof ToolItem || item instanceof SwordItem || item instanceof ShearsItem)) {
						float bonus = dragonStateHandler.getLevel() == DragonLevel.ADULT ? (
								blockState.getHarvestTool() == ToolType.AXE && dragonStateHandler.getType() == DragonType.FOREST ? 4 :
								blockState.getHarvestTool() == ToolType.PICKAXE && dragonStateHandler.getType() == DragonType.CAVE ? 4 :
								blockState.getHarvestTool() == ToolType.SHOVEL && dragonStateHandler.getType() == DragonType.SEA ? 4 : 2F
								) : dragonStateHandler.getLevel() == DragonLevel.BABY ? ConfigHandler.SERVER.bonusUnlockedAt.get() != DragonLevel.BABY ? 2F : 1F
								: dragonStateHandler.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.bonusUnlockedAt.get() == DragonLevel.ADULT && dragonStateHandler.getLevel() != DragonLevel.BABY ? 2F : 1F
								: 2F;
						
						breakSpeedEvent.setNewSpeed((speed * bonus));
					} else {
						breakSpeedEvent.setNewSpeed((speed * 0.7f));
					}
				}
			});
		}
	}
}
