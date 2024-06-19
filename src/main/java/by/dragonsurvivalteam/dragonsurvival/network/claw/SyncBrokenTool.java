package by.dragonsurvivalteam.dragonsurvival.network.claw;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.DRAGON_HANDLER;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncBrokenTool implements IMessage<SyncBrokenTool.Data> {

    public static void handleClient(final SyncBrokenTool.Data message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = ClientProxy.getLocalPlayer();

            if (player != null) {
                Entity entity = player.level().getEntity(message.playerId);

                if (entity instanceof Player) {
                    DragonStateProvider.getCap(entity).ifPresent(handler -> {
                        if (handler.switchedTool || handler.switchedWeapon) {
                            player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        } else {
                            handler.getClawToolData().getClawsInventory().setItem(message.slot, ItemStack.EMPTY);
                        }
                    });
                }
            }
        });
    }

    public record Data(int playerId, int slot) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "broken_tool"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            Data::playerId,
            ByteBufCodecs.INT,
            Data::slot,
            Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
