package by.dragonsurvivalteam.dragonsurvival.network;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestClientData implements IMessage<RequestClientData> {
	public DragonStateHandler handler;
	public AbstractDragonType type;
	public DragonLevel level;

	public RequestClientData(final AbstractDragonType type, final  DragonLevel level) {
		this.type = type;
		this.level = level;
	}

	public RequestClientData() { /* Nothing to do */ }

	@Override
	public void encode(final RequestClientData message, final FriendlyByteBuf buffer) {
		buffer.writeUtf(message.type != null ? message.type.getTypeName() : "none");
		buffer.writeEnum(message.level);
	}

	@Override
	public RequestClientData decode(final FriendlyByteBuf buffer) {
		String type = buffer.readUtf();
		return new RequestClientData(type.equals("none") ? null : DragonTypes.getStaticSubtype(type), buffer.readEnum(DragonLevel.class));
	}

	@Override
	public void handle(final RequestClientData message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleRequestClientData(message));
		}

		context.setPacketHandled(true);
	}
}