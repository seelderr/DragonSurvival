package by.dragonsurvivalteam.dragonsurvival.network.flight;

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

public class SyncSpinStatus implements IMessage<SyncSpinStatus> {
	public int playerId;
	public int spinAttack;
	public int spinCooldown;
	public boolean spinLearned;

	public SyncSpinStatus(int playerId, int spinAttack, int spinCooldown, boolean spinLearned) {
		this.playerId = playerId;
		this.spinAttack = spinAttack;
		this.spinCooldown = spinCooldown;
		this.spinLearned = spinLearned;
	}

	public SyncSpinStatus() { /* Nothing to do */ }

	@Override
	public void encode(final SyncSpinStatus message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeInt(message.spinAttack);
		buffer.writeInt(message.spinCooldown);
		buffer.writeBoolean(message.spinLearned);
	}

	@Override
	public SyncSpinStatus decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		int spinAttack = buffer.readInt();
		int spinCooldown = buffer.readInt();
		boolean spinLearned = buffer.readBoolean();
		return new SyncSpinStatus(playerId, spinAttack, spinCooldown, spinLearned);
	}

	@Override
	public void handle(final SyncSpinStatus message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncSpinStatus(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				DragonStateProvider.getCap(sender).ifPresent(handler -> {
					handler.getMovementData().spinAttack = message.spinAttack;
					handler.getMovementData().spinCooldown = message.spinCooldown;
					handler.getMovementData().spinLearned = message.spinLearned;
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncSpinStatus(sender.getId(), message.spinAttack, message.spinCooldown, message.spinLearned));
			}
		}

		context.setPacketHandled(true);
	}
}