package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncBooleanConfig implements IMessage<SyncBooleanConfig> {
	public String path;
	public boolean value;

	public SyncBooleanConfig() { /* Nothing to do */ }

	public SyncBooleanConfig(final String path, boolean value) {
		this.path = path;
		this.value = value;
	}

	@Override

	public void encode(final SyncBooleanConfig message, final FriendlyByteBuf buffer) {
		buffer.writeBoolean(message.value);
		buffer.writeUtf(message.path);
	}

	@Override

	public SyncBooleanConfig decode(final FriendlyByteBuf buffer) {
		boolean value = buffer.readBoolean();
		String path = buffer.readUtf();
		return new SyncBooleanConfig(path, value);
	}

	@Override
	public void handle(final SyncBooleanConfig message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender == null || !sender.hasPermissions(2)) {
				context.setPacketHandled(true);
				return;
			}

			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncBooleanConfig(message.path, message.value));
		}

		if (ConfigHandler.serverSpec.getValues().get(message.path) instanceof BooleanValue booleanValue) {
			ConfigHandler.updateConfigValue(booleanValue, message.value);
		}

		context.setPacketHandled(true);
	}
}