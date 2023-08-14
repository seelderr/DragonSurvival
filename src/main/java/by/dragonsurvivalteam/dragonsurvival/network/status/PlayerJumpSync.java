package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Jump animation length is 20.8 ticks */
public class PlayerJumpSync implements IMessage<PlayerJumpSync> {
	public int playerId;
	public int ticks;

	public PlayerJumpSync(int playerId, int ticks) {
		this.playerId = playerId;
		this.ticks = ticks;
	}

	public PlayerJumpSync() { /* Nothing to do */ }

	@Override
	public void encode(final PlayerJumpSync message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeByte(message.ticks);
	}

	@Override
	public PlayerJumpSync decode(final FriendlyByteBuf buffer) {
		return new PlayerJumpSync(buffer.readInt(), buffer.readByte());
	}

	@Override
	public void handle(final PlayerJumpSync message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handlePlayerJumpSync(message));
		}

		context.setPacketHandled(true);
	}
}