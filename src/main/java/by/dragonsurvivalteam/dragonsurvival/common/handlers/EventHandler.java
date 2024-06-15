package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.container.AllowOpenDragonAltar;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncPlayerJump;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "unused" )
@EventBusSubscriber
public class EventHandler{


	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event){
		if(!ServerConfig.startWithDragonChoice) return;
		if(event.getEntity().level().isClientSide()) return;

		if(event.getEntity() instanceof ServerPlayer player){
			if(player.isDeadOrDying()) return;

			if(player.tickCount > 5 * 20){
				DragonStateProvider.getCap(player).ifPresent(cap -> {
					if(!cap.hasUsedAltar && !DragonStateProvider.isDragon(player)){
						PacketDistributor.sendToPlayer(player, new AllowOpenDragonAltar.Data());
						cap.hasUsedAltar = true;
					}

					if(cap.altarCooldown > 0){
						cap.altarCooldown--;
					}
				});
			}
		}
	}

	static int cycle = 0;

	/**
	 * Check every 2 seconds
	 */
	//TODO: Could probably do this in a cleaner way with a mixin somewhere
	@SubscribeEvent
	public static void removeElytraFromDragon(PlayerTickEvent.Post playerTickEvent){
		if(!ServerConfig.dragonsAllowedToUseElytra){
			Player player = playerTickEvent.getEntity();
			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				if(dragonStateHandler.isDragon() && player instanceof ServerPlayer && cycle >= 40){
					//chestplate slot is #38
					ItemStack stack = player.getInventory().getItem(38);
					Item item = stack.getItem();
					if(item instanceof ElytraItem){
						player.drop(player.getInventory().removeItemNoUpdate(38), true, false);
					}
					cycle = 0;
				}else{
					cycle++;
				}
			});
		}
	}

	/**
	 * Adds dragon avoidance goal
	 */
	@SubscribeEvent
	public static void onJoin(EntityJoinLevelEvent joinWorldEvent){
		Entity entity = joinWorldEvent.getEntity();
		if(entity instanceof Animal && !(entity instanceof Wolf || entity instanceof Hoglin)){
			((Animal)entity).goalSelector.addGoal(5, new AvoidEntityGoal((Animal)entity, Player.class, living -> DragonStateProvider.isDragon((Player)living) && !((Player)living).hasEffect(DSEffects.ANIMAL_PEACE), 20.0F, 1.3F, 1.5F, s -> true));
		}
	}

	@SubscribeEvent
	public static void addFireProtectionToCaveDragonDrops(BlockDropsEvent dropsEvent) {
		if (dropsEvent.getBreaker() == null) return;
		if (DragonUtils.isDragonType(dropsEvent.getBreaker(), DragonTypes.CAVE)) {
			dropsEvent.getDrops().replaceAll(itemEntity -> new ItemEntity(itemEntity.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemEntity.getItem()) {
				@Override
				public boolean fireImmune() {
					return true;
				}
			});
		}
	}

	@SubscribeEvent
	public static void createAltar(PlayerInteractEvent.RightClickBlock rightClickBlock){
		if(!ServerConfig.altarCraftable){
			return;
		}

		ItemStack itemStack = rightClickBlock.getItemStack();
		if(itemStack.getItem() == DSItems.ELDER_DRAGON_BONE){
			if(!rightClickBlock.getEntity().isSpectator()){

				final Level world = rightClickBlock.getLevel();
				final BlockPos blockPos = rightClickBlock.getPos();
				BlockState blockState = world.getBlockState(blockPos);
				final Block block = blockState.getBlock();

				boolean replace = false;
				rightClickBlock.getEntity().isSpectator();
				rightClickBlock.getEntity().isCreative();
				BlockPlaceContext direction = new BlockPlaceContext(rightClickBlock.getLevel(), rightClickBlock.getEntity(), rightClickBlock.getHand(), rightClickBlock.getItemStack(), new BlockHitResult(new Vec3(0, 0, 0), rightClickBlock.getEntity().getDirection(), blockPos, false));
				if(block == Blocks.STONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_STONE.get().getStateForPlacement(direction));
					replace = true;
				}else if(block == Blocks.MOSSY_COBBLESTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_MOSSY_COBBLESTONE.get().getStateForPlacement(direction));
					replace = true;
				}else if(block == Blocks.SANDSTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_SANDSTONE.get().getStateForPlacement(direction));
					replace = true;
				}else if(block == Blocks.RED_SANDSTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_RED_SANDSTONE.get().getStateForPlacement(direction));
					replace = true;
				}else if(ResourceHelper.getKey(block).getPath().contains(ResourceHelper.getKey(Blocks.OAK_LOG).getPath())){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_OAK_LOG.get().getStateForPlacement(direction));
					replace = true;
				}else if(ResourceHelper.getKey(block).getPath().contains(ResourceHelper.getKey(Blocks.BIRCH_LOG).getPath())){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_BIRCH_LOG.get().getStateForPlacement(direction));
					replace = true;
				}else if(block == Blocks.PURPUR_BLOCK){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_PURPUR_BLOCK.get().getStateForPlacement(direction));
					replace = true;
				}else if(block == Blocks.NETHER_BRICKS){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_NETHER_BRICKS.get().getStateForPlacement(direction));
					replace = true;
				}else if(block == Blocks.BLACKSTONE){
					rightClickBlock.getEntity().getDirection();
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_BLACKSTONE.get().getStateForPlacement(direction));
					replace = true;
				}

				if(replace){
					if(!rightClickBlock.getEntity().isCreative()){
						itemStack.shrink(1);
					}
					rightClickBlock.setCanceled(true);
					world.playSound(rightClickBlock.getEntity(), blockPos, SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1, 1);
					rightClickBlock.setCancellationResult(InteractionResult.SUCCESS);
				}
			}
		}
	}

	@SubscribeEvent
	public static void returnBeacon(PlayerEvent.ItemCraftedEvent craftedEvent){
		Container inventory = craftedEvent.getInventory();
		ItemStack result = craftedEvent.getCrafting();
		int rem = ContainerHelper.clearOrCountMatchingItems(inventory, item -> item.getItem() == DSItems.PASSIVE_FIRE_BEACON
			|| item.getItem() == DSItems.PASSIVE_MAGIC_BEACON
			|| item.getItem() == DSItems.PASSIVE_PEACE_BEACON, 1, true);
		if(rem == 0 && result.getItem() == DSBlocks.DRAGON_BEACON.get().asItem()){
			craftedEvent.getEntity().addItem(new ItemStack(Items.BEACON));
		}
	}

	@SubscribeEvent
	public static void returnNetherStarHeart(PlayerEvent.ItemCraftedEvent craftedEvent){
		Container inventory = craftedEvent.getInventory();
		ItemStack result = craftedEvent.getCrafting();
		int rem = ContainerHelper.clearOrCountMatchingItems(inventory, item -> item.getItem() == DSItems.STAR_HEART, 1, true);
		if(rem == 0 && result.getItem() == DSItems.STAR_HEART){
			craftedEvent.getEntity().addItem(new ItemStack(Items.NETHER_STAR));
		}
	}

	@SubscribeEvent
	public static void returnNetherStarBone(PlayerEvent.ItemCraftedEvent craftedEvent){
		Container inventory = craftedEvent.getInventory();
		ItemStack result = craftedEvent.getCrafting();
		int rem = ContainerHelper.clearOrCountMatchingItems(inventory, item -> item.getItem() == DSItems.STAR_BONE, 1, true);
		if(rem == 0 && result.getItem() == DSItems.STAR_BONE){
			craftedEvent.getEntity().addItem(new ItemStack(Items.NETHER_STAR));
		}
	}

	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent jumpEvent){
		final LivingEntity living = jumpEvent.getEntity();


		if(living.getEffect(DSEffects.TRAPPED) != null){
			Vec3 deltaMovement = living.getDeltaMovement();
			living.setDeltaMovement(deltaMovement.x, deltaMovement.y < 0 ? deltaMovement.y : 0, deltaMovement.z);
			living.setJumping(false);
			return;
		}

		DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				if(living instanceof ServerPlayer){
					PacketDistributor.sendToAllPlayers(new SyncPlayerJump.Data(living.getId(), 10));
				}
			}
		});
	}

	@SubscribeEvent
	public static void setDragonFoodUseDuration(final LivingEntityUseItemEvent.Start event) {
		DragonStateProvider.getCap(event.getEntity()).ifPresent(handler -> {
			if (handler.isDragon()) {
				if (DragonFoodHandler.isEdible(event.getItem(), handler.getType())) {
					FoodProperties foodProperties = DragonFoodHandler.getDragonFoodProperties(event.getItem().getItem(), handler.getType());
					if (foodProperties != null) {
						event.setDuration(foodProperties.eatDurationTicks());
					}
				}
			}
		});
	}
}