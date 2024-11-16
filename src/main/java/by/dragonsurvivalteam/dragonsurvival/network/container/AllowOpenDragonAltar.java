package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public record AllowOpenDragonAltar() implements CustomPacketPayload {
    public static final AllowOpenDragonAltar INSTANCE = new AllowOpenDragonAltar();
    public static final StreamCodec<ByteBuf, AllowOpenDragonAltar> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<AllowOpenDragonAltar> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "open_dragon_altar"));

    public static void handleClient(final AllowOpenDragonAltar ignored, final IPayloadContext context) {
        context.enqueueWork(ClientProxy::handleOpenDragonAltar);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}