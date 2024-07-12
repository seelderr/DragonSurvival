package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybinds;
import by.dragonsurvivalteam.dragonsurvival.network.container.RequestOpenDragonInventory;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncDragonAbilitySlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKey(InputEvent.Key keyInputEvent) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !DragonStateProvider.isDragon(minecraft.player))
            return;

        DragonStateHandler dragonStateHandler = DragonStateProvider.getOrGenerateHandler(player);

        if (Keybinds.DRAGON_INVENTORY.consumeClick()) {
            if (minecraft.screen == null) {
                PacketDistributor.sendToServer(new RequestOpenDragonInventory.Data());
            } else {
                player.closeContainer();
            }

        } else if (Keybinds.TOGGLE_ABILITIES.consumeClick()) {
            dragonStateHandler.getMagicData().setRenderAbilities(!dragonStateHandler.getMagicData().shouldRenderAbilities());
            PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), dragonStateHandler.getMagicData().getSelectedAbilitySlot(), dragonStateHandler.getMagicData().shouldRenderAbilities()));

        } else if (Keybinds.NEXT_ABILITY.consumeClick()) {
            int nextSlot = dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 3 ? 0 : dragonStateHandler.getMagicData().getSelectedAbilitySlot() + 1;
            dragonStateHandler.getMagicData().setSelectedAbilitySlot(nextSlot);
            PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), dragonStateHandler.getMagicData().getSelectedAbilitySlot(), dragonStateHandler.getMagicData().shouldRenderAbilities()));

        } else if (Keybinds.PREV_ABILITY.consumeClick()) {
            int nextSlot = dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 0 ? 3 : dragonStateHandler.getMagicData().getSelectedAbilitySlot() - 1;
            dragonStateHandler.getMagicData().setSelectedAbilitySlot(nextSlot);
            PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), dragonStateHandler.getMagicData().getSelectedAbilitySlot(), dragonStateHandler.getMagicData().shouldRenderAbilities()));
        }


        if (!ClientConfig.alternateCastMode) {
            if (Keybinds.ABILITY1.consumeClick()) {
                if (dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()) != null)
                    dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(Minecraft.getInstance().player);
                dragonStateHandler.getMagicData().setSelectedAbilitySlot(0);
                PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), 0, dragonStateHandler.getMagicData().shouldRenderAbilities()));
            } else if (Keybinds.ABILITY2.consumeClick()) {
                if (dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()) != null)
                    dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(Minecraft.getInstance().player);
                dragonStateHandler.getMagicData().setSelectedAbilitySlot(1);
                PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), 1, dragonStateHandler.getMagicData().shouldRenderAbilities()));
            } else if (Keybinds.ABILITY3.consumeClick()) {
                if (dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()) != null)
                    dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(Minecraft.getInstance().player);
                dragonStateHandler.getMagicData().setSelectedAbilitySlot(2);
                PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), 2, dragonStateHandler.getMagicData().shouldRenderAbilities()));
            } else if (Keybinds.ABILITY4.consumeClick()) {
                if (dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()) != null)
                    dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(Minecraft.getInstance().player);
                dragonStateHandler.getMagicData().setSelectedAbilitySlot(3);
                PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), 3, dragonStateHandler.getMagicData().shouldRenderAbilities()));
            }
        } else {
            if (Keybinds.ABILITY1.isDown()) {
                if (dragonStateHandler.getMagicData().getSelectedAbilitySlot() != 0) {
                    if (dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()) != null)
                        dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(Minecraft.getInstance().player);
                    dragonStateHandler.getMagicData().setSelectedAbilitySlot(0);
                    PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), 0, dragonStateHandler.getMagicData().shouldRenderAbilities()));
                }
            } else if (Keybinds.ABILITY2.isDown()) {
                if (dragonStateHandler.getMagicData().getSelectedAbilitySlot() != 1) {
                    if (dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()) != null)
                        dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(Minecraft.getInstance().player);
                    dragonStateHandler.getMagicData().setSelectedAbilitySlot(1);
                    PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), 1, dragonStateHandler.getMagicData().shouldRenderAbilities()));
                }
            } else if (Keybinds.ABILITY3.isDown()) {
                if (dragonStateHandler.getMagicData().getSelectedAbilitySlot() != 2) {
                    if (dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()) != null)
                        dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(Minecraft.getInstance().player);
                    dragonStateHandler.getMagicData().setSelectedAbilitySlot(2);
                    PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), 2, dragonStateHandler.getMagicData().shouldRenderAbilities()));
                }
            } else if (Keybinds.ABILITY4.isDown()) {
                if (dragonStateHandler.getMagicData().getSelectedAbilitySlot() != 3) {
                    if (dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()) != null)
                        dragonStateHandler.getMagicData().getAbilityFromSlot(dragonStateHandler.getMagicData().getSelectedAbilitySlot()).onKeyReleased(Minecraft.getInstance().player);
                    dragonStateHandler.getMagicData().setSelectedAbilitySlot(3);
                    PacketDistributor.sendToServer(new SyncDragonAbilitySlot.Data(player.getId(), 3, dragonStateHandler.getMagicData().shouldRenderAbilities()));
                }
            }
        }
    }
}