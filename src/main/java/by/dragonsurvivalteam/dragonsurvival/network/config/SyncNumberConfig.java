package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncNumberConfig implements IMessage<SyncNumberConfig> {
	public String type;
	public String path;
	public Number value;

	public SyncNumberConfig() { /* Nothing to do */ }

	public SyncNumberConfig(final String path, final Number value) {
		this.path = path;
		this.value = value;
	}

	@Override
	public void encode(final SyncNumberConfig message, final FriendlyByteBuf buffer) {
		if (message.value instanceof Double doubleValue) {
			buffer.writeUtf("DOUBLE");
			buffer.writeDouble(doubleValue);
		} else if (message.value instanceof Long longValue) {
			buffer.writeUtf("LONG");
			buffer.writeLong(longValue);
		} else if (message.value instanceof Float floatValue) {
			buffer.writeUtf("FLOAT");
			buffer.writeFloat(floatValue);
		} else if (message.value instanceof Integer intValue) {
			buffer.writeUtf("INTEGER");
			buffer.writeInt(intValue);
		}

		buffer.writeUtf(message.path);
	}

	@Override
	public SyncNumberConfig decode(final FriendlyByteBuf buffer) {
		String type = buffer.readUtf();

		Number value = switch (type) {
            case "DOUBLE" -> buffer.readDouble();
            case "LONG" -> buffer.readLong();
            case "FLOAT" -> buffer.readFloat();
            case "INTEGER" -> buffer.readInt();
            default -> 0;
        };

        String path = buffer.readUtf();
		return new SyncNumberConfig(path, value);
	}

	@Override
	public void handle(final SyncNumberConfig message, final Supplier<NetworkEvent.Context> supplier) {
		if (supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer entity = supplier.get().getSender();

			if (entity == null || !entity.hasPermissions(2)) {
				supplier.get().setPacketHandled(true);
				return;
			}

			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncNumberConfig(message.path, message.value));
		}

		Object object = ConfigHandler.serverSpec.getValues().get(message.path);

		if (object instanceof IntValue intValue) {
			ConfigHandler.updateConfigValue(intValue, message.value.intValue());
		} else if (object instanceof DoubleValue doubleValue) {
			ConfigHandler.updateConfigValue(doubleValue, message.value.doubleValue());
		} else if (object instanceof LongValue longValue) {
			ConfigHandler.updateConfigValue(longValue, message.value.longValue());
		}

		supplier.get().setPacketHandled(true);
	}
}