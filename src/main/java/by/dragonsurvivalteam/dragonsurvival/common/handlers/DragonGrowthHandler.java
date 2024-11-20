package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
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

@EventBusSubscriber(modid = DragonSurvival.MODID)
public class DragonGrowthHandler {
    @Translation(type = Translation.Type.MISC, comments = "§6You need another type of growth artifact:§r %1$s")
    private static final String INVALID_ITEM = Translation.Type.GUI.wrap("growth_hud.invalid_item");

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
            boolean canContinue = false;

//            HashSet<Item> newbornList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growNewborn);
//            HashSet<Item> youngList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growYoung);
//            HashSet<Item> adultList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growAdult);

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
//        HashSet<Item> newbornList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growNewborn);
//        HashSet<Item> youngList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growYoung);
//        HashSet<Item> adultList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growAdult);

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
        Player player = event.getEntity();

        if (!ServerConfig.naturalGrowth || player.level().isClientSide()) {
            return;
        }

        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.isDragon() || !data.isGrowing) {
            return;
        }

        int increment = Functions.secondsToTicks(60);

        if (player.tickCount % increment != 0) {
            return;
        }

        //noinspection DataFlowIssue -> level is present
        DragonLevel level = data.getLevel().value();
        double growth = (level.sizeRange().max() - level.sizeRange().min()) / Functions.secondsToTicks(level.ticksUntilGrown()) * increment;

        if (growth > 0) {
            data.setSize(data.getSize() + growth, player);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSize.Data(player.getId(), data.getSize()));
            DSAdvancementTriggers.BE_DRAGON.get().trigger((ServerPlayer) player, data.getSize(), data.getTypeName());
            player.refreshDimensions();
        }
    }
}