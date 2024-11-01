package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.MagicCap;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAbilityCasting;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncDragonAbilitySlot;
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

        DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);
        MagicCap magicData = dragonStateHandler.getMagicData();

        // TODO: all of this needs a rework, the current code just the original updated to fit the new Keybinds
        // onKeyReleased gets triggered twice, the code flow isn't fully clear, etc...

        // Toggle HUD visibility
        if (Keybind.TOGGLE_ABILITIES.consumeClick()) {
            magicData.setRenderAbilities(!magicData.shouldRenderAbilities());
        }

        // Check ability key
        // TODO: This might incorrectly forget to stop casting if the selected slot gets changed externally
        int lastSelectedSlot = magicData.getSelectedAbilitySlot();

        // Check for slot selection
        int selectedSlot = lastSelectedSlot;

        // Check for prev/next keys first
        if (Keybind.NEXT_ABILITY.consumeClick()) {
            selectedSlot = (selectedSlot + 1) % slotKeybinds.length;
        }
        if (Keybind.PREV_ABILITY.consumeClick()) {
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
            ActiveDragonAbility lastAbility = magicData.getAbilityFromSlot(lastSelectedSlot);
            if (lastAbility != null && castStartTime != -1 && lastAbility.canCastSkill(player)) {
                lastAbility.onKeyReleased(player);
                PacketDistributor.sendToServer(new SyncAbilityCasting.Data(
                        player.getId(),
                        false,
                        lastSelectedSlot,
                        lastAbility.saveNBT(),
                        castStartTime,
                        player.level().getGameTime()));
                //System.out.println(ability.getName() + " finished casting due to swap.");
            }
            hasCast = false;
            status = CastingStatus.Idle;
            castStartTime = -1;

            // Update slot
            magicData.setSelectedAbilitySlot(selectedSlot);
            PacketDistributor.sendToServer(
                    new SyncDragonAbilitySlot.Data(
                            player.getId(),
                            selectedSlot,
                            magicData.shouldRenderAbilities()
                    )
            );
        }

        boolean isAbilityKeyDown;
        if (!ClientConfig.alternateCastMode) {
            isAbilityKeyDown = Keybind.USE_ABILITY.isDown();
        } else {
            isAbilityKeyDown = slotKeybinds.length > selectedSlot && slotKeybinds[selectedSlot].isDown();
        }
        // Not holding ability key - return early
        if (!isAbilityKeyDown) {
            if (status != CastingStatus.Idle) {
                var ability = magicData.getAbilityFromSlot(selectedSlot);
                if (ability != null) {
                    PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, selectedSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                    ability.onKeyReleased(player);
                }
            }
            status = CastingStatus.Idle;
            castStartTime = -1;
            hasCast = false;
            return;
        }

        // Proceed with casting
        ActiveDragonAbility ability = magicData.getAbilityFromSlot(selectedSlot);
        if (ability == null) return;

        boolean canCast = ability.canCastSkill(player);
        switch (status) {
            case Idle -> {
                castStartTime = -1;
                status = CastingStatus.InProgress;

                if (canCast) {
                    castStartTime = player.level().getGameTime();
                    PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), true, selectedSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                }
            }
            case InProgress -> {
                if (canCast) {
                    if (castStartTime == -1) {
                        castStartTime = player.level().getGameTime();
                    }
                    PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), true, selectedSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                } else {
                    if (castStartTime != -1) {
                        PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, selectedSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                        ability.onKeyReleased(player);
                    }
                    status = CastingStatus.Idle;
                    castStartTime = -1;
                }
            }
            case Stop -> {
                PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, selectedSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                ability.onKeyReleased(player);
                status = CastingStatus.Idle;
                castStartTime = -1;
            }
        }
    }
}