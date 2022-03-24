<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/magic/ClawToolHandler.java
package by.jackraidenph.dragonsurvival.common.handlers.magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
=======
package by.dragonsurvivalteam.dragonsurvival.common.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/magic/ClawToolHandler.java
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class ClawToolHandler{
	public static ConcurrentHashMap<Integer, ItemStack> destroyedItems = new ConcurrentHashMap<>();

	@SubscribeEvent
	public static void experiencePickup(PickupXp event){
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/magic/ClawToolHandler.java
		Player player = event.getPlayer();
		
=======
		PlayerEntity player = event.getPlayer();

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/magic/ClawToolHandler.java
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			ArrayList<ItemStack> stacks = new ArrayList<>();

			for(int i = 0; i < 4; i++){
				ItemStack clawStack = cap.getClawInventory().getClawsInventory().getItem(i);
				int mending = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, clawStack);

				if(mending > 0 && clawStack.isDamaged()){
					stacks.add(clawStack);
				}
			}

			if(stacks.size() > 0){
				ItemStack repairTime = stacks.get(player.level.random.nextInt(stacks.size()));
				if(!repairTime.isEmpty() && repairTime.isDamaged()){

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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/magic/ClawToolHandler.java
		
		if(ent instanceof Player){
			Player player = (Player)ent;
			
=======

		if(ent instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)ent;

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/magic/ClawToolHandler.java
			if(!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !ConfigHandler.SERVER.keepClawItems.get()){
				DragonStateHandler handler = DragonUtils.getHandler(player);

				if(handler != null){
					for(int i = 0; i < handler.getClawInventory().getClawsInventory().getContainerSize(); i++){
						ItemStack stack = handler.getClawInventory().getClawsInventory().getItem(i);

						if(!stack.isEmpty()){
							event.getDrops().add(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack));
							handler.getClawInventory().getClawsInventory().setItem(i, ItemStack.EMPTY);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void dropBlocksMinedByPaw(PlayerEvent.HarvestCheck harvestCheck){
		if(!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.clawsAreTools.get()){
			return;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/magic/ClawToolHandler.java
	    Player playerEntity = harvestCheck.getPlayer();
	    DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
	        if (dragonStateHandler.isDragon()) {
	            ItemStack stack = playerEntity.getMainHandItem();
	            Item item = stack.getItem();
	            BlockState blockState = harvestCheck.getTargetBlock();
	            if (!(item instanceof DiggerItem || item instanceof SwordItem || item instanceof ShearsItem) && !harvestCheck.canHarvest()) {
		            harvestCheck.setCanHarvest(dragonStateHandler.canHarvestWithPaw(playerEntity, blockState));
		        }
	        }
	    });
	}
	
	public static ItemStack getDragonTools(Player player)
	{
		ItemStack mainStack = player.getInventory().getSelected();
		ItemStack harvestTool = mainStack;
		float newSpeed = 0F;
		
		DragonStateHandler cap = DragonStateProvider.getCap(player).orElse(null);
		
		if ((mainStack.getItem() instanceof DiggerItem || mainStack.getItem() instanceof SwordItem || mainStack.getItem() instanceof ShearsItem || (mainStack.getItem() instanceof TieredItem))) {
			return mainStack;
		}
		
		if(cap != null) {
			Level world = player.level;
			BlockHitResult raytraceresult = Item.getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
			
			if (raytraceresult.getType() != HitResult.Type.MISS) {
=======
		}
		PlayerEntity playerEntity = harvestCheck.getPlayer();
		DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				ItemStack stack = playerEntity.getMainHandItem();
				Item item = stack.getItem();
				BlockState blockState = harvestCheck.getTargetBlock();
				if(!(item instanceof ToolItem || item instanceof SwordItem || item instanceof ShearsItem) && !harvestCheck.canHarvest()){
					harvestCheck.setCanHarvest(dragonStateHandler.canHarvestWithPaw(playerEntity, blockState));
				}
			}
		});
	}

	public static ItemStack getDragonTools(PlayerEntity player){
		ItemStack mainStack = player.inventory.getSelected();
		ItemStack harvestTool = mainStack;
		float newSpeed = 0F;

		DragonStateHandler cap = DragonUtils.getHandler(player);

		if((mainStack.getItem() instanceof ToolItem || mainStack.getItem() instanceof SwordItem || mainStack.getItem() instanceof ShearsItem || (mainStack.getItem() instanceof TieredItem))){
			return mainStack;
		}

		if(cap != null){
			World world = player.level;
			BlockRayTraceResult raytraceresult = Item.getPlayerPOVHitResult(world, player, FluidMode.NONE);

			if(raytraceresult.getType() != RayTraceResult.Type.MISS){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/magic/ClawToolHandler.java
				BlockState state = world.getBlockState(raytraceresult.getBlockPos());

				if(state != null){
					for(int i = 1; i < 4; i++){
						ItemStack breakingItem = cap.getClawInventory().getClawsInventory().getItem(i);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/magic/ClawToolHandler.java
						
						if (!breakingItem.isEmpty() && breakingItem.isCorrectToolForDrops(state)) {
							float tempSpeed = breakingItem.getDestroySpeed(state);
							
							if(breakingItem.getItem() instanceof DiggerItem){
								DiggerItem item = (DiggerItem)breakingItem.getItem();
=======

						if(!breakingItem.isEmpty() && breakingItem.getToolTypes().stream().anyMatch(state::isToolEffective)){
							float tempSpeed = breakingItem.getDestroySpeed(state);

							if(breakingItem.getItem() instanceof ToolItem){
								ToolItem item = (ToolItem)breakingItem.getItem();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/magic/ClawToolHandler.java
								tempSpeed = item.getDestroySpeed(breakingItem, state);
							}

							if(tempSpeed > newSpeed){
								newSpeed = tempSpeed;
								harvestTool = breakingItem;
							}
						}
					}
				}
			}
		}

		return harvestTool;
	}

	public static ItemStack getWeapon(LivingEntity entity, ItemStack mainStack){
		DragonStateHandler cap = DragonStateProvider.getCap(entity).orElse(null);

		if(!(mainStack.getItem() instanceof TieredItem) && cap != null){
			ItemStack sword = cap.getClawInventory().getClawsInventory().getItem(0);

			if(!sword.isEmpty()){
				return sword;
			}
		}
		return null;
	}

	@SubscribeEvent
	public static void onToolBreak(PlayerDestroyItemEvent event){
		destroyedItems.put(event.getEntityLiving().getId(), event.getOriginal());
	}

	@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD )
	public static class Event_busHandler{
		@SubscribeEvent
		public void modifyBreakSpeed(PlayerEvent.BreakSpeed breakSpeedEvent){
			if(!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.clawsAreTools.get()){
				return;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/magic/ClawToolHandler.java
			Player playerEntity = breakSpeedEvent.getPlayer();
			
=======
			}
			PlayerEntity playerEntity = breakSpeedEvent.getPlayer();

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/magic/ClawToolHandler.java
			ItemStack mainStack = playerEntity.getMainHandItem();
			DragonStateHandler dragonStateHandler = DragonStateProvider.getCap(playerEntity).orElse(null);
			if(mainStack.getItem() instanceof TieredItem || dragonStateHandler == null || !dragonStateHandler.isDragon()){
				return;
			}

			BlockState blockState = breakSpeedEvent.getState();

			float originalSpeed = breakSpeedEvent.getOriginalSpeed();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/magic/ClawToolHandler.java
			
			if (!(mainStack.getItem() instanceof DiggerItem || mainStack.getItem() instanceof SwordItem || mainStack.getItem() instanceof ShearsItem || (mainStack.getItem() instanceof TieredItem))) {
				for (int i = 1; i < 4; i++) {
					ItemStack breakingItem = dragonStateHandler.getClawInventory().getClawsInventory().getItem(i);
					if (breakingItem.getItem().isCorrectToolForDrops(breakingItem, blockState)) {
						if (breakingItem != null && !breakingItem.isEmpty()) {
							return;
						}
					}
				}
			}
			
			if (!(mainStack.getItem() instanceof DiggerItem || mainStack.getItem() instanceof SwordItem || mainStack.getItem() instanceof ShearsItem || (mainStack.getItem() instanceof TieredItem))) {
				float bonus = dragonStateHandler.getLevel() == DragonLevel.ADULT ? (
						blockState.is(BlockTags.MINEABLE_WITH_AXE) &&dragonStateHandler.getType() == DragonType.FOREST ? 4 :
						blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) && dragonStateHandler.getType() == DragonType.CAVE ? 4 :
						blockState.is(BlockTags.MINEABLE_WITH_SHOVEL) && dragonStateHandler.getType() == DragonType.SEA ? 4 : 2F
						) : dragonStateHandler.getLevel() == DragonLevel.BABY ? ConfigHandler.SERVER.bonusUnlockedAt.get() == DragonLevel.BABY ? 2F : 1F
						: dragonStateHandler.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.bonusUnlockedAt.get() != DragonLevel.ADULT ? 2F : 1F
						: 2F;
				
=======

			if(!(mainStack.getItem() instanceof ToolItem || mainStack.getItem() instanceof SwordItem || mainStack.getItem() instanceof ShearsItem || (mainStack.getItem() instanceof TieredItem))){
				float bonus = dragonStateHandler.getLevel() == DragonLevel.ADULT
					? (blockState.getHarvestTool() == ToolType.AXE && dragonStateHandler.getType() == DragonType.FOREST ? 4 : blockState.getHarvestTool() == ToolType.PICKAXE && dragonStateHandler.getType() == DragonType.CAVE ? 4 : blockState.getHarvestTool() == ToolType.SHOVEL && dragonStateHandler.getType() == DragonType.SEA ? 4 : 2F)
					: dragonStateHandler.getLevel() == DragonLevel.BABY ? ConfigHandler.SERVER.bonusUnlockedAt.get() == DragonLevel.BABY ? 2F : 1F : dragonStateHandler.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.bonusUnlockedAt.get() != DragonLevel.ADULT ? 2F : 1F : 2F;

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/magic/ClawToolHandler.java
				breakSpeedEvent.setNewSpeed((originalSpeed * bonus));
			}
		}
	}
}