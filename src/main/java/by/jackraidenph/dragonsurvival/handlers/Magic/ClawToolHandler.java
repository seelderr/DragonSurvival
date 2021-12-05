package by.jackraidenph.dragonsurvival.handlers.Magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.ArrayList;
@EventBusSubscriber
public class ClawToolHandler
{
	public static boolean hasEmptySlot(PlayerEntity player){
      boolean hasEmptySlot = false;
        for(int slot = 0; slot < 9; slot++){
            ItemStack hotbarStack = player.inventory.getItem(slot);
            
            if(hotbarStack.isEmpty()){
                hasEmptySlot = true;
                break;
            }
        }
		
		return hasEmptySlot;
	}
	
	@SubscribeEvent
	public static void playerAttack(AttackEntityEvent event){
		if(!(event.getTarget() instanceof LivingEntity)) return;
		
		PlayerEntity player = (PlayerEntity)event.getPlayer().getEntity();
		LivingEntity target = (LivingEntity)event.getTarget();
		
		if(!hasEmptySlot(player)) return;
		
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			ItemStack mainStack = player.getMainHandItem();
			ItemStack sword = cap.getClawInventory().getClawsInventory().getItem(0);
			
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
					sword.getItem().onLeftClickEntity(sword, player, target);
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
		});
	}
	
	@SubscribeEvent
	public static void playerDieEvent(LivingDropsEvent event){
		Entity ent = event.getEntity();
		
		if(ent instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)ent;
			
			if(!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)){
				DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
				
				if(handler != null){
					for(int i = 0; i < handler.getClawInventory().getClawsInventory().getContainerSize(); i++){
						ItemStack stack = handler.getClawInventory().getClawsInventory().getItem(i);
						
						if(!stack.isEmpty()) {
							player.level.addFreshEntity(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack));
						}
						
						handler.getClawInventory().getClawsInventory().setItem(i, ItemStack.EMPTY);
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
		if(!hasEmptySlot(playerEntity)) return;
		
		
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
				
				if(hotbarItem != null && !playerEntity.level.isClientSide){
					int exp = breakEvent.getExpToDrop();
					hotbarItem.mineBlock(playerEntity.level, blockState, breakEvent.getPos(), playerEntity);
					breakEvent.getState().getBlock().playerDestroy((World)breakEvent.getWorld(), playerEntity, breakEvent.getPos(), breakEvent.getState(), breakEvent.getWorld().getBlockEntity(breakEvent.getPos()), hotbarItem);
					
					int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, hotbarItem);
					
					if (breakEvent.getWorld().setBlock(breakEvent.getPos(), Blocks.AIR.defaultBlockState(), 3) && exp > 0 && i <= 0) {
						if(!((World)breakEvent.getWorld()).isClientSide) {
							breakEvent.getState().getBlock().popExperience((ServerWorld)breakEvent.getWorld(), breakEvent.getPos(), exp);
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
					
					float speed = breakSpeedEvent.getOriginalSpeed();
					float newSpeed = 0F;
					
					if(hasEmptySlot(playerEntity)) {
						for (int i = 1; i < 4; i++) {
							if (blockState.getHarvestTool() == DragonStateHandler.CLAW_TOOL_TYPES[i]) {
								ItemStack breakingItem = dragonStateHandler.getClawInventory().getClawsInventory().getItem(i);
								if (!breakingItem.isEmpty()) {
									float tempSpeed = breakingItem.getDestroySpeed(blockState) * 0.7F;
									
									if (tempSpeed > newSpeed) {
										newSpeed = tempSpeed;
									}
								}
							}
						}
						
						if (!playerEntity.getMainHandItem().isEmpty()) {
							float tempSpeed = playerEntity.getMainHandItem().getDestroySpeed(blockState);
							
							if (tempSpeed > newSpeed) {
								newSpeed = 0;
							}
						}
					}
					
					
					if (!(item instanceof ToolItem || item instanceof SwordItem || item instanceof ShearsItem)) {
						switch (dragonStateHandler.getLevel()) {
							case BABY:
								if (ConfigHandler.SERVER.bonusUnlockedAt.get() != DragonLevel.BABY) {
									breakSpeedEvent.setNewSpeed((speed * 2.0F) + newSpeed);
									break;
								}
							case YOUNG:
								if (ConfigHandler.SERVER.bonusUnlockedAt.get() == DragonLevel.ADULT && dragonStateHandler.getLevel() != DragonLevel.BABY) {
									breakSpeedEvent.setNewSpeed((speed * 2.0F) + newSpeed);
									break;
								}
							case ADULT:
								switch (dragonStateHandler.getType()) {
									case FOREST:
										if (blockState.getHarvestTool() == ToolType.AXE) {
											breakSpeedEvent.setNewSpeed((speed * 4.0F) + newSpeed);
										} else breakSpeedEvent.setNewSpeed((speed * 2.0F) + newSpeed);
										break;
									case CAVE:
										if (blockState.getHarvestTool() == ToolType.PICKAXE) {
											breakSpeedEvent.setNewSpeed((speed * 4.0F) + newSpeed);
										} else breakSpeedEvent.setNewSpeed((speed * 2.0F) + newSpeed);
										break;
									case SEA:
										if (blockState.getHarvestTool() == ToolType.SHOVEL) {
											breakSpeedEvent.setNewSpeed((speed * 4.0F) + newSpeed);
										} else breakSpeedEvent.setNewSpeed((speed * 2.0F) + newSpeed);
										if (playerEntity.isInWaterOrBubble()) {
											breakSpeedEvent.setNewSpeed((speed * 1.4f) + newSpeed);
										}
										break;
								}
								break;
						}
					} else {
						breakSpeedEvent.setNewSpeed((speed * 0.7f) + newSpeed);
					}
				}
			});
		}
	}
}
