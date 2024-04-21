package by.dragonsurvivalteam.dragonsurvival.network.claw;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncBrokenTool(int playerId, int slot) {
    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeInt(playerId);
        buffer.writeInt(slot);
    }

    public static SyncBrokenTool decode(final FriendlyByteBuf buffer) {
        return new SyncBrokenTool(buffer.readInt(), buffer.readInt());
    }

    public static void handle(final SyncBrokenTool message, final Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                Player localPlayer = ClientProxy.getLocalPlayer();

                if (localPlayer != null) {
                    Entity entity = localPlayer.level().getEntity(message.playerId);

                    if (entity instanceof Player) {
                        DragonStateHandler handler = DragonUtils.getHandler(entity);

                        if (handler.switchedTool || handler.switchedWeapon) {
                            localPlayer.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        } else {
                            handler.getClawToolData().getClawsInventory().setItem(message.slot, ItemStack.EMPTY);
                        }
                    }
                }
            });
        }

        context.setPacketHandled(true);
    }
}
