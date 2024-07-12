package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybinds;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncAbilityCasting;
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
    private static int castSlot = -1;

    @SubscribeEvent
    public static void abilityKeyBindingChecks(ClientTickEvent.Post clientTickEvent) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.player == null || instance.level == null)
            return;

        Player player = instance.player;
        if (player.isSpectator() || !DragonStateProvider.isDragon(player))
            return;

        DragonStateHandler dragonStateHandler = DragonStateProvider.getOrGenerateHandler(player);

        // Key to use ability is down
        // In alternate cast mode, the same key is held as the selected slot
        int selectedAbilitySlot = dragonStateHandler.getMagicData().getSelectedAbilitySlot();
        boolean isKeyDown = Keybinds.USE_ABILITY.isDown() || ClientConfig.alternateCastMode && (
                Keybinds.ABILITY1.isDown() && selectedAbilitySlot == 0
                        || Keybinds.ABILITY2.isDown() && selectedAbilitySlot == 1
                        || Keybinds.ABILITY3.isDown() && selectedAbilitySlot == 2
                        || Keybinds.ABILITY4.isDown() && selectedAbilitySlot == 3);

        // Get ability from currently cast slot
        ActiveDragonAbility ability = dragonStateHandler.getMagicData().getAbilityFromSlot(castSlot);

        // No ability = switch to selected slot
        if (ability == null) {
            castSlot = selectedAbilitySlot;
            ability = dragonStateHandler.getMagicData().getAbilityFromSlot(castSlot);
        }

        boolean canCast = ability.canCastSkill(player);

        if (castSlot != selectedAbilitySlot) { // ability slot has been changed, cancel any casts and put skills on cooldown if needed
            status = CastingStatus.Idle;
            //System.out.println(player + " ability changed from " + ability.getName() + " to " + dragonStateHandler.getMagicData().getAbilityFromSlot(slot).getName() + ".");
            if (castStartTime != -1 && canCast) {
                PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                //System.out.println(ability.getName() + " finished casting due to swap.");
            }
            hasCast = false;
            ability.onKeyReleased(player);
            castStartTime = -1;
            castSlot = selectedAbilitySlot;
            return;
        }

        if (isKeyDown) {
            switch (status) {
                case Idle -> {
                    castStartTime = -1;
                    status = CastingStatus.InProgress;

                    if (canCast) {
                        castStartTime = player.level().getGameTime();
                        PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), true, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                    }
                }
                case InProgress -> {
                    if (canCast && castStartTime == -1) {
                        castStartTime = player.level().getGameTime();
                        PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), true, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                    }
                    if (canCast && castStartTime != -1) {
                        PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), true, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                    }
                    if (!canCast && castStartTime != -1) {
                        PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                        ability.onKeyReleased(player);
                        status = CastingStatus.Idle;
                        castStartTime = -1;
                    }
                }
                case Stop -> {
                    PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                    ability.onKeyReleased(player);
                    status = CastingStatus.Idle;
                    castStartTime = -1;
                }
            }
        } else {
            if (status != CastingStatus.Idle) {
                PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                ability.onKeyReleased(player);
            }
            status = CastingStatus.Idle;
            castStartTime = -1;
            hasCast = false;
        }

        castSlot = selectedAbilitySlot;
    }
}