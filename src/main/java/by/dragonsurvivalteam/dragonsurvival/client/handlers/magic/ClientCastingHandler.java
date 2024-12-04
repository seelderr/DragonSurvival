package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncBeginCast;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncStopCast;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(Dist.CLIENT)
public class ClientCastingHandler {
    public enum CastingStatus {
        Idle,
        InProgress,
        Stop
    }

    public static CastingStatus status = CastingStatus.Idle;
    public static boolean hasCast = false;
    public static long castStartTime = -1;

    private static final Keybind[] slotKeybinds = new Keybind[]{
            Keybind.ABILITY1,
            Keybind.ABILITY2,
            Keybind.ABILITY3,
            Keybind.ABILITY4
    };

    @SubscribeEvent // FIXME :: why isnt this using the keybind event
    public static void onTick(ClientTickEvent.Post clientTickEvent) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.player == null || instance.level == null)
            return;

        Player player = instance.player;
        if (player.isSpectator() || !DragonStateProvider.isDragon(player))
            return;

        MagicData magicData = MagicData.getData(player);
        // Toggle HUD visibility
        if (Keybind.TOGGLE_ABILITIES.consumeClick()) {
            magicData.setRenderAbilities(!magicData.shouldRenderAbilities());
        }

        // Check ability key
        int lastSelectedSlot = magicData.getSelectedAbilitySlot();

        // Check for slot selection
        int selectedSlot = lastSelectedSlot;

        // Check for prev/next keys first
        if (Keybind.NEXT_ABILITY.consumeClick()) {
            selectedSlot = (selectedSlot + 1) % slotKeybinds.length;
        }
        if (Keybind.PREVIOUS_ABILITY.consumeClick()) {
            // Add length because % can return a negative remainder
            selectedSlot = (selectedSlot - 1 + slotKeybinds.length) % slotKeybinds.length;
        }

        // Select the slot of the most recently pressed ability key, check for new keypresses
        // (This overrides the prev/next keypress)
        for (int i = 0; i < slotKeybinds.length; i++) {
            if (slotKeybinds[i].consumeClick()) {
                selectedSlot = i;
            }
        }
        // Was selected slot changed?
        if (selectedSlot != lastSelectedSlot) {
            // Cancel casting if in progress
            if (magicData.isCasting()) {
                magicData.stopCasting();
                PacketDistributor.sendToServer(new SyncStopCast(player.getId()));
            }

            // Update slot
            magicData.setSelectedAbilitySlot(selectedSlot);
        }

        boolean isAbilityKeyDown;
        if (!ClientConfig.alternateCastMode) {
            isAbilityKeyDown = Keybind.USE_ABILITY.isDown();
        } else {
            isAbilityKeyDown = slotKeybinds.length > selectedSlot && slotKeybinds[selectedSlot].isDown();
        }
        // Not holding ability key - return early
        if (!isAbilityKeyDown) {
            if (magicData.isCasting()) {
                magicData.stopCasting();
                PacketDistributor.sendToServer(new SyncStopCast(player.getId()));
            }

            // Now that the player has released the ability key, we can allow them to attempt to cast again
            magicData.setCastWasDenied(false);

            return;
        }

        // Proceed with casting (ignore anything blocking the cast from happening; we'll let the server deny the client later)
        if(!magicData.isCasting() && magicData.canBeginCast(selectedSlot)) {
            magicData.setAbilitySlotAndBeginCastClient(selectedSlot);
            PacketDistributor.sendToServer(new SyncBeginCast.Data(player.getId(), selectedSlot));
        }
    }
}