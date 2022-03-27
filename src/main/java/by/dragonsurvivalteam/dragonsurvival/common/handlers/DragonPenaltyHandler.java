package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.DamageSources;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SyncCapabilityDebuff;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class DragonPenaltyHandler{
	@SubscribeEvent
	public static void hitByPotion(ProjectileImpactEvent potionEvent){
		if(!ConfigHandler.SERVER.penalties.get() || ConfigHandler.SERVER.caveSplashDamage.get() == 0.0){
			return;
		}

		if(potionEvent.getProjectile() instanceof ThrownPotion){
			ThrownPotion potion = (ThrownPotion)potionEvent.getProjectile();
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
						if(dragonStateHandler.getType() != DragonType.CAVE){
							return;
						}
						player.hurt(DamageSources.WATER_BURN, ConfigHandler.SERVER.caveSplashDamage.get().floatValue());
					}
				});
			}
		}
	}

	@SubscribeEvent
	public static void consumeHurtfulItem(LivingEntityUseItemEvent.Finish destroyItemEvent){
		if(!ConfigHandler.SERVER.penalties.get()){
			return;
		}

		if(!(destroyItemEvent.getEntityLiving() instanceof Player)){
			return;
		}

		Player player = (Player)destroyItemEvent.getEntityLiving();
		ItemStack itemStack = destroyItemEvent.getItem();

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				List<String> hurtfulItems = new ArrayList<>(dragonStateHandler.getType() == DragonType.FOREST ? ConfigHandler.SERVER.forestDragonHurtfulItems.get() : dragonStateHandler.getType() == DragonType.CAVE ? ConfigHandler.SERVER.caveDragonHurtfulItems.get() : dragonStateHandler.getType() == DragonType.SEA ? ConfigHandler.SERVER.seaDragonHurtfulItems.get() : new ArrayList<>());

				if(hurtfulItems.size() > 0){
					ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());

					if(itemId != null){
						for(String item : hurtfulItems){
							boolean match = item.startsWith("item:" + itemId + ":");

							if(!match){
								match = itemStack.getTags().anyMatch((s) -> s.location().getPath().equals(item));
							}

							if(match){
								String damage = item.substring(item.lastIndexOf(":") + 1);
								player.hurt(DamageSource.GENERIC, Float.parseFloat(damage));
								break;
							}
						}
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void onWaterConsumed(LivingEntityUseItemEvent.Finish destroyItemEvent){
		if(!ConfigHandler.SERVER.penalties.get() || ConfigHandler.SERVER.seaTicksWithoutWater.get() == 0){
			return;
		}
		ItemStack itemStack = destroyItemEvent.getItem();
		DragonStateProvider.getCap(destroyItemEvent.getEntityLiving()).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				Player player = (Player)destroyItemEvent.getEntityLiving();
				if(ConfigHandler.SERVER.seaAllowWaterBottles.get() && itemStack.getItem() instanceof PotionItem){
					if(PotionUtils.getPotion(itemStack) == Potions.WATER && dragonStateHandler.getType() == DragonType.SEA && !player.level.isClientSide){
						dragonStateHandler.getDebuffData().timeWithoutWater = Math.max(dragonStateHandler.getDebuffData().timeWithoutWater - ConfigHandler.SERVER.seaTicksWithoutWaterRestored.get(), 0);
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
					}
				}
				if(DragonConfigHandler.SEA_DRAGON_HYDRATION_USE_ALTERNATIVES.contains(itemStack.getItem()) && !player.level.isClientSide){
					dragonStateHandler.getDebuffData().timeWithoutWater = Math.max(dragonStateHandler.getDebuffData().timeWithoutWater - ConfigHandler.SERVER.seaTicksWithoutWaterRestored.get(), 0);
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncCapabilityDebuff(player.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
				}
			}
		});
	}
}