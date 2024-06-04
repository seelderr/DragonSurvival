package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SyncEnumConfig implements IMessage<SyncEnumConfig.Data> {

	public static void handleServer(final SyncEnumConfig.Data message, final IPayloadContext context) {
		Player sender = context.player();

		if (sender.hasPermissions(2)) {
			return;
		}

		if (ConfigHandler.serverSpec.getValues().get(message.path) instanceof ForgeConfigSpec.EnumValue<?> enumValue) {
			context.enqueueWork(() -> ConfigHandler.updateConfigValue(enumValue, message.value));
		}

		PacketDistributor.sendToAllPlayers(message);
	}

	public static void handleClient(final SyncEnumConfig.Data message, final IPayloadContext context) {
		if (ConfigHandler.serverSpec.getValues().get(message.path) instanceof ForgeConfigSpec.EnumValue<?> enumValue) {
			context.enqueueWork(() -> ConfigHandler.updateConfigValue(enumValue, message.value));
		}
	}

	public record Data(String path, Enum<?> value) implements CustomPacketPayload {
		public static final Type<Data> TYPE = new Type<>(new ResourceLocation(MODID, "enum_config"));

		public static StreamCodec<FriendlyByteBuf, Data> STREAM_CODEC = new StreamCodec<>() {

            @Override
            public void encode(FriendlyByteBuf pBuffer, Data pValue) {
                pBuffer.writeUtf(pValue.value.getDeclaringClass().getName());
                pBuffer.writeEnum(pValue.value);
                pBuffer.writeUtf(pValue.path);
            }

            @Override
            public Data decode(FriendlyByteBuf pBuffer) {
                String classType = pBuffer.readUtf();
                Enum<?> enumValue = null;

                try {
                    Class<? extends Enum> cls = (Class<? extends Enum>) Class.forName(classType);
                    enumValue = pBuffer.readEnum(cls);
                } catch (ClassNotFoundException ignored) { /* Nothing to do */ }

                String path = pBuffer.readUtf();
                return new Data(path, enumValue);
            }
        };

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}