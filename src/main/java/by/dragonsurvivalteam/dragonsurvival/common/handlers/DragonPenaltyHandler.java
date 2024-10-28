package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSItemTags;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.PotionUtils;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler.DRAGON_BLACKLISTED_ITEMS;

@EventBusSubscriber
public class DragonPenaltyHandler {
    @SubscribeEvent
    public static void hitByWaterPotion(ProjectileImpactEvent potionEvent) {
        if (!ServerConfig.penaltiesEnabled || ServerConfig.caveSplashDamage == 0.0) {
            return;
        }

        if (potionEvent.getProjectile() instanceof ThrownPotion potion) {
            if (potion.getItem().getItem() != Items.SPLASH_POTION) {
                return;
            }

            Optional<Potion> potionData = PotionUtils.getPotion(potion.getItem());
            // If we have no data here, just default to doing nothing (some mods do strange things with potion items that have no Potion data)
            if (potionData.isEmpty() || !potionData.get().getEffects().isEmpty()) {
                return;
            }

            Vec3 pos = potionEvent.getRayTraceResult().getLocation();
            List<Player> entities = potion.level().getEntities(EntityType.PLAYER, new AABB(pos.x - 5, pos.y - 1, pos.z - 5, pos.x + 5, pos.y + 1, pos.z + 5), entity -> entity.position().distanceTo(pos) <= 4);

            for (Player player : entities) {
                if (player.hasEffect(DSEffects.FIRE)) {
                    continue;
                }

                DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
                    if(dragonStateHandler.isDragon()){
                        if(dragonStateHandler.getType() == null || !DragonUtils.isDragonType(dragonStateHandler, DragonTypes.CAVE)){
                            return;
                        }
                        player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.WATER_BURN)), ServerConfig.caveSplashDamage.floatValue());
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void consumeHurtfulItem(LivingEntityUseItemEvent.Finish destroyItemEvent) {
        if (!ServerConfig.penaltiesEnabled || !(destroyItemEvent.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack itemStack = destroyItemEvent.getItem();

        DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
            if(dragonStateHandler.isDragon()){
                List<String> hurtfulItems = new ArrayList<>(
                        DragonUtils.isDragonType(dragonStateHandler, DragonTypes.FOREST) ? ServerConfig.forestDragonHurtfulItems : DragonUtils.isDragonType(dragonStateHandler, DragonTypes.CAVE) ? ServerConfig.caveDragonHurtfulItems : DragonUtils.isDragonType(dragonStateHandler, DragonTypes.SEA) ? ServerConfig.seaDragonHurtfulItems : new ArrayList<>());

                for (String item : hurtfulItems) {
                    if (item.replace("item:", "").replace("tag:", "").startsWith(ResourceHelper.getKey(itemStack.getItem()) + ":")) {
                        String damage = item.substring(item.lastIndexOf(":") + 1);
                        player.hurt(player.damageSources().generic(), Float.parseFloat(damage));
                        break;
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void onWaterConsumed(LivingEntityUseItemEvent.Finish destroyItemEvent) {
        if (!ServerConfig.penaltiesEnabled || ServerConfig.seaTicksWithoutWater == 0) {
            return;
        }

        DragonStateProvider.getOptional(destroyItemEvent.getEntity()).ifPresent(handler -> {
            if (handler.isDragon() && handler.getType() instanceof SeaDragonType seaDragonType) {
                ItemStack itemStack = destroyItemEvent.getItem();
                Player player = (Player) destroyItemEvent.getEntity();

                if (!player.level().isClientSide() && ServerConfig.seaAllowWaterBottles && itemStack.getItem() instanceof PotionItem) {
                    Optional<Potion> potion = PotionUtils.getPotion(itemStack);

                    if (potion.isPresent() && potion.get() == Potions.WATER.value() && DragonUtils.isDragonType(handler, DragonTypes.SEA)) {
                        seaDragonType.timeWithoutWater = Math.max(seaDragonType.timeWithoutWater - ServerConfig.seaTicksWithoutWaterRestored, 0);
                        PacketDistributor.sendToPlayersTrackingEntity(player, new SyncDragonType.Data(player.getId(), seaDragonType.writeNBT()));
                    }
                }

                if (itemStack.is(DSItemTags.SEA_ADDITIONAL_WATER_USABLES) && !player.level().isClientSide()) {
                    seaDragonType.timeWithoutWater = Math.max(seaDragonType.timeWithoutWater - ServerConfig.seaTicksWithoutWaterRestored, 0);
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonType.Data(player.getId(), seaDragonType.writeNBT()));
                }
            }
        });
    }

    public static boolean itemIsBlacklisted(Item item) {
        return DRAGON_BLACKLISTED_ITEMS.contains(item);
    }

    @SubscribeEvent
    public static void preventBlackListedItemsFromBeingEquipped(PlayerTickEvent.Pre event) {
        if (!ServerConfig.penaltiesEnabled) {
            return;
        }

        Player player = event.getEntity();
        DragonStateProvider.getOptional(player).ifPresent(dragonStateHandler -> {
            if (dragonStateHandler.isDragon()) {
                ItemStack mainHandItem = player.getMainHandItem();
                ItemStack offHandItem = player.getOffhandItem();

                if (!mainHandItem.isEmpty() && itemIsBlacklisted(mainHandItem.getItem())) {
                    player.getInventory().removeItem(mainHandItem);
                    player.drop(mainHandItem, false);
                }

                if (!offHandItem.isEmpty() && itemIsBlacklisted(offHandItem.getItem())) {
                    player.getInventory().removeItem(offHandItem);
                    player.drop(offHandItem, false);
                }
            }
        });
    }
}