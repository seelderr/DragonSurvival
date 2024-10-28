package by.dragonsurvivalteam.dragonsurvival.network.dragon_editor;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncPlayerSkinPreset implements IMessage<SyncPlayerSkinPreset.Data> {
    public static void handleClient(final SyncPlayerSkinPreset.Data message, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleSyncPlayerSkinPreset(message, context.player().registryAccess()));
    }

    public static void handleServer(final SyncPlayerSkinPreset.Data message, final IPayloadContext context) {
        Player sender = context.player();

        context.enqueueWork(() -> {
            DragonStateProvider.getOptional(sender).ifPresent(handler -> {
                handler.getSkinData().skinPreset = new SkinPreset();
                handler.getSkinData().skinPreset.deserializeNBT(sender.registryAccess(), message.preset());
            });
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(sender, message));
    }

    public record Data(int playerId, CompoundTag preset) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "player_skin_preset"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.COMPOUND_TAG,
                Data::preset,
                Data::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}