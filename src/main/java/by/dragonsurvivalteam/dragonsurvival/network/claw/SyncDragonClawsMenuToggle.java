package by.dragonsurvivalteam.dragonsurvival.network.claw;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncDragonClawsMenuToggle implements IMessage<SyncDragonClawsMenuToggle.Data> {

    public static void handleClient(final SyncDragonClawsMenuToggle.Data message, final IPayloadContext context) {
        Player sender = context.player();

        context.enqueueWork(() -> {
            DragonStateProvider.getOptional(sender).ifPresent(handler -> {
                handler.getClawToolData().setMenuOpen(message.state);

                if (sender.containerMenu instanceof DragonContainer container) {
                    container.update();
                }
            });
        });
    }

    public static void handleServer(final SyncDragonClawsMenuToggle.Data message, final IPayloadContext context) {
        Player sender = context.player();

        context.enqueueWork(() -> {
            DragonStateProvider.getOptional(sender).ifPresent(handler -> {
                handler.getClawToolData().setMenuOpen(message.state);

                if (sender.containerMenu instanceof DragonContainer container) {
                    container.update();
                }
            });
        });
    }

    public record Data(boolean state) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dragon_claw_menu_toggle"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                Data::state,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}