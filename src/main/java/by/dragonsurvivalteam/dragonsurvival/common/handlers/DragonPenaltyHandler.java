package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.PotionUtils;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class DragonPenaltyHandler{
	@SubscribeEvent
	public static void hitByWaterPotion(ProjectileImpactEvent potionEvent){
		if(!ServerConfig.penalties || ServerConfig.caveSplashDamage == 0.0){
			return;
		}

		if(potionEvent.getProjectile() instanceof ThrownPotion potion){
			if(potion.getItem().getItem() != Items.SPLASH_POTION){
				return;
			}

			if(!PotionUtils.getPotion(potion.getItem()).getEffects().isEmpty()){
				return;
			}

			Vec3 pos = potionEvent.getRayTraceResult().getLocation();
			List<Player> entities = potion.level().getEntities(EntityType.PLAYER, new AABB(pos.x - 5, pos.y - 1, pos.z - 5, pos.x + 5, pos.y + 1, pos.z + 5), entity -> entity.position().distanceTo(pos) <= 4);

			for(Player player : entities){
				if(player.hasEffect(DSEffects.FIRE)){
					continue;
				}

				DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
					if(dragonStateHandler.isDragon()){
						if(dragonStateHandler.getType() == null || !Objects.equals(dragonStateHandler.getType(), DragonTypes.CAVE)){
							return;
						}
						player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.WATER_BURN)), ServerConfig.caveSplashDamage.floatValue());
					}
				});
			}
		}
	}

	@SubscribeEvent
	public static void consumeHurtfulItem(LivingEntityUseItemEvent.Finish destroyItemEvent){
		if(!ServerConfig.penalties || !(destroyItemEvent.getEntity() instanceof Player player)){
			return;
		}

		ItemStack itemStack = destroyItemEvent.getItem();

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				List<String> hurtfulItems = new ArrayList<>(
						Objects.equals(dragonStateHandler.getType(), DragonTypes.FOREST) ? ServerConfig.forestDragonHurtfulItems : Objects.equals(dragonStateHandler.getType(), DragonTypes.CAVE) ? ServerConfig.caveDragonHurtfulItems : Objects.equals(dragonStateHandler.getType(), DragonTypes.SEA) ? ServerConfig.seaDragonHurtfulItems : new ArrayList<>());

				for(String item : hurtfulItems){
					if(item.replace("item:", "").replace("tag:", "").startsWith(ResourceHelper.getKey(itemStack.getItem()).toString() + ":")){
						String damage = item.substring(item.lastIndexOf(":") + 1);
						player.hurt(player.damageSources().generic(), Float.parseFloat(damage));
						break;
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void onWaterConsumed(LivingEntityUseItemEvent.Finish destroyItemEvent){
		if(!ServerConfig.penalties || ServerConfig.seaTicksWithoutWater == 0){
			return;
		}
		ItemStack itemStack = destroyItemEvent.getItem();
		DragonStateProvider.getCap(destroyItemEvent.getEntity()).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && dragonStateHandler.getType() instanceof SeaDragonType seaDragonType){
				Player player = (Player)destroyItemEvent.getEntity();
				if(ServerConfig.seaAllowWaterBottles && itemStack.getItem() instanceof PotionItem){
					if(PotionUtils.getPotion(itemStack) == Potions.WATER && Objects.equals(dragonStateHandler.getType(), DragonTypes.SEA) && !player.level().isClientSide()){
						seaDragonType.timeWithoutWater = Math.max(seaDragonType.timeWithoutWater - ServerConfig.seaTicksWithoutWaterRestored, 0);
						PacketDistributor.sendToPlayersTrackingEntity(player, new SyncDragonType.Data(player.getId(), seaDragonType.writeNBT()));
					}
				}
				if(DragonConfigHandler.SEA_DRAGON_HYDRATION_USE_ALTERNATIVES.contains(itemStack.getItem()) && !player.level().isClientSide()){
					seaDragonType.timeWithoutWater = Math.max(seaDragonType.timeWithoutWater - ServerConfig.seaTicksWithoutWaterRestored, 0);
					PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonType.Data(player.getId(), seaDragonType.writeNBT()));
				}
			}
		});
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
}