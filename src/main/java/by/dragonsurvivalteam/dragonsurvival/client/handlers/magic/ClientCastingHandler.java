package by.dragonsurvivalteam.dragonsurvival.client.handlers.magic;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
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
    public static final byte StatusIdle = 0;
    public static final byte StatusInProgress = 1;
    public static final byte StatusStop = 2;
    public static byte status = StatusIdle;
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

        boolean isKeyDown = Keybinds.USE_ABILITY.isDown() || ClientConfig.alternateCastMode && (
                Keybinds.ABILITY1.isDown() && dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 0
                        || Keybinds.ABILITY2.isDown() && dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 1
                        || Keybinds.ABILITY3.isDown() && dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 2
                        || Keybinds.ABILITY4.isDown() && dragonStateHandler.getMagicData().getSelectedAbilitySlot() == 3);

        int slot = dragonStateHandler.getMagicData().getSelectedAbilitySlot();
        ActiveDragonAbility ability = dragonStateHandler.getMagicData().getAbilityFromSlot(castSlot);

        if (ability == null) {
            castSlot = slot;
            ability = dragonStateHandler.getMagicData().getAbilityFromSlot(castSlot);
        }

        if (castSlot != slot) { // ability slot has been changed, cancel any casts and put skills on cooldown if needed
            status = StatusIdle;
            //System.out.println(player + " ability changed from " + ability.getName() + " to " + dragonStateHandler.getMagicData().getAbilityFromSlot(slot).getName() + ".");
            if (castStartTime != -1 && ability.canCastSkill(player)) {
                PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
                //System.out.println(ability.getName() + " finished casting due to swap.");
            }
            hasCast = false;
            ability.onKeyReleased(player);
            castStartTime = -1;
            castSlot = slot;
            return;
        }

        if (status == StatusIdle && isKeyDown) {
            castStartTime = -1;
            status = StatusInProgress;
        }

        if (status == StatusInProgress && !isKeyDown) {
            castStartTime = -1;
            status = StatusStop;
        }

        if (!isKeyDown) {
            castStartTime = -1;
            hasCast = false;
        }

        if (status == StatusInProgress && ability.canCastSkill(player)) {
            if (castStartTime == -1)
                castStartTime = player.level().getGameTime();
            PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), true, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
        } else if (status == StatusStop || status == StatusInProgress && !ability.canCastSkill(player) && castStartTime != -1) {
            PacketDistributor.sendToServer(new SyncAbilityCasting.Data(player.getId(), false, castSlot, ability.saveNBT(), castStartTime, player.level().getGameTime()));
            ability.onKeyReleased(player);
            status = StatusIdle;
            castStartTime = -1;
        }
        castSlot = slot;
    }
}