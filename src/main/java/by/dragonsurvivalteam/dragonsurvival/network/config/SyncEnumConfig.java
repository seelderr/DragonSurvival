package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncEnumConfig implements IMessage<SyncEnumConfig> {
	public String path;
	public Enum<?> value;

	public SyncEnumConfig() { /* Nothing to do */ }

	public SyncEnumConfig(final String path, final Enum<?> value) {
		this.path = path;
		this.value = value;
	}

	@Override
	public void encode(final SyncEnumConfig message, final FriendlyByteBuf buffer) {
		buffer.writeUtf(message.value.getDeclaringClass().getName());
		buffer.writeEnum(message.value);
		buffer.writeUtf(message.path);
	}

	@Override
	public SyncEnumConfig decode(final FriendlyByteBuf buffer) {
		String classType = buffer.readUtf();
		Enum<?> enumValue = null;

		try {
			Class<? extends Enum> cls = (Class<? extends Enum>) Class.forName(classType);
			enumValue = buffer.readEnum(cls);
		} catch (ClassNotFoundException ignored) { /* Nothing to do */ }

		String path = buffer.readUtf();
		return new SyncEnumConfig(path, enumValue);
	}

	@Override
	public void handle(final SyncEnumConfig message, final Supplier<NetworkEvent.Context> supplier) {
		if (supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer entity = supplier.get().getSender();

			if (entity == null || !entity.hasPermissions(2)) {
				supplier.get().setPacketHandled(true);
				return;
			}

			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncEnumConfig(message.path, message.value));
		}

		if (ConfigHandler.serverSpec.getValues().get(message.path) instanceof ForgeConfigSpec.EnumValue<?> enumValue) {
			ConfigHandler.updateConfigValue(enumValue, message.value);
		}

		supplier.get().setPacketHandled(true);
	}
}