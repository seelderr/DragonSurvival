package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.common.capability.Capabilities;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.stream.StreamSupport;

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

	@SubscribeEvent
	public static void mobDeath(LivingDropsEvent event){
		LivingEntity entity = event.getEntityLiving();
		float health = entity.getMaxHealth();

		//if(entity instanceof AnimalEntity) return;
		if(event.getSource() == null || !(event.getSource().getEntity() instanceof Player)){
			return;
		}
		if(!DragonUtils.isDragon(event.getSource().getEntity())){
			return;
		}

		if(health >= 14 && health < 20){
			if(entity.level.random.nextInt(100) <= (ConfigHandler.SERVER.dragonHeartShardChance.get() * 100) + (event.getLootingLevel() * ((ConfigHandler.SERVER.dragonHeartShardChance.get() * 100) / 4))){
				event.getDrops().add(new ItemEntity(entity.level, entity.position().x, entity.position().y, entity.position().z, new ItemStack(DSItems.dragonHeartShard)));
			}
		}else if(health >= 20 && health < 50){
			if(entity.level.random.nextInt(100) <= (ConfigHandler.SERVER.weakDragonHeartChance.get() * 100) + (event.getLootingLevel() * ((ConfigHandler.SERVER.weakDragonHeartChance.get() * 100) / 4))){
				event.getDrops().add(new ItemEntity(entity.level, entity.position().x, entity.position().y, entity.position().z, new ItemStack(DSItems.weakDragonHeart)));
			}
		}else if(health >= 50){
			if(entity.level.random.nextInt(100) <= (ConfigHandler.SERVER.elderDragonHeartChance.get() * 100) + (event.getLootingLevel() * ((ConfigHandler.SERVER.elderDragonHeartChance.get() * 100) / 4))){
				event.getDrops().add(new ItemEntity(entity.level, entity.position().x, entity.position().y, entity.position().z, new ItemStack(DSItems.elderDragonHeart)));
			}
		}
	}

	/**
	 * Adds dragon avoidance goal
	 */
	@SubscribeEvent
	public static void onJoin(EntityJoinWorldEvent joinWorldEvent){
		Entity entity = joinWorldEvent.getEntity();
		if(entity instanceof Animal && !(entity instanceof Wolf || entity instanceof Hoglin)){
			((Animal)entity).goalSelector.addGoal(5, new AvoidEntityGoal((Animal)entity, Player.class, living -> DragonUtils.isDragon((Player)living) && !((Player)living).hasEffect(DragonEffects.ANIMAL_PEACE), 20.0F, 1.3F, 1.5F, (s) -> true));
		}
		if(entity instanceof Horse){
			Horse horse = (Horse)entity;
			horse.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(horse, Player.class, 0, true, false, living -> living.getCapability(Capabilities.DRAGON_CAPABILITY).orElseGet(null).getLevel() != ADULT));
			horse.targetSelector.addGoal(4, new AvoidEntityGoal<>(horse, Player.class, living -> living.getCapability(Capabilities.DRAGON_CAPABILITY).orElse(null).getLevel() == ADULT && !living.hasEffect(DragonEffects.ANIMAL_PEACE), 20, 1.3, 1.5, (s) -> true));
		}
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent e){
		LivingEntity living = e.getEntityLiving();
		Level world = living.level;
		if(living instanceof Animal && living.level.getRandom().nextDouble() < ConfigHandler.COMMON.predatorAnimalSpawnChance.get()){
			if(world.getEntitiesOfClass(Player.class, new AABB(living.blockPosition()).inflate(50), player -> player.hasEffect(DragonEffects.PREDATOR_ANTI_SPAWN)).isEmpty()){
				MagicalPredator beast = DSEntities.MAGICAL_BEAST.create(living.level);
				living.level.addFreshEntity(beast);
				beast.teleportToWithTicket(living.getX(), living.getY(), living.getZ());
			}
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
				TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, ores);
				boolean hasItself = StreamSupport.stream(Registry.ITEM.getTagOrEmpty(tagKey).spliterator(), false).anyMatch((s) -> s.value() == block.asItem());

				if(hasItself){
					return;
				}

				List<ItemStack> drops = block.getDrops(blockState, new LootContext.Builder((ServerLevel)world).withParameter(LootContextParams.ORIGIN, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())).withParameter(LootContextParams.TOOL, mainHandItem));
				DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
					final boolean suitableOre = (mainHandItem.isCorrectToolForDrops(blockState) || (dragonStateHandler.isDragon() && dragonStateHandler.canHarvestWithPaw(player, blockState)));
					if(suitableOre && !player.isCreative()){
						boolean isCave = dragonStateHandler.getType() == DragonType.CAVE;

						if(dragonStateHandler.isDragon()){
							if(player.getRandom().nextDouble() < ConfigHandler.SERVER.dragonOreDustChance.get()){
								world.addFreshEntity(new ItemEntity((Level)world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, new ItemStack(DSItems.elderDragonDust)){
									@Override
									public boolean fireImmune(){
										return isCave || super.fireImmune();
									}
								});
							}
							if(player.getRandom().nextDouble() < ConfigHandler.SERVER.dragonOreBoneChance.get()){
								world.addFreshEntity(new ItemEntity((Level)world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, new ItemStack(DSItems.elderDragonBone)){
									@Override
									public boolean fireImmune(){
										return isCave || super.fireImmune();
									}
								});
							}
						}else{
							if(player.getRandom().nextDouble() < ConfigHandler.SERVER.humanOreDustChance.get()){
								world.addFreshEntity(new ItemEntity((Level)world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, new ItemStack(DSItems.elderDragonDust)){
									@Override
									public boolean fireImmune(){
										return isCave || super.fireImmune();
									}
								});
							}
							if(player.getRandom().nextDouble() < ConfigHandler.SERVER.humanOreBoneChance.get()){
								world.addFreshEntity(new ItemEntity((Level)world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, new ItemStack(DSItems.elderDragonBone)){
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
				}else if(block.getRegistryName().getPath().contains(Blocks.OAK_LOG.getRegistryName().getPath())){
					world.setBlockAndUpdate(blockPos, DSBlocks.dragon_altar_oak_log.getStateForPlacement(deirection));
					replace = true;
				}else if(block.getRegistryName().getPath().contains(Blocks.BIRCH_LOG.getRegistryName().getPath())){
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
		Container inventory = craftedEvent.getInventory();
		ItemStack result = craftedEvent.getCrafting();
		if(result.getItem() == DSBlocks.dragonBeacon.asItem()){
			craftedEvent.getPlayer().addItem(new ItemStack(Items.BEACON));
		}
	}

	@SubscribeEvent
	public static void onJump(LivingJumpEvent jumpEvent){
		final LivingEntity living = jumpEvent.getEntityLiving();
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