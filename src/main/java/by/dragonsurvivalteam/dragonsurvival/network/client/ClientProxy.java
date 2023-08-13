package by.dragonsurvivalteam.dragonsurvival.network.client;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawRender;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.dragonsurvivalteam.dragonsurvival.network.dragon_editor.SyncDragonSkinSettings;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** To avoid loading client classes on the server side */
public class ClientProxy {
    public static void handleSyncDragonClawRender(final SyncDragonClawRender message) {
        Player localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            Level world = localPlayer.level;
            Entity entity = world.getEntity(message.playerId);

            if (entity instanceof Player) {
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> dragonStateHandler.getClawToolData().shouldRenderClaws = message.state);
            }
        }
    }

    public static void handleSyncDragonClawsMenu(final SyncDragonClawsMenu message) {
        Player localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            Entity entity = localPlayer.level.getEntity(message.playerId);

            if (entity instanceof Player) {
                DragonStateProvider.getCap(entity).ifPresent(handler -> {
                    handler.getClawToolData().setMenuOpen(message.state);
                    handler.getClawToolData().setClawsInventory(message.clawInventory);
                });
            }
        }
    }

    public static void handleSyncDragonSkinSettings(final SyncDragonSkinSettings message) {
        Player localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            Level world = localPlayer.level;
            Entity entity = world.getEntity(message.playerId);

            if (entity instanceof Player) {
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                    dragonStateHandler.getSkinData().renderNewborn = message.newborn;
                    dragonStateHandler.getSkinData().renderYoung = message.young;
                    dragonStateHandler.getSkinData().renderAdult = message.adult;
                });
            }
        }
    }

    public static void requestClientData(final DragonStateHandler handler) {
        if (handler == DragonUtils.getHandler(Minecraft.getInstance().player)) {
            ClientEvents.sendClientData(new RequestClientData(handler.getType(), handler.getLevel()));
        }
    }
}
