package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenDragonAltar implements IMessage<OpenDragonAltar> {
	public OpenDragonAltar() { /* Nothing to do */ }

	@Override
	public void encode(final OpenDragonAltar message, final FriendlyByteBuf buffer) { /* Nothing to do */ }

	@Override
	public OpenDragonAltar decode(final FriendlyByteBuf buffer) {
		return new OpenDragonAltar();
	}

	@Override
	public void handle(final OpenDragonAltar message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(ClientProxy::handleOpenDragonAltar);
		}

		context.setPacketHandled(true);
	}
}