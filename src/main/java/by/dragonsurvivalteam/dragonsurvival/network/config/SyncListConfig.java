package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncListConfig implements IMessage<SyncListConfig.Data> {
	public static void handleServer(final SyncListConfig.Data message, final IPayloadContext context) {
		Player sender = context.player();

		if (!sender.hasPermissions(2)) {
			return;
		}

		PacketDistributor.sendToAllPlayers(message);

		UnmodifiableConfig spec = ConfigHandler.serverSpec.getValues();
		Object config = spec.get(message.path);

		if (config instanceof ConfigValue<?> configValue) {
			context.enqueueWork(() -> {
				ConfigHandler.updateConfigValue(configValue, message.value);

				// In case the config event does not get triggered
				if (message.path.startsWith("food")) {
					DragonFoodHandler.rebuildFoodMap();
				} else if (message.path.contains("SpeedupBlocks")) {
					DragonConfigHandler.rebuildSpeedupBlocksMap();
				} else if (message.path.contains("seaHydrationBlocks") || message.path.contains("seaHydrationItems")) {
					DragonConfigHandler.rebuildSeaDragonConfigs();
				} else if (message.path.contains("forestBreathGrowBlacklist")) {
					DragonConfigHandler.rebuildForestDragonConfigs();
				} else if (message.path.contains("BreathBlockBreaks")) {
					DragonConfigHandler.rebuildBreathBlocks();
				} else if (message.path.contains("DragonManaBlocks")) {
					DragonConfigHandler.rebuildManaBlocks();
				}
			});
		}
	}

	public static void handleClient(final SyncListConfig.Data message, final IPayloadContext context) {
		UnmodifiableConfig spec = ConfigHandler.serverSpec.getValues();
		Object config = spec.get(message.path);

		if (config instanceof ConfigValue<?> configValue) {
			context.enqueueWork(() -> {
				ConfigHandler.updateConfigValue(configValue, message.value);

				// In case the config event does not get triggered
				if (message.path.startsWith("food")) {
					DragonFoodHandler.rebuildFoodMap();
				} else if (message.path.contains("SpeedupBlocks")) {
					DragonConfigHandler.rebuildSpeedupBlocksMap();
				} else if (message.path.contains("seaHydrationBlocks") || message.path.contains("seaHydrationItems")) {
					DragonConfigHandler.rebuildSeaDragonConfigs();
				} else if (message.path.contains("forestBreathGrowBlacklist")) {
					DragonConfigHandler.rebuildForestDragonConfigs();
				} else if (message.path.contains("BreathBlockBreaks")) {
					DragonConfigHandler.rebuildBreathBlocks();
				} else if (message.path.contains("DragonManaBlocks")) {
					DragonConfigHandler.rebuildManaBlocks();
				}
			});
		}
	}

	public record Data(String path, List<?> value) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(new ResourceLocation(MODID, "list_config"));

		public static final StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public void encode(FriendlyByteBuf pBuffer, Data pValue) {
				pBuffer.writeUtf(pValue.path);
				pBuffer.writeInt(pValue.value.size());

				for (Object object : pValue.value) {
					if (object instanceof Number number) {
						// TODO :: Check for Integer / Double and handle those instead as actual numbers
						pBuffer.writeUtf(number.toString());
					} else {
						pBuffer.writeUtf(object.toString());
					}
				}
			}

			@Override
			public Data decode(FriendlyByteBuf pBuffer) {
				String path = pBuffer.readUtf();
				int size = pBuffer.readInt();
				ArrayList<String> list = new ArrayList<>();

				for (int i = 0; i < size; i++) {
					list.add(pBuffer.readUtf());
				}

				return new Data(path, list);
			}
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}