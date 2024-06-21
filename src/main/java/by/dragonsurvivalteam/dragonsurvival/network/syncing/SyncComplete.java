package by.dragonsurvivalteam.dragonsurvival.network.syncing;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonPenaltyHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// We getOrGenerateHandler here since we might not have created the handler when doing a SyncComplete (this happens when the player selects a dragon for the first time)
public class SyncComplete implements IMessage<SyncComplete.Data> {
	public static void handleClient(final Data message, final IPayloadContext context) {
		Entity entity = context.player().level().getEntity(message.playerId);
		if(entity instanceof Player player) {
			context.enqueueWork(() -> {
				DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
				handler.deserializeNBT(player.registryAccess(), message.nbt);
				player.refreshDimensions();
			});
		}
	}

	private static void dropAllItemsInList(Player player, NonNullList<ItemStack> items) {
		items.forEach(stack -> {
			if(DragonPenaltyHandler.itemIsBlacklisted(stack.getItem())) {
				player.getInventory().removeItem(stack);
				player.drop(stack, false);
			}
		});
	}

	public static void handleServer(final Data message, final IPayloadContext context) {
		Player player = context.player();
		context.enqueueWork(() -> {
					DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
					handler.deserializeNBT(player.registryAccess(), message.nbt);
					player.refreshDimensions();

					// If we are a dragon, make sure to drop any blacklisted items equipped
					if(handler.isDragon()) {
						dropAllItemsInList(player, player.getInventory().armor);
						dropAllItemsInList(player, player.getInventory().offhand);
						ItemStack mainHandItem = player.getMainHandItem();
						player.getInventory().removeItem(mainHandItem);
						player.drop(mainHandItem, false);
					}
				})
				.thenRun(() -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, message))
				.thenAccept(v  -> context.reply(new RequestClientData.Data()));
	}

	public record Data(int playerId, CompoundTag nbt) implements CustomPacketPayload {

		public static final Type<Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "complete_data"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				Data::playerId,
				ByteBufCodecs.COMPOUND_TAG,
				Data::nbt,
				Data::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}