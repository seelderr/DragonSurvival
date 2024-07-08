package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.CliffhangerAbility;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncPlayerJump;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import java.util.Objects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class DragonBonusHandler {
	@SubscribeEvent
	public static void dragonDamageImmunities(final LivingIncomingDamageEvent event) {
		LivingEntity living = event.getEntity();
		DamageSource damageSource = event.getSource();

		DragonStateProvider.getCap(living).ifPresent(handler -> {
			if (handler.isDragon()) {
				if (ServerConfig.bonuses) {
					if (ServerConfig.caveFireImmunity && DragonUtils.isDragonType(handler, DragonTypes.CAVE) && damageSource.is(DamageTypeTags.IS_FIRE)) {
						event.setCanceled(true);
					} else if (ServerConfig.forestBushImmunity && DragonUtils.isDragonType(handler, DragonTypes.FOREST) && damageSource == living.damageSources().sweetBerryBush()) {
						event.setCanceled(true);
					} else if (ServerConfig.forestCactiImmunity && DragonUtils.isDragonType(handler, DragonTypes.FOREST) && damageSource == living.damageSources().cactus()) {
						event.setCanceled(true);
					}
				}

				if (ServerConfig.caveSplashDamage != 0) {
					if (DragonUtils.isDragonType(handler, DragonTypes.CAVE) && !living.hasEffect(DSEffects.FIRE)) {
						if (damageSource.getDirectEntity() instanceof Snowball) {
							living.hurt(living.damageSources().generic(), ServerConfig.caveSplashDamage.floatValue());
						}
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void removeLavaFootsteps(PlayLevelSoundEvent.AtEntity event){
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}

		if (event.getSound() != null) {
			boolean isRelevant = event.getSound().value().getLocation().getPath().contains(".step");

			if (isRelevant && ServerConfig.bonuses && ServerConfig.caveLavaSwimming) {
				if (DragonUtils.isDragonType(player, DragonTypes.CAVE) && DragonSizeHandler.getOverridePose(player) == Pose.SWIMMING) {
					event.setCanceled(true);
				}
			}
		}
	}

	// TODO: This can be completely removed and have these events utilize Attributes.SAFE_FALL_DISTANCE
	@SubscribeEvent
	public static void reduceFallDistance(LivingFallEvent livingFallEvent){
		LivingEntity living = livingFallEvent.getEntity();
		DragonStateProvider.getCap(living).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				float distance = livingFallEvent.getDistance();

				if(Objects.equals(dragonStateHandler.getType(),DragonTypes.FOREST)){

					if(ServerConfig.bonuses){
						distance -= ServerConfig.forestFallReduction.floatValue();
					}

					CliffhangerAbility ability = DragonAbilities.getSelfAbility(living, CliffhangerAbility.class);
					distance -= ability.getHeight();
				}

				livingFallEvent.setDistance(distance);
			}
		});
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
}