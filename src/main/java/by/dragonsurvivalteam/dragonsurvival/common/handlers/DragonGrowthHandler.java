package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EventBusSubscriber(modid = DragonSurvival.MODID)
public class DragonGrowthHandler {
    @Translation(type = Translation.Type.MISC, comments = "§6You need another type of growth artifact:§r %1$s")
    private static final String INVALID_ITEM = Translation.Type.GUI.wrap("growth_hud.invalid_item");

    public static final long NEWBORN_TO_YOUNG = TimeUnit.SECONDS.convert(3, TimeUnit.HOURS);
    public static final long YOUNG_TO_ADULT = TimeUnit.SECONDS.convert(15, TimeUnit.HOURS);
    public static final long ADULT_TO_ANCIENT = TimeUnit.SECONDS.convert(24, TimeUnit.HOURS);
    public static final long ANCIENT = TimeUnit.SECONDS.convert(30, TimeUnit.DAYS);

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        Player player = event.getEntity();
        Level world = player.getCommandSenderWorld();

        DragonStateProvider.getOptional(player).ifPresent(handler -> {
            if (!handler.isDragon()) {
                return;
            }

            double size = handler.getSize();

            if (size >= ServerConfig.maxGrowthSize) {
                return;
            }

            boolean canContinue = false;

            HashSet<Item> newbornList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growNewborn);
            HashSet<Item> youngList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growYoung);
            HashSet<Item> adultList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growAdult);

            HashSet<Item> allowedItems = new HashSet<>();

            // FIXME level :: add as part of the registry
//            switch (handler.getLevel()) {
//                case NEWBORN:
//                    if (newbornList.contains(item)) {
//                        canContinue = true;
//                    } else if (youngList.contains(item) || adultList.contains(item)) {
//                        allowedItems = newbornList;
//                    }
//
//                    break;
//                case YOUNG:
//                    if (youngList.contains(item)) {
//                        canContinue = true;
//                    } else if (newbornList.contains(item) || adultList.contains(item)) {
//                        allowedItems = youngList;
//                    }
//
//                    break;
//                case ADULT:
//                    if (adultList.contains(item)) {
//                        canContinue = true;
//                    } else if (newbornList.contains(item) || youngList.contains(item)) {
//                        allowedItems = adultList;
//                    }
//
//                    break;
//            }

            if (!canContinue) {
                if (!allowedItems.isEmpty() && world.isClientSide()) {
                    List<String> displayData = allowedItems.stream().map(i -> new ItemStack(i).getDisplayName().getString()).toList();
                    StringBuilder result = new StringBuilder();

                    for (int i = 0; i < displayData.size(); i++) {
                        String entry = displayData.get(i);

                        result.append(entry).append(i + 1 < displayData.size() ? ", " : "");
                    }

                    player.displayClientMessage(Component.translatable(INVALID_ITEM, result), false);
                }

                return;
            }

            int increment = getIncrement(item, handler.getLevel());
            size += increment;
            handler.setSize(size, player);

            if (!player.isCreative()) {
                event.getItemStack().shrink(1);
            }

            if (world.isClientSide()) {
                return;
            }

            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSize.Data(player.getId(), size));
            DSAdvancementTriggers.BE_DRAGON.get().trigger((ServerPlayer) player, handler.getSize(), handler.getTypeName());
        });
    }

    public static int getIncrement(Item item, Holder<DragonLevel> level) {
        HashSet<Item> newbornList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growNewborn);
        HashSet<Item> youngList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growYoung);
        HashSet<Item> adultList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growAdult);

        int increment = 0;

        if (item == DSItems.STAR_BONE.value()) {
            return -2;
        }

        // FIXME level :: add as part of the registry
//        switch (level) {
//            case NEWBORN:
//                if (adultList.contains(item)) {
//                    increment = 3;
//                } else if (youngList.contains(item)) {
//                    increment = 2;
//                } else if (newbornList.contains(item)) {
//                    increment = 1;
//                }
//                break;
//            case YOUNG:
//                if (adultList.contains(item)) {
//                    increment = 2;
//                } else if (youngList.contains(item)) {
//                    increment = 1;
//                }
//                break;
//
//            case ADULT:
//                if (adultList.contains(item)) {
//                    increment = 1;
//                }
//                break;
//        }

        return increment;
    }

    @SubscribeEvent
    public static void onPlayerUpdate(PlayerTickEvent.Pre event) {
        if (!ServerConfig.naturalGrowth) {
            return;
        }

        Player player = event.getEntity();
        Level world = player.getCommandSenderWorld();

        if (world.isClientSide()) {
            return;
        }

        if (!DragonStateProvider.isDragon(player)) {
            return;
        }

        if (player.tickCount % (Functions.secondsToTicks(60)) != 0) {
            return;
        }

        DragonStateProvider.getOptional(player).ifPresent(handler -> {
            if (handler.growing) {
                /*
                    1. Newborn - young = 3-4 h
                    2. Young - adult = 15-20h
                    3. Adult - maximum growth = 24h
                    4. After maximum growth. = 30 days for max growth
                 */

                double growth = 10; // FIXME level
                double timeIncrement = Functions.secondsToTicks(60);

                // FIXME level :: add as part of the registry
//                if (handler.getSize() < YOUNG.size) {
//                    growth = (double) (YOUNG.size - NEWBORN.size) / Functions.secondsToTicks(NEWBORN_TO_YOUNG) * timeIncrement * ServerConfig.newbornGrowthModifier;
//                } else if (handler.getSize() < ADULT.size) {
//                    growth = (double) (ADULT.size - YOUNG.size) / Functions.secondsToTicks(YOUNG_TO_ADULT) * timeIncrement * ServerConfig.youngGrowthModifier;
//                } else if (handler.getSize() < ADULT.maxSize) {
//                    growth = (double) (40 - ADULT.size) / Functions.secondsToTicks(ADULT_TO_ANCIENT) * timeIncrement * ServerConfig.adultGrowthModifier;
//                } else {
//                    growth = (double) (60 - 40) / Functions.secondsToTicks(ANCIENT) * timeIncrement * ServerConfig.maxGrowthModifier;
//                }

                double size = handler.getSize() + growth;
                size = Math.min(size, ServerConfig.maxGrowthSize);

                if (handler.getSize() != size) {
                    handler.setSize(size, player);

                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSize.Data(player.getId(), size));
                    DSAdvancementTriggers.BE_DRAGON.get().trigger((ServerPlayer) player, handler.getSize(), handler.getTypeName());

                    player.refreshDimensions();
                }
            }
        });
    }
}