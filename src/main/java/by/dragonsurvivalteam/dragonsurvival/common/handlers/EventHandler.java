package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonAltar;
import by.dragonsurvivalteam.dragonsurvival.network.status.PlayerJumpSync;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers;
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
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@SuppressWarnings( "unused" )
@Mod.EventBusSubscriber
public class EventHandler{


	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		if(event.phase == Phase.START || !ServerConfig.startWithDragonChoice) return;
		if(event.side == LogicalSide.CLIENT) return;

		if(event.player instanceof ServerPlayer player){
			if(player.isDeadOrDying()) return;

			if(player.tickCount > 5 * 20){
				DragonStateProvider.getCap(player).ifPresent(cap -> {
					if(!cap.hasUsedAltar && !DragonStateProvider.isDragon(player)){
						NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenDragonAltar());
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
	//TODO add Elytra from other mods
	@SubscribeEvent
	public static void removeElytraFromDragon(TickEvent.PlayerTickEvent playerTickEvent){
		if(!ServerConfig.dragonsAllowedToUseElytra && playerTickEvent.phase == TickEvent.Phase.START){
			Player player = playerTickEvent.player;
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

	@SubscribeEvent( priority = EventPriority.HIGHEST )
	public static void expDrops(BlockEvent.BreakEvent breakEvent){
		if(DragonStateProvider.isDragon(breakEvent.getPlayer())){
			if(breakEvent.getExpToDrop() > 0){
				int bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE, breakEvent.getPlayer());
				int silklevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, breakEvent.getPlayer());
				breakEvent.setExpToDrop(breakEvent.getState().getExpDrop(breakEvent.getLevel(), RandomSource.create(), breakEvent.getPos(), bonusLevel, silklevel));
			}
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
				BlockPlaceContext deirection = new BlockPlaceContext(rightClickBlock.getLevel(), rightClickBlock.getEntity(), rightClickBlock.getHand(), rightClickBlock.getItemStack(), new BlockHitResult(new Vec3(0, 0, 0), rightClickBlock.getEntity().getDirection(), blockPos, false));
				if(block == Blocks.STONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_STONE.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.MOSSY_COBBLESTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_MOSSY_COBBLESTONE.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.SANDSTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_SANDSTONE.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.RED_SANDSTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_RED_SANDSTONE.getStateForPlacement(deirection));
					replace = true;
				}else if(ResourceHelper.getKey(block).getPath().contains(ResourceHelper.getKey(Blocks.OAK_LOG).getPath())){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_OAK_LOG.getStateForPlacement(deirection));
					replace = true;
				}else if(ResourceHelper.getKey(block).getPath().contains(ResourceHelper.getKey(Blocks.BIRCH_LOG).getPath())){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_BIRCH_LOG.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.PURPUR_BLOCK){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_PURPUR_BLOCK.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.NETHER_BRICKS){
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_NETHER_BRICKS.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.BLACKSTONE){
					rightClickBlock.getEntity().getDirection();
					world.setBlockAndUpdate(blockPos, DSBlocks.DRAGON_ALTAR_BLACKSTONE.getStateForPlacement(deirection));
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
		if(rem == 0 && result.getItem() == DSBlocks.DRAGON_BEACON.asItem()){
			craftedEvent.getEntity().addItem(new ItemStack(Items.BEACON));
		}
	}

	@SubscribeEvent
	public static void returnNetherStarHeart(PlayerEvent.ItemCraftedEvent craftedEvent){
		Container inventory = craftedEvent.getInventory();
		ItemStack result = craftedEvent.getCrafting();
		int rem = ContainerHelper.clearOrCountMatchingItems(inventory, item -> item.getItem() == DSItems.STAR_HEART, 1, true);
		if(rem == 0 && result.getItem() == DSItems.STAR_HEART.asItem()){
			craftedEvent.getEntity().addItem(new ItemStack(Items.NETHER_STAR));
		}
	}

	@SubscribeEvent
	public static void returnNetherStarBone(PlayerEvent.ItemCraftedEvent craftedEvent){
		Container inventory = craftedEvent.getInventory();
		ItemStack result = craftedEvent.getCrafting();
		int rem = ContainerHelper.clearOrCountMatchingItems(inventory, item -> item.getItem() == DSItems.STAR_BONE, 1, true);
		if(rem == 0 && result.getItem() == DSItems.STAR_BONE.asItem()){
			craftedEvent.getEntity().addItem(new ItemStack(Items.NETHER_STAR));
		}
	}

	@SubscribeEvent
	public static void onJump(LivingJumpEvent jumpEvent){
		final LivingEntity living = jumpEvent.getEntity();


		if(living.getEffect(DSEffects.TRAPPED) != null){
			Vec3 deltaMovement = living.getDeltaMovement();
			living.setDeltaMovement(deltaMovement.x, deltaMovement.y < 0 ? deltaMovement.y : 0, deltaMovement.z);
			living.setJumping(false);
			jumpEvent.setCanceled(true);
			return;
		}

		DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				Double jumpBonus = 0.0;
				if (dragonStateHandler.getBody() != null) {
					jumpBonus = DSModifiers.getJumpBonus(dragonStateHandler);
				}

				living.push(0, jumpBonus, 0);

				if(living instanceof ServerPlayer){
					if(living.getServer().isSingleplayer()){
						NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new PlayerJumpSync(living.getId(), 20)); // 42
					}else{
						NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new PlayerJumpSync(living.getId(), 10)); // 21
					}
				}
			}
		});
	}
}