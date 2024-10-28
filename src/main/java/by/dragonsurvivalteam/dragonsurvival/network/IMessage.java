package by.dragonsurvivalteam.dragonsurvival.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IMessage<T extends CustomPacketPayload> {
    static <T> void handleClient(final T message, final IPayloadContext context) {
    }

    static <T> void handleServer(final T message, final IPayloadContext context) {
    }
}