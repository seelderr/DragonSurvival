package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonHitboxHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncFlyingStatus;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.util.DragonType.*;

/**
 * Used in pair with {@link ClientFlightHandler}
 */
@Mod.EventBusSubscriber()
@SuppressWarnings( "unused" )
public class ServerFlightHandler{

	public static final int spinDuration = (int)Math.round(0.76 * 20);

	/**
	 * Sets the fall damage based on flight speed and dragon's size
	 */
	@SubscribeEvent
	public static void changeFallDistance(LivingFallEvent event){
		LivingEntity livingEntity = event.getEntityLiving();
		double flightSpeed = event.getDistance();

		DragonStateProvider.getCap(livingEntity).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && dragonStateHandler.hasWings()){
				if(!ServerConfig.enableFlightFallDamage){
					event.setCanceled(true);
				}

				if(flightSpeed <= 2 || dragonStateHandler.isWingsSpread() && !livingEntity.isSprinting() && flightSpeed <= 4){
					event.setCanceled(true);
					return;
				}

				if(livingEntity.isPassenger() && DragonUtils.isDragon(livingEntity.getVehicle())){
					event.setCanceled(true);
					return;
				}


				MobEffectInstance effectinstance = livingEntity.getEffect(MobEffects.JUMP);
				float f = effectinstance == null ? 0.0F : (float)(effectinstance.getAmplifier() + 1);

				double damage = livingEntity.getDeltaMovement().lengthSqr() * (dragonStateHandler.getSize() / 20);
				damage = Mth.clamp(damage, 0, livingEntity.getHealth() - (ServerConfig.lethalFlight ? 0 : 1));

				if(!livingEntity.level.isClientSide && dragonStateHandler.isWingsSpread()){
					event.setDistance((float)Math.floor(((damage + 3.0F + f) / event.getDamageMultiplier())));
				}

				if(!livingEntity.level.isClientSide){
					if(ServerConfig.foldWingsOnLand){
						if(dragonStateHandler.isWingsSpread()){
							dragonStateHandler.setWingsSpread(false);
							NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), new SyncFlyingStatus(livingEntity.getId(), false));
						}
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void foldWings(PlayerTickEvent tickEvent){
		Player player = tickEvent.player;
		if(tickEvent.phase == Phase.START || !DragonUtils.isDragon(player) || player.level.isClientSide){
			return;
		}
		if(!ServerConfig.foldWingsOnLand || player.getFoodData().getFoodLevel() <= ServerConfig.flightHungerThreshold && !player.isCreative() && !ServerConfig.allowFlyingWithoutHunger){
			return;
		}

		DragonStateHandler dragonStateHandler = DragonUtils.getHandler(player);

		if(dragonStateHandler.isWingsSpread()){
			player.fallDistance = Math.max(0, player.fallDistance * 0.5f);
		}

		if(dragonStateHandler.hasFlown && player.isOnGround()){
			if(dragonStateHandler.isWingsSpread() && player.isCreative()){
				dragonStateHandler.hasFlown = false;
				dragonStateHandler.setWingsSpread(false);
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncFlyingStatus(player.getId(), false));
			}
		}else{
			if(!dragonStateHandler.hasFlown && isFlying(player)){
				dragonStateHandler.hasFlown = true;
			}
		}
	}

	public static boolean isFlying(LivingEntity player){
		DragonStateHandler dragonStateHandler = DragonUtils.getHandler(player);
		return dragonStateHandler.hasWings() && dragonStateHandler.isWingsSpread() && !player.isOnGround() && !player.isInWater() && !player.isInLava();
	}

	@SubscribeEvent
	public static void playerFlightIcon(TickEvent.PlayerTickEvent playerTickEvent){
		if(playerTickEvent.phase == Phase.START){
			return;
		}
		Player player = playerTickEvent.player;
		DragonStateProvider.getCap(player).ifPresent(handler -> {
			if(handler.isDragon()){
				if(player.tickCount % 10 == 0){
					if(handler.isWingsSpread()){
						switch(handler.getType()){
							case SEA -> player.addEffect(new MobEffectInstance(DragonEffects.sea_wings, 500));
							case CAVE -> player.addEffect(new MobEffectInstance(DragonEffects.cave_wings, 500));
							case FOREST -> player.addEffect(new MobEffectInstance(DragonEffects.forest_wings, 500));
						}
					}
				}
			}

			if(!handler.isDragon() || !handler.isWingsSpread()){
				if(player.hasEffect(DragonEffects.sea_wings)){
					player.resetFallDistance();
					player.removeEffect(DragonEffects.sea_wings);
				}
				if(player.hasEffect(DragonEffects.cave_wings)){
					player.resetFallDistance();
					player.removeEffect(DragonEffects.cave_wings);
				}
				if(player.hasEffect(DragonEffects.forest_wings)){
					player.resetFallDistance();
					player.removeEffect(DragonEffects.forest_wings);
				}
			}
		});
	}

