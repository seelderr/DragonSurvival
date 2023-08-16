package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenDragonEditorPacket implements IMessage<OpenDragonEditorPacket> {

	public OpenDragonEditorPacket() { /* Nothing to do */ }

	@Override
	public void encode(final OpenDragonEditorPacket message, final FriendlyByteBuf buffer) { /* Nothing to do */ }

	@Override
	public OpenDragonEditorPacket decode(final FriendlyByteBuf buffer) {
		return new OpenDragonEditorPacket();
	}

	@Override
	public void handle(final OpenDragonEditorPacket message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(ClientProxy::handleOpenDragonEditorPacket);
		}

		context.setPacketHandled(true);
	}
}