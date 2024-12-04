package by.dragonsurvivalteam.dragonsurvival.network.syncing;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonPenaltyHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

// We getOrGenerateHandler here since we might not have created the handler when doing a SyncComplete (this happens when the player selects a dragon for the first time)
public class SyncComplete implements IMessage<SyncComplete.Data> {
    public static void handleClient(final Data message, final IPayloadContext context) {
        Entity entity = context.player().level().getEntity(message.playerId);

        if (entity instanceof Player player) {
            context.enqueueWork(() -> {
                DragonStateHandler handler = DragonStateProvider.getData(player);
                Holder<DragonType> previousType = handler.getDragonType();
                handler.deserializeNBT(player.registryAccess(), message.nbt);
                if (previousType != null && !previousType.is(handler.getType())) {
                    MagicData magicData = MagicData.getData(player);
                    magicData.refresh(handler.getType());
                }
                DSModifiers.updateAllModifiers(player);
                player.refreshDimensions();
            });
        }
    }

    public static void dropAllItemsInList(Player player, NonNullList<ItemStack> items) {
        items.forEach(stack -> {
            if (DragonPenaltyHandler.itemIsBlacklisted(stack.getItem())) {
                player.getInventory().removeItem(stack);
                player.drop(stack, false);
            }
        });
    }

    public static void handleDragonSync(Player player) {
        DragonStateHandler handler = DragonStateProvider.getData(player);
        DSModifiers.updateAllModifiers(player);
        player.refreshDimensions();

        // If we are a dragon, make sure to drop any blacklisted items equipped
        if (handler.isDragon()) {
            dropAllItemsInList(player, player.getInventory().armor);
            dropAllItemsInList(player, player.getInventory().offhand);
            ItemStack mainHandItem = player.getMainHandItem();

            if (DragonPenaltyHandler.itemIsBlacklisted(mainHandItem.getItem())) {
                player.getInventory().removeItem(mainHandItem);
                player.drop(mainHandItem, false);
            }

            if (player instanceof ServerPlayer serverPlayer) {
                DSAdvancementTriggers.BE_DRAGON.get().trigger(serverPlayer);
            }
        }
    }

    public static void handleServer(final Data message, final IPayloadContext context) {
        Player player = context.player();
        context.enqueueWork(() -> {
                    DragonStateHandler handler = DragonStateProvider.getData(player);
                    Holder<DragonType> previousType = handler.getDragonType();
                    handler.deserializeNBT(player.registryAccess(), message.nbt);
                    if (previousType != null && !previousType.is(handler.getType())) {
                        MagicData magicData = MagicData.getData(player);
                        magicData.refresh(handler.getType());
                    }
                    handleDragonSync(player);
                })
                .thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, message))
                .thenAccept(v -> context.reply(RequestClientData.INSTANCE));
    }

    public record Data(int playerId, CompoundTag nbt) implements CustomPacketPayload {
        public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "complete_data"));

        public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Data::playerId,
                ByteBufCodecs.COMPOUND_TAG, Data::nbt,
                Data::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}