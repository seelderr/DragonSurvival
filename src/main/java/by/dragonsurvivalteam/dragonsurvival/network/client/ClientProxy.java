package by.dragonsurvivalteam.dragonsurvival.network.client;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawRender;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawsMenu;
import by.dragonsurvivalteam.dragonsurvival.network.dragon_editor.SyncDragonSkinSettings;
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
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> dragonStateHandler.getClawToolData().renderClaws = message.state);
            }
        }
    }

    public static void handleSyncDragonClawsMenu(final SyncDragonClawsMenu message) {
        Player localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            Entity entity = localPlayer.level.getEntity(message.playerId);

            if (entity instanceof Player) {
                DragonStateProvider.getCap(entity).ifPresent(handler -> {
                    handler.getClawToolData().setClawsMenuOpen(message.state);
                    handler.getClawToolData().setClawsInventory(message.inv);
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
}
