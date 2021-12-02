package by.jackraidenph.dragonsurvival.handlers.Magic;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.Capabilities;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.abilities.Actives.BreathAbilities.LightningBreathAbility;
import by.jackraidenph.dragonsurvival.magic.abilities.Passives.BurnAbility;
import by.jackraidenph.dragonsurvival.magic.abilities.Passives.SpectralImpactAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.magic.SyncPotionAddedEffect;
import by.jackraidenph.dragonsurvival.network.magic.SyncPotionRemovedEffect;
import by.jackraidenph.dragonsurvival.registration.BlockInit;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;

import java.util.ArrayList;

@EventBusSubscriber
public class MagicHandler
{
	public static AbilityTickingHandler cooldownHandler = new AbilityTickingHandler();
	
	public static boolean isPlayerInGoodConditions(PlayerEntity player){
		BlockState blockBelow = player.level.getBlockState(player.blockPosition().below());
		BlockState feetBlock = player.getFeetBlockState();
		
		return DragonStateProvider.getCap(player).map(cap -> {
			switch (cap.getType()) {
				case SEA:
					if (player.isInWaterRainOrBubble() || blockBelow.getMaterial() == Material.SNOW || blockBelow.getMaterial() == Material.WATER || blockBelow.getBlock() == Blocks.WET_SPONGE
							|| blockBelow.getMaterial() == Material.ICE || player.hasEffect(DragonEffects.CHARGED) || player.hasEffect(DragonEffects.PEACE)) {
						return true;
					}
					
					if(blockBelow.getBlock() == BlockInit.smallSeaNest || blockBelow.getBlock() == BlockInit.mediumSeaNest || blockBelow.getBlock() == BlockInit.bigSeaNest){
						return true;
					}
					
					if(blockBelow.getBlock() == Blocks.CAULDRON){
						if(blockBelow.hasProperty(CauldronBlock.LEVEL)) {
							int level = blockBelow.getValue(CauldronBlock.LEVEL);
							
							if(level > 0){
								return true;
							}
						}
					}
					
					if(feetBlock.getBlock() == Blocks.CAULDRON){
						if(feetBlock.hasProperty(CauldronBlock.LEVEL)) {
							int level = feetBlock.getValue(CauldronBlock.LEVEL);
							
							if(level > 0){
								return true;
							}
						}
					}
					
					break;
				
				case FOREST:
					if(!player.level.canSeeSky(player.blockPosition()) || !player.level.isDay()){
						return false;
					}
					
					if (player.hasEffect(DragonEffects.DRAIN) || player.hasEffect(DragonEffects.MAGIC)
					    || blockBelow.getMaterial() == Material.PLANT || blockBelow.getMaterial() == Material.REPLACEABLE_PLANT
					    || feetBlock.getMaterial() == Material.PLANT || feetBlock.getMaterial() == Material.REPLACEABLE_PLANT)  {
						return true;
					}
					
					if(blockBelow.getBlock() == BlockInit.smallForestNest || blockBelow.getBlock() == BlockInit.mediumForestNest || blockBelow.getBlock() == BlockInit.bigForestNest){
						return true;
					}
					
					break;
				
				case CAVE:
					if (player.isInLava() || blockBelow.getMaterial() == Material.LAVA || blockBelow.getMaterial() == Material.FIRE || player.isOnFire()
					    || blockBelow.getBlock() == Blocks.CAMPFIRE || blockBelow.getBlock() == Blocks.SOUL_CAMPFIRE || blockBelow.getBlock() == Blocks.BLAST_FURNACE
						|| blockBelow.getBlock() == Blocks.SMOKER || blockBelow.getBlock() == Blocks.FURNACE || blockBelow.getBlock() == Blocks.MAGMA_BLOCK
						|| player.hasEffect(DragonEffects.BURN) || player.hasEffect(DragonEffects.FIRE)) {
						return true;
					}
					
					if(blockBelow.getBlock() == BlockInit.smallCaveNest || blockBelow.getBlock() == BlockInit.mediumCaveNest || blockBelow.getBlock() == BlockInit.bigCaveNest){
						return true;
					}
					
					//If cave dragon is ontop of a burning furnace
					if(blockBelow.getBlock() instanceof AbstractFurnaceBlock){
						if(blockBelow.hasProperty(AbstractFurnaceBlock.LIT)) {
							if (blockBelow.getValue(AbstractFurnaceBlock.LIT)) {
								return true;
							}
						}
					}
					
					break;
			}
			
			return false;
		}).orElse(false);
	}
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		PlayerEntity player = event.player;
		
