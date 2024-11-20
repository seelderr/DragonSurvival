package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

@EventBusSubscriber(modid = DragonSurvival.MODID)
public class DragonGrowthHandler {
    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        DragonStateHandler data = DragonStateProvider.getData(player);

        if (!data.isDragon()) {
            return;
        }

        double growth = getGrowth(data.getLevel(), event.getItemStack().getItem());

        if (growth == 0) {
            return;
        }

        data.setSize(data.getSize() + growth, player); // TODO :: check if size can be reduced / increased

        if (!player.isCreative()) {
            event.getItemStack().shrink(1);
        }

        if (player.level().isClientSide()) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSize.Data(player.getId(), data.getSize()));
        DSAdvancementTriggers.BE_DRAGON.get().trigger((ServerPlayer) player, data.getSize(), data.getTypeName());
    }

    public static double getGrowth(final Holder<DragonLevel> dragonLevel, final Item item) {
        int growth = 0;

        for (MiscCodecs.GrowthItem growthItem : Objects.requireNonNull(dragonLevel).value().growthItems()) {
            //noinspection deprecation -> ignore
            if ((growth == 0 || Math.abs(growthItem.growthInTicks()) > Math.abs(growth)) && growthItem.items().contains(item.builtInRegistryHolder())) {
                growth = growthItem.growthInTicks();
            }
        }

        return dragonLevel.value().ticksToSize(growth);
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

        DragonLevel level = Objects.requireNonNull(data.getLevel()).value();
        double growth = level.ticksToSize(increment);

        if (growth > 0) {
            data.setSize(data.getSize() + growth, player);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSize.Data(player.getId(), data.getSize()));
            DSAdvancementTriggers.BE_DRAGON.get().trigger((ServerPlayer) player, data.getSize(), data.getTypeName());
            player.refreshDimensions();
        }
    }
}