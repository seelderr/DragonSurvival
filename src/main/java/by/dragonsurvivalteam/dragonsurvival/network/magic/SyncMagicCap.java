package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.MagicCap;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncMagicCap implements IMessage<SyncMagicCap> {
	public int playerId;
	public MagicCap cap;
	public CompoundTag nbt;

	public SyncMagicCap() { /* Nothing to do */ }

	public SyncMagicCap(int playerId, final MagicCap cap) {
		this.cap = cap;
		this.playerId = playerId;
	}

	public SyncMagicCap(int playerId, final CompoundTag nbt) {
		this.nbt = nbt;
		this.playerId = playerId;
	}

	@Override
	public void encode(final SyncMagicCap message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.cap != null ? message.cap.writeNBT() : nbt);
	}

	@Override
	public SyncMagicCap decode(final FriendlyByteBuf buffer) {
		return new SyncMagicCap(buffer.readInt(), buffer.readNbt());
	}

	@Override
	public void handle(final SyncMagicCap message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncMagicCap(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			context.enqueueWork(()-> {
				ServerPlayer sender = context.getSender();

				DragonStateProvider.getCap(sender).ifPresent(handler -> {
					handler.getMagicData().readNBT(message.nbt);
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncMagicCap(sender.getId(), handler.getMagicData()));
				});
			});
		}

		context.setPacketHandled(true);
	}
}