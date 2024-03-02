package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DiggingStatus implements IMessage<DiggingStatus> {
	public int playerId;
	public boolean status;

	public DiggingStatus(int playerId, boolean status) {
		this.playerId = playerId;
		this.status = status;
	}

	public DiggingStatus() { /* Nothing to do */ }

	@Override
	public void encode(final DiggingStatus message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.status);
	}

	@Override
	public DiggingStatus decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		boolean status = buffer.readBoolean();
		return new DiggingStatus(playerId, status);
	}

	@Override
	public void handle(final DiggingStatus message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleDiggingStatus(message));
		}

		context.setPacketHandled(true);
	}
}