	@SubscribeEvent
	public static void playerFlightAttacks(TickEvent.PlayerTickEvent playerTickEvent){
		if(playerTickEvent.phase == Phase.START){
			return;
		}
		Player player = playerTickEvent.player;
		DragonStateProvider.getCap(player).ifPresent(handler -> {
			if(handler.isDragon()){
				if(handler.getMovementData().spinAttack > 0){
					if(!isFlying(player) && !canSwimSpin(player)){
						if(!player.level.isClientSide){
							handler.getMovementData().spinAttack = 0;
							NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSpinStatus(player.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned));
						}
					}
				}

				if(isSpin(player)){
					int range = 5;
					List<Entity> entities = player.level.getEntities(null, new AABB(player.position().x - range, player.position().y - range, player.position().z - range, player.position().x + range, player.position().y + range, player.position().z + range));
					entities.removeIf((e) -> e.distanceTo(player) > range);
					entities.remove(player);
					entities.removeIf((e) -> e instanceof Player && !player.canHarmPlayer((Player)e));
					entities.removeIf((e) -> e instanceof DragonHitBox && player == (((DragonHitBox)e).player));
					entities.removeIf((e) -> e instanceof DragonHitboxPart && player == (((DragonHitboxPart)e).getParent().player));
					for(Entity ent : entities){
						if(player.hasPassenger(ent) || ent.getId() == DragonHitboxHandler.dragonHitboxes.getOrDefault(player.getId(), -1)){
							continue;
						}
						if(ent instanceof LivingEntity entity){
							//Don't hit the same mob multiple times
							if(entity.getLastHurtByMob() == player && entity.getLastHurtByMobTimestamp() <= entity.tickCount + 5 * 20){
								continue;
							}
						}
						player.attack(ent);
					}

					handler.getMovementData().spinAttack--;

					if(!player.level.isClientSide){
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSpinStatus(player.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned));
					}
				}else if(handler.getMovementData().spinCooldown > 0){
					if(!player.level.isClientSide){
						handler.getMovementData().spinCooldown--;
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSpinStatus(player.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned));
					}
				}
			}
		});
	}

	public static boolean isSpin(Player entity){
		DragonStateHandler handler = DragonUtils.getHandler(entity);

		if(isFlying(entity) || canSwimSpin(entity)){
			return handler.getMovementData().spinAttack > 0;
		}

		return false;
	}

	public static boolean canSwimSpin(LivingEntity player){
		DragonStateHandler dragonStateHandler = DragonUtils.getHandler(player);
		boolean validSwim = (dragonStateHandler.getType() == SEA || dragonStateHandler.getType() == FOREST) && player.isInWater() || player.isInLava() && dragonStateHandler.getType() == CAVE;
		return validSwim && dragonStateHandler.hasWings() && !player.isOnGround();
	}

	@ConfigRange(min = 1, max = 60 * 60 * 20)
	@ConfigOption(side = ConfigSide.SERVER, key = "flightHungerTicks", category = "wings", comment = "How many ticks it takes for one hunger point to be drained while flying, this is based on hover flight.")
	public static int flightHungerTicks = 50;

	@SubscribeEvent
	public static void playerFoodExhaustion(TickEvent.PlayerTickEvent playerTickEvent){
		if(playerTickEvent.phase == Phase.START){
			return;
		}
		Player player = playerTickEvent.player;

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				boolean wingsSpread = dragonStateHandler.isWingsSpread();
				if(ServerConfig.creativeFlight && !player.level.isClientSide){
					if(player.getAbilities().flying != wingsSpread && (!player.isCreative() && !player.isSpectator())){
						player.getAbilities().flying = wingsSpread;
						player.onUpdateAbilities();
					}
				}

				if(wingsSpread){
					if(ServerConfig.flyingUsesHunger){
						if(isFlying(player)){
							if(!player.level.isClientSide){
								if(player.getFoodData().getFoodLevel() <= ServerConfig.foldWingsThreshold && !ServerConfig.allowFlyingWithoutHunger && !player.isCreative()){
									player.sendMessage(new TranslatableComponent("ds.wings.nohunger"), player.getUUID());
									dragonStateHandler.setWingsSpread(false);
									NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncFlyingStatus(player.getId(), false));
									return;
								}
							}
							Vec3 delta = player.getDeltaMovement();
							float moveSpeed = (float)delta.horizontalDistance();
							float l = 4f / flightHungerTicks;
							float moveSpeedReq = 1.0F;
							float minFoodReq = l / 10f;
							float drain = Math.max(minFoodReq, (float)(Math.min(1.0, Math.max(0, Math.max(moveSpeedReq - moveSpeed, 0) / moveSpeedReq)) * l));

							player.causeFoodExhaustion(drain);
						}
					}
				}
			}
		});
	}

	public static boolean isGliding(Player player){
		DragonStateHandler dragonStateHandler = DragonUtils.getHandler(player);
		boolean hasFood = player.getFoodData().getFoodLevel() > ServerConfig.flightHungerThreshold || player.isCreative() || ServerConfig.allowFlyingWithoutHunger;
		return hasFood && player.isSprinting() && isFlying(player);
	}

	public static double getLandTime(Player player, double goalTime){
		if(isFlying(player)){
			Vec3 motion = player.getDeltaMovement();
			BlockPos blockHeight = player.level.getHeightmapPos(Types.MOTION_BLOCKING, player.blockPosition());
			int height = blockHeight.getY();
			double aboveGround = Math.max(0, player.position().y - height);
			double timeToGround = (aboveGround / Math.abs(motion.y));
			if(player.fallDistance > 5 && motion.y < 0){
				if(aboveGround < 20 && timeToGround <= goalTime){
					return timeToGround;
				}
			}
		}

		return -1;
	}
}