		if(!player.level.isClientSide) {
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				boolean goodConditions = isPlayerInGoodConditions(player);
				
				int timeToRecover = goodConditions ? 5 : 15;
				
				if (player.tickCount % Functions.secondsToTicks(timeToRecover) == 0) {
					if (cap.lastTick == -1 || cap.lastTick != player.tickCount) {
						cap.lastTick = player.tickCount; //It was activating twice for some reason
						if (cap.getCurrentMana() < DragonStateProvider.getMaxMana(player)) {
							DragonStateProvider.replenishMana(player, 1);
						}
					}
				}
			});
		}
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if (!cap.isDragon()) return;
			
			for (DragonAbility ability : cap.getAbilities()) {
				ability.player = player;
			}
			
			if (player.hasEffect(DragonEffects.HUNTER)) {
				BlockState bl = player.getFeetBlockState();
				BlockState below = player.level.getBlockState(player.blockPosition().below());
				
				if (bl.getMaterial() == Material.PLANT || bl.getMaterial() == Material.REPLACEABLE_PLANT || bl.getMaterial() == Material.GRASS || below.getMaterial() == Material.PLANT || below.getMaterial() == Material.REPLACEABLE_PLANT) {
					player.addEffect(new EffectInstance(Effects.INVISIBILITY, 10, 0, false, false));
				}
				
				player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 20, 2, false, false));
			}
			
			if(!player.isCreative()) {
				if (cap.getCurrentlyCasting() != null && cap.getCurrentlyCasting().getCastingSlowness() > 0) {
					player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 10, cap.getCurrentlyCasting().getCastingSlowness(), false, false));
					player.addEffect(new EffectInstance(Effects.JUMP, 10, -cap.getCurrentlyCasting().getCastingSlowness(), false, false));
					player.addEffect(new EffectInstance(Effects.SLOW_FALLING, 10, 0, false, false));
				}
			}
		});
	}
	
	@SubscribeEvent
	public static void livingVisibility(LivingVisibilityEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.HUNTER)) {
					event.modifyVisibility(0);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void livingTick(LivingUpdateEvent event){
		LivingEntity entity = event.getEntityLiving();
		
		if (entity.hasEffect(DragonEffects.BURN)) {
			if (entity.isEyeInFluid(FluidTags.WATER) || entity.isInWaterRainOrBubble()) {
				entity.removeEffect(DragonEffects.BURN);
			}
		}
		
		
		if(entity.hasEffect(DragonEffects.DRAIN)){
			DragonType type = DragonStateProvider.getCap(entity).map( cap -> cap.getType()).orElse(null);
			
			if(type != DragonType.FOREST){
				if (entity.tickCount % 20 == 0) {
					entity.hurt(DamageSource.MAGIC, 1.0F);
				}
			}
		}
		
		if(entity.hasEffect(DragonEffects.CHARGED)){
			if (entity.tickCount % 20 == 0) {
				DragonType type = DragonStateProvider.getCap(entity).map(cap -> cap.getType()).orElse(null);
				
				if (type != DragonType.SEA) {
					LightningBreathAbility.chargedEffectSparkle(entity, 6, 2, 1);
				}
			}
		}
		
		Capabilities.getGenericCapability(entity).ifPresent(cap -> {
			if (entity.tickCount % 20 == 0) {
				if (entity.hasEffect(DragonEffects.BURN)) {
					if(!entity.fireImmune()){
						if (cap.lastPos != null) {
							double distance = entity.distanceToSqr(cap.lastPos);
							float damage = MathHelper.clamp((float)distance, 0, 10);
							
							if (damage > 0) {
								//Short enough fire duration to not cause fire damage but still drop cooked items
								if(!entity.isOnFire()){
									entity.setRemainingFireTicks(1);
								}
								
								entity.hurt(DamageSource.ON_FIRE, damage);
							}
						}
					}
				}
				
				cap.lastPos = entity.position();
			}
		});
	}
	
	@SubscribeEvent
	public static void playerStruckByLightning(EntityStruckByLightningEvent event){
		if(event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntity();
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if(cap.getType() == DragonType.SEA){
					event.setCanceled(true);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void playerDamaged(LivingDamageEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			LivingEntity target = event.getEntityLiving();
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.HUNTER)) {
					player.removeEffect(DragonEffects.HUNTER);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void playerHitEntity(CriticalHitEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.HUNTER)) {
					EffectInstance hunter = player.getEffect(DragonEffects.HUNTER);
					player.removeEffect(DragonEffects.HUNTER);
					event.setDamageModifier((hunter.getAmplifier() + 1) * 1.5F);
					event.setResult(Result.ALLOW);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void livingHurt(LivingAttackEvent event){
		if(event.getSource() instanceof EntityDamageSource && !(event.getSource() instanceof IndirectEntityDamageSource)) {
			if(event.getEntity() instanceof LivingEntity){
			if (event.getSource() != null && event.getSource().getEntity() != null) {
				if (event.getSource().getEntity() instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity)event.getSource().getEntity();
					LivingEntity target = (LivingEntity)event.getEntity();
					DragonStateProvider.getCap(player).ifPresent(cap -> {
						if (!cap.isDragon()) return;
						
						if (cap.getType() == DragonType.SEA) {
							SpectralImpactAbility spectralImpact = (SpectralImpactAbility)cap.getAbilityOrDefault(DragonAbilities.SPECTRAL_IMPACT);
							boolean hit = player.level.random.nextInt(100) <= spectralImpact.getChance();
							
							if (hit) {
								event.getSource().bypassArmor();
							}
						} else if (cap.getType() == DragonType.CAVE) {
							BurnAbility burnAbility = (BurnAbility)cap.getAbilityOrDefault(DragonAbilities.BURN);
							boolean hit = player.level.random.nextInt(100) <= burnAbility.getChance();
							
							if (hit) {
								((LivingEntity)event.getEntity()).addEffect(new EffectInstance(DragonEffects.BURN, Functions.secondsToTicks(30)));
							}
						}
						
					});
					
				}
			}
			}
		}
	}
	
	@SubscribeEvent
	public static void playerAttack(AttackEntityEvent event){
		if(!(event.getTarget() instanceof LivingEntity)) return;
		
		PlayerEntity player = (PlayerEntity)event.getPlayer().getEntity();
		LivingEntity target = (LivingEntity)event.getTarget();
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			ItemStack mainStack = player.getMainHandItem();
			ItemStack sword = cap.clawsInventory.getItem(0);
			
			float mainDamage = 0;
			float swordDamage = 0;
			
			if (!mainStack.isEmpty() && mainStack.getItem() instanceof SwordItem) {
				mainDamage = ((SwordItem)mainStack.getItem()).getDamage();
				mainDamage += EnchantmentHelper.getDamageBonus(mainStack, target.getMobType());
			}
			
			if (!sword.isEmpty() && sword.getItem() instanceof SwordItem) {
				swordDamage = ((SwordItem)sword.getItem()).getDamage();
				swordDamage += EnchantmentHelper.getDamageBonus(sword, target.getMobType());
			}
			
			if (swordDamage > mainDamage) {
				float f = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
				float f1 = (float)player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
				f += DragonStateHandler.buildDamageMod(cap, cap.isDragon()).getAmount();
				
				f += EnchantmentHelper.getDamageBonus(sword, target.getMobType());
				f1 += (float)EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, sword);
				
				int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, sword);
				
				if (i > 0) {
					target.setSecondsOnFire(i * 4);
				}
				
				boolean flag = target.hurt(DamageSource.playerAttack(player), f);
				
				if (flag) {
					if (f1 > 0.0F) {
						target.knockback(f1 * 0.5F, (double)MathHelper.sin(target.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(target.yRot * ((float)Math.PI / 180F))));
						target.setDeltaMovement(target.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
					}
					((SwordItem)sword.getItem()).hurtEnemy(sword, player, target);
					
					player.doEnchantDamageEffects(player, target);
					target.setLastHurtMob(player);
				}
				
				event.setCanceled(true);
			}
		});
	}
	
	@SubscribeEvent
	public static void experiencePickup(PickupXp event){
		PlayerEntity player = event.getPlayer();
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			ArrayList<ItemStack> stacks = new ArrayList<>();
			
			for(int i = 0; i < 4; i++){
				ItemStack clawStack = cap.clawsInventory.getItem(i);
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
		});
	}
	
	@SubscribeEvent
	public static void experienceDrop(LivingExperienceDropEvent event){
		PlayerEntity player = event.getAttackingPlayer();
		
		if(player != null) {
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				if (!cap.isDragon()) return;
				
				if (player.hasEffect(DragonEffects.REVEALING_THE_SOUL)) {
					int extra = Math.min(20, event.getDroppedExperience()); //TODO Change this to a config option for max exp gain
					event.setDroppedExperience(event.getDroppedExperience() + extra);
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void potionAdded(PotionAddedEvent event){
		if(event.getPotionEffect().getEffect() != DragonEffects.DRAIN && event.getPotionEffect().getEffect() != DragonEffects.CHARGED && event.getPotionEffect().getEffect() != DragonEffects.BURN){
			return;
		}
		
		LivingEntity entity = event.getEntityLiving();
		
		if(!entity.level.isClientSide){
			TargetPoint point = new TargetPoint(entity.position().x, entity.position().y, entity.position().z, 64, entity.level.dimension());
			NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncPotionAddedEffect(entity.getId(), Effect.getId(event.getPotionEffect().getEffect()), event.getPotionEffect().getDuration(), event.getPotionEffect().getAmplifier()));
		}
	}
	
	@SubscribeEvent
	public static void potionRemoved(PotionExpiryEvent event){
		if(event.getPotionEffect().getEffect() != DragonEffects.DRAIN && event.getPotionEffect().getEffect() != DragonEffects.CHARGED && event.getPotionEffect().getEffect() != DragonEffects.BURN){
			return;
		}
		
		LivingEntity entity = event.getEntityLiving();
		
		if(!entity.level.isClientSide){
			TargetPoint point = new TargetPoint(entity.position().x, entity.position().y, entity.position().z, 64, entity.level.dimension());
			NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncPotionRemovedEffect(entity.getId(), Effect.getId(event.getPotionEffect().getEffect())));
		}
	}
}
