package by.dragonsurvivalteam.dragonsurvival.network.config;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncNumberConfig implements IMessage<SyncNumberConfig.Data> {
	public static void handleClient(final SyncNumberConfig.Data message, final IPayloadContext context) {
		context.enqueueWork(() -> {
			Object object = ConfigHandler.clientSpec.getValues().get(message.path);

			if (object instanceof ModConfigSpec.IntValue intValue) {
				ConfigHandler.updateConfigValue(intValue, message.value.intValue());
			} else if (object instanceof ModConfigSpec.DoubleValue doubleValue) {
				ConfigHandler.updateConfigValue(doubleValue, message.value.doubleValue());
			} else if (object instanceof ModConfigSpec.LongValue longValue) {
				ConfigHandler.updateConfigValue(longValue, message.value.longValue());
			}
		});
	}

	public static void handleServer(final SyncNumberConfig.Data message, final IPayloadContext context) {
		Player sender = context.player();

		if (!sender.hasPermissions(2)) {
			return;
		}

		PacketDistributor.sendToAllPlayers(message);

		context.enqueueWork(() -> {
			Object object = ConfigHandler.serverSpec.getValues().get(message.path);

			if (object instanceof ModConfigSpec.IntValue intValue) {
				ConfigHandler.updateConfigValue(intValue, message.value.intValue());
			} else if (object instanceof ModConfigSpec.DoubleValue doubleValue) {
				ConfigHandler.updateConfigValue(doubleValue, message.value.doubleValue());
			} else if (object instanceof ModConfigSpec.LongValue longValue) {
				ConfigHandler.updateConfigValue(longValue, message.value.longValue());
			}
		});
	}

	public record Data(String path, Number value) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(new ResourceLocation(MODID, "number_config"));

		public static StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, Data>() {
			@Override
			public void encode(FriendlyByteBuf pBuffer, Data pValue) {
				if (pValue.value instanceof Double doubleValue) {
					pBuffer.writeUtf("DOUBLE");
					pBuffer.writeDouble(doubleValue);
				} else if (pValue.value instanceof Long longValue) {
					pBuffer.writeUtf("LONG");
					pBuffer.writeLong(longValue);
				} else if (pValue.value instanceof Float floatValue) {
					pBuffer.writeUtf("FLOAT");
					pBuffer.writeFloat(floatValue);
				} else if (pValue.value instanceof Integer intValue) {
					pBuffer.writeUtf("INTEGER");
					pBuffer.writeInt(intValue);
				}

				pBuffer.writeUtf(pValue.path);
			}

			@Override
			public Data decode(FriendlyByteBuf pBuffer) {
				String type = pBuffer.readUtf();

				Number value = switch (type) {
					case "DOUBLE" -> pBuffer.readDouble();
					case "LONG" -> pBuffer.readLong();
					case "FLOAT" -> pBuffer.readFloat();
					case "INTEGER" -> pBuffer.readInt();
					default -> 0;
				};

				String path = pBuffer.readUtf();
				return new Data(path, value);
			}
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}