package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncTreasureRestStatus implements IMessage<SyncTreasureRestStatus> {
	public int playerId;
	public boolean state;

	public SyncTreasureRestStatus() { /* Nothing to do */ }

	public SyncTreasureRestStatus(int playerId, boolean state) {
		this.playerId = playerId;
		this.state = state;
	}

	@Override
	public void encode(final SyncTreasureRestStatus message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}

	@Override
	public SyncTreasureRestStatus decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		return new SyncTreasureRestStatus(playerId, state);
	}

	@Override
	public void handle(final SyncTreasureRestStatus message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncTreasureRestStatus(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer entity = context.getSender();

			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(handler -> {
					boolean update = false;

					if (message.state != handler.treasureResting) {
						handler.treasureRestTimer = 0;
						handler.treasureSleepTimer = 0;
						update = true;
					}

					handler.treasureResting = message.state;

					if (update) {
						((ServerLevel) entity.level).updateSleepingPlayerList();
					}
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncTreasureRestStatus(entity.getId(), message.state));
			}
		}

		context.setPacketHandled(true);
	}
}