package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class SyncBeginCast implements IMessage<SyncBeginCast.Data> {

    public static void handleServer(final SyncBeginCast.Data message, final IPayloadContext context) {
        Player sender = context.player();
        context.enqueueWork(() -> {
            MagicData magicData = MagicData.getData(sender);
            // The server can deny the cast if the player doesn't meet the entity predicate for the casting
            if(!magicData.setAbilitySlotAndBeginCastServer(message.abilitySlot, (ServerPlayer) sender)) {
                context.reply(new SyncStopCast(message.playerId()));
            }
        });
    }

    public record Data(int playerId, int abilitySlot) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "ability_casting"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::playerId,
                ByteBufCodecs.VAR_INT,
                Data::abilitySlot,
                Data::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}