package by.dragonsurvivalteam.dragonsurvival.network.dragon_editor;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncDragonSkinSettings implements IMessage<SyncDragonSkinSettings> {
	public int playerId;
	public boolean newborn;
	public boolean young;
	public boolean adult;

	public SyncDragonSkinSettings() {}

	public SyncDragonSkinSettings(int playerId, boolean newborn, boolean young, boolean adult) {
		this.playerId = playerId;
		this.newborn = newborn;
		this.young = young;
		this.adult = adult;
	}

	@Override
	public void encode(final SyncDragonSkinSettings message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.newborn);
		buffer.writeBoolean(message.young);
		buffer.writeBoolean(message.adult);
	}

	@Override
	public SyncDragonSkinSettings decode(FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		boolean newborn = buffer.readBoolean();
		boolean young = buffer.readBoolean();
		boolean adult = buffer.readBoolean();
		return new SyncDragonSkinSettings(playerId, newborn, young, adult);
	}

	@Override
	public void handle(final SyncDragonSkinSettings message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncDragonSkinSettings(message));
		}

		if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				DragonStateProvider.getCap(sender).ifPresent(handler -> {
					handler.getSkinData().renderNewborn = message.newborn;
					handler.getSkinData().renderYoung = message.young;
					handler.getSkinData().renderAdult = message.adult;
				});

				// Make the other clients aware of the changes
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncDragonSkinSettings(sender.getId(), message.newborn, message.young, message.adult));
			}
		}

		context.setPacketHandled(true);
	}
}