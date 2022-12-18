package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonTypeData;
import by.dragonsurvivalteam.dragonsurvival.registry.DamageSources;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class DragonPenaltyHandler{
	@SubscribeEvent
	public static void hitByPotion(ProjectileImpactEvent potionEvent){
		if(!ServerConfig.penalties || ServerConfig.caveSplashDamage == 0.0){
			return;
		}

		if(potionEvent.getProjectile() instanceof ThrownPotion potion){
			if(potion.getItem().getItem() != Items.SPLASH_POTION){
				return;
			}
			if(PotionUtils.getPotion(potion.getItem()).getEffects().size() > 0){
				return; //Remove this line if you want potions with effects to also damage rather then just water ones.
			}

			Vec3 pos = potionEvent.getRayTraceResult().getLocation();
			List<Player> entities = potion.level.getEntities(EntityType.PLAYER, new AABB(pos.x - 5, pos.y - 1, pos.z - 5, pos.x + 5, pos.y + 1, pos.z + 5), (entity) -> entity.position().distanceTo(pos) <= 4);

			for(Player player : entities){
				if(player.hasEffect(DragonEffects.FIRE)){
					continue;
				}

				DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
					if(dragonStateHandler.isDragon()){
						if(dragonStateHandler.getType() == null || !dragonStateHandler.getType().equals(DragonTypes.CAVE)){
							return;
						}
						player.hurt(DamageSources.WATER_BURN, ServerConfig.caveSplashDamage.floatValue());
					}
				});
			}
		}
	}

	@SubscribeEvent
	public static void consumeHurtfulItem(LivingEntityUseItemEvent.Finish destroyItemEvent){
		if(!ServerConfig.penalties || !(destroyItemEvent.getEntityLiving() instanceof Player player)){
			return;
		}

		ItemStack itemStack = destroyItemEvent.getItem();

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				List<String> hurtfulItems = new ArrayList<>(dragonStateHandler.getType().equals(DragonTypes.FOREST) ? ServerConfig.forestDragonHurtfulItems : dragonStateHandler.getType().equals(DragonTypes.CAVE) ? ServerConfig.caveDragonHurtfulItems : dragonStateHandler.getType() .equals(DragonTypes.SEA) ? ServerConfig.seaDragonHurtfulItems : new ArrayList<>());

				for(String item : hurtfulItems){
					if(item.replace("item:", "").replace("tag:", "").startsWith(itemStack.getItem().getRegistryName().toString() + ":")){
						String damage = item.substring(item.lastIndexOf(":") + 1);
						player.hurt(DamageSource.GENERIC, Float.parseFloat(damage));
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
		DragonStateProvider.getCap(destroyItemEvent.getEntityLiving()).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && dragonStateHandler.getType() instanceof SeaDragonType seaDragonType){
				Player player = (Player)destroyItemEvent.getEntityLiving();
				if(ServerConfig.seaAllowWaterBottles && itemStack.getItem() instanceof PotionItem){
					if(PotionUtils.getPotion(itemStack) == Potions.WATER && dragonStateHandler.getType() .equals(DragonTypes.SEA) && !player.level.isClientSide){
						seaDragonType.timeWithoutWater = Math.max(seaDragonType.timeWithoutWater - ServerConfig.seaTicksWithoutWaterRestored, 0);
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncDragonTypeData(player.getId(), seaDragonType));
					}
				}
				if(DragonConfigHandler.SEA_DRAGON_HYDRATION_USE_ALTERNATIVES.contains(itemStack.getItem()) && !player.level.isClientSide){
					seaDragonType.timeWithoutWater = Math.max(seaDragonType.timeWithoutWater - ServerConfig.seaTicksWithoutWaterRestored, 0);
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncDragonTypeData(player.getId(), seaDragonType));
				}
			}
		});
	}
}