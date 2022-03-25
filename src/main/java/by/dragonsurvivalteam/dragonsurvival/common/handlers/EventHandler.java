package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.entity.monsters.MagicalPredator;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.status.PlayerJumpSync;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.item.Item;
import net.minecraft.entity.monster.Hoglin;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.passive.Wolf;
import net.minecraft.entity.passive.horse.Horse;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.AABB;
import net.minecraft.util.math.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Level;
import net.minecraft.world.server.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel.ADULT;

@SuppressWarnings( "unused" )
@Mod.EventBusSubscriber
public class EventHandler{

	static int cycle = 0;

	/**
	 * Check every 2 seconds
	 */
	@SubscribeEvent
	public static void removeElytraFromDragon(TickEvent.PlayerTickEvent playerTickEvent){
		if(!ConfigHandler.COMMON.dragonsAllowedToUseElytra.get() && playerTickEvent.phase == TickEvent.Phase.START){
			Player player = playerTickEvent.player;
			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				if(dragonStateHandler.isDragon() && player instanceof ServerPlayer && cycle >= 40){
					//chestplate slot is #38
					ItemStack stack = player.inventory.getItem(38);
					Item item = stack.getItem();
					if(item instanceof ElytraItem){
						player.drop(player.inventory.removeItemNoUpdate(38), true, false);
					}
					cycle = 0;
				}else{
					cycle++;
				}
			});
		}
	}

	@SubscribeEvent
	public static void mobDeath(LivingEntityDropsEvent event){
		LivingEntity entity = event.getEntityLivingEntity();
		float health = entity.getMaxHealth();

		//if(entity instanceof AnimalEntity) return;
		if(event.getSource() == null || !(event.getSource().get() instanceof Player)){
			return;
		}
		if(!DragonUtils.isDragon(event.getSource().get())){
			return;
		}

		if(health >= 14 && health < 20){
			if(entity.level.random.nextInt(100) <= (ConfigHandler.SERVER.dragonHeartShardChance.get() * 100) + (event.getLootingLevel() * ((ConfigHandler.SERVER.dragonHeartShardChance.get() * 100) / 4))){
				event.getDrops().add(new Item(entity.level, entity.position().x, entity.position().y, entity.position().z, new ItemStack(DSItems.dragonHeartShard)));
			}
		}else if(health >= 20 && health < 50){
			if(entity.level.random.nextInt(100) <= (ConfigHandler.SERVER.weakDragonHeartChance.get() * 100) + (event.getLootingLevel() * ((ConfigHandler.SERVER.weakDragonHeartChance.get() * 100) / 4))){
				event.getDrops().add(new Item(entity.level, entity.position().x, entity.position().y, entity.position().z, new ItemStack(DSItems.weakDragonHeart)));
			}
		}else if(health >= 50){
			if(entity.level.random.nextInt(100) <= (ConfigHandler.SERVER.elderDragonHeartChance.get() * 100) + (event.getLootingLevel() * ((ConfigHandler.SERVER.elderDragonHeartChance.get() * 100) / 4))){
				event.getDrops().add(new Item(entity.level, entity.position().x, entity.position().y, entity.position().z, new ItemStack(DSItems.elderDragonHeart)));
			}
		}
	}

	/**
	 * Adds dragon avoidance goal
	 */
	@SubscribeEvent
	public static void onJoin(EntityJoinWorldEvent joinWorldEvent){
		Entity entity = joinWorldEvent.get();
		if(entity instanceof Animal && !(entity instanceof Wolf || entity instanceof Hoglin)){

			((Animal)entity).goalSelector.addGoal(5, new AvoidEntityGoal((Animal)entity, Player.class, living -> DragonUtils.isDragon((Player)living) && !((Player)living).hasEffect(DragonEffects.ANIMAL_PEACE), 20.0F, 1.3F, 1.5F, EntityPredicates.ATTACK_ALLOWED));
		}
		if(entity instanceof Horse){
			Horse horse = (Horse)entity;
			horse.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(horse, Player.class, 0, true, false, living -> living.getCapability(Capabilities.DRAGON_CAPABILITY).orElseGet(null).getLevel() != ADULT));
			horse.targetSelector.addGoal(4, new AvoidEntityGoal<>(horse, Player.class, living -> living.getCapability(Capabilities.DRAGON_CAPABILITY).orElse(null).getLevel() == ADULT && !living.hasEffect(DragonEffects.ANIMAL_PEACE), 20, 1.3, 1.5, EntityPredicates.ATTACK_ALLOWED::test));
		}
	}

	@SubscribeEvent
	public static void onDeath(LivingEntityDeathEvent e){
		LivingEntity living = e.getEntityLivingEntity();
		Level world = living.level;
		if(living instanceof Animal && living.level.getRandom().nextDouble() < ConfigHandler.COMMON.predatorAnimalSpawnChance.get()){
			if(world.getEntitiesOfClass(Player.class, new AABB(living.blockPosition()).inflate(50), player -> player.hasEffect(DragonEffects.PREDATOR_ANTI_SPAWN)).isEmpty()){
				MagicalPredator beast = DSEntities.MAGICAL_BEAST.create(living.level);
				living.level.addFreshEntity(beast);
				beast.teleportToWithTicket(living.getX(), living.getY(), living.getZ());
			}
		}
	}

	@SubscribeEvent
	public static void sleepCheck(SleepingLocationCheckEvent sleepingLocationCheckEvent){
		BlockPos sleepingLocation = sleepingLocationCheckEvent.getSleepingLocation();
		Level world = sleepingLocationCheckEvent.get().level;
		if(world.isNight() && world.getBlockEntity(sleepingLocation) instanceof SourceOfMagicBlockEntity){
			sleepingLocationCheckEvent.setResult(Event.Result.ALLOW);
		}
	}

	@SubscribeEvent( priority = EventPriority.HIGHEST )
	public static void expDrops(BlockEvent.BreakEvent breakEvent){
		if(DragonUtils.isDragon(breakEvent.getPlayer())){
			if(breakEvent.getExpToDrop() > 0){
				int bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE, breakEvent.getPlayer());
				int silklevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, breakEvent.getPlayer());
				breakEvent.setExpToDrop(breakEvent.getState().getExpDrop(breakEvent.getWorld(), breakEvent.getPos(), bonusLevel, silklevel));
			}
		}
	}

	@SubscribeEvent
	public static void blockBroken(BlockEvent.BreakEvent breakEvent){
		if(breakEvent.isCanceled()){
			return;
		}

		Player player = breakEvent.getPlayer();
		if(player.isCreative()){
			return;
		}

		int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player);

		if(i <= 0){
			LevelAccessor world = breakEvent.getWorld();
			if(world instanceof ServerLevel){
				BlockState blockState = breakEvent.getState();
				BlockPos blockPos = breakEvent.getPos();
				Block block = blockState.getBlock();
				ItemStack mainHandItem = ClawToolHandler.getDragonTools(player);
				double random;
				// Modded Ore Support
				String[] tagStringSplit = ConfigHandler.SERVER.oresTag.get().split(":");
				ResourceLocation ores = new ResourceLocation(tagStringSplit[0], tagStringSplit[1]);
				// Checks to make sure the ore does not drop itself or another ore from the tag (no going infinite with ores)
				ITag<Item> oresTag = ItemTags.getAllTags().getTag(ores);

				if(!oresTag.contains(block.asItem())){
					return;
				}

				List<ItemStack> drops = block.getDrops(blockState, new LootContext.Builder((ServerLevel)world).withParameter(LootParameters.ORIGIN, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())).withParameter(LootParameters.TOOL, mainHandItem));
				DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
					final boolean suitableOre = (mainHandItem.isCorrectToolForDrops(blockState) || (dragonStateHandler.isDragon() && dragonStateHandler.canHarvestWithPaw(player, blockState))) && drops.stream().noneMatch(item -> oresTag.contains(item.getItem()));
					if(suitableOre && !player.isCreative()){
						boolean isCave = dragonStateHandler.getType() == DragonType.CAVE;

						if(dragonStateHandler.isDragon()){
							if(player.getRandom().nextDouble() < ConfigHandler.SERVER.dragonOreDustChance.get()){
								world.addFreshEntity(new Item((Level)world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, new ItemStack(DSItems.elderDragonDust)){
									@Override
									public boolean fireImmune(){
										return isCave || super.fireImmune();
									}
								});
							}
							if(player.getRandom().nextDouble() < ConfigHandler.SERVER.dragonOreBoneChance.get()){
								world.addFreshEntity(new Item((Level)world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, new ItemStack(DSItems.elderDragonBone)){
									@Override
									public boolean fireImmune(){
										return isCave || super.fireImmune();
									}
								});
							}
						}else{
							if(player.getRandom().nextDouble() < ConfigHandler.SERVER.humanOreDustChance.get()){
								world.addFreshEntity(new Item((Level)world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, new ItemStack(DSItems.elderDragonDust)){
									@Override
									public boolean fireImmune(){
										return isCave || super.fireImmune();
									}
								});
							}
							if(player.getRandom().nextDouble() < ConfigHandler.SERVER.humanOreBoneChance.get()){
								world.addFreshEntity(new Item((Level)world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, new ItemStack(DSItems.elderDragonBone)){
									@Override
									public boolean fireImmune(){
										return isCave || super.fireImmune();
									}
								});
							}
						}
					}
				});
			}
		}
	}


	@SubscribeEvent
	public static void createAltar(PlayerInteractEvent.RightClickBlock rightClickBlock){
		if(!ConfigHandler.SERVER.altarCraftable.get()){
			return;
		}

		ItemStack itemStack = rightClickBlock.getItemStack();
		if(itemStack.getItem() == DSItems.elderDragonBone){
			if(!rightClickBlock.getPlayer().isSpectator()){

				final Level world = rightClickBlock.getWorld();
				final BlockPos blockPos = rightClickBlock.getPos();
				BlockState blockState = world.getBlockState(blockPos);
				final Block block = blockState.getBlock();

				boolean replace = false;
				rightClickBlock.getPlayer().isSpectator();
				rightClickBlock.getPlayer().isCreative();
				BlockPlaceContext deirection = new BlockPlaceContext(rightClickBlock.getPlayer(), rightClickBlock.getHand(), rightClickBlock.getItemStack(), new BlockHitResult(new Vec3(0, 0, 0), rightClickBlock.getPlayer().getDirection(), blockPos, false));
				if(block == Blocks.STONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_stone.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.MOSSY_COBBLESTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_mossy_cobblestone.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.SANDSTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_sandstone.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.RED_SANDSTONE){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_red_sandstone.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.OAK_LOG){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_oak_log.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.BIRCH_LOG){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_birch_log.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.PURPUR_BLOCK){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_purpur_block.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.NETHER_BRICKS){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_nether_bricks.getStateForPlacement(deirection));
					replace = true;
				}else if(block == Blocks.BLACKSTONE){
					rightClickBlock.getPlayer().getDirection();
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_blackstone.getStateForPlacement(deirection));
					replace = true;
				}

				if(replace){
					if(!rightClickBlock.getPlayer().isCreative()){
						itemStack.shrink(1);
					}
					rightClickBlock.setCanceled(true);
					world.playSound(rightClickBlock.getPlayer(), blockPos, SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1, 1);
					rightClickBlock.setCancellationResult(InteractionResult.SUCCESS);
				}
			}
		}
	}

	@SubscribeEvent
	public static void returnBeacon(PlayerEvent.ItemCraftedEvent craftedEvent){
		IInventory inventory = craftedEvent.getInventory();
		ItemStack result = craftedEvent.getCrafting();
		if(result.getItem() == DSBlocks.dragonBeacon.asItem()){
			craftedEvent.getPlayer().addItem(new ItemStack(Items.BEACON));
		}
	}

	@SubscribeEvent
	public static void onJump(LivingEntityEvent.LivingEntityJumpEvent jumpEvent){
		final LivingEntity living = jumpEvent.getEntityLivingEntity();
		DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				switch(dragonStateHandler.getLevel()){
					case BABY:
						living.push(0, ConfigHandler.SERVER.newbornJump.get(), 0); //1+ block
						break;
					case YOUNG:
						living.push(0, ConfigHandler.SERVER.youngJump.get(), 0); //1.5+ block
						break;
					case ADULT:
						living.push(0, ConfigHandler.SERVER.adultJump.get(), 0); //2+ blocks
						break;
				}
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