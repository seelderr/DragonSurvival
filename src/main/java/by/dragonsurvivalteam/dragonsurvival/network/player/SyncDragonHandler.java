package by.dragonsurvivalteam.dragonsurvival.network.player;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.DRAGON_HANDLER;

import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncDragonHandler implements IMessage<SyncDragonHandler.Data> {

	public static void handleClient(final Data message, final IPayloadContext context) {
		context.enqueueWork(() -> ClientProxy.handleSynchronizeDragonCap(message));
	}

	public static void handleServer(final Data message, final IPayloadContext context) {
		PacketDistributor.sendToPlayersTrackingEntity(context.player(), message);

		context.enqueueWork(() -> {
			Player sender = context.player();
			DragonStateHandler handler = sender.getData(DRAGON_HANDLER);
			if (message.dragonType == null && handler.getType() != null) {
				DragonCommand.reInsertClawTools(sender, handler);
			}

			handler.setIsHiding(message.hiding);
			handler.setType(message.dragonType, sender);
			handler.setBody(message.dragonBody, sender);
			handler.setSize(message.size, sender);
			handler.setHasFlight(message.hasWings);
			handler.setPassengerId(message.passengerId);
		});
	}

	public record Data(int playerId, boolean hiding, AbstractDragonType dragonType, AbstractDragonBody dragonBody, double size, boolean hasWings, int passengerId) implements CustomPacketPayload
	{
		public static final Type<Data> TYPE = new Type<>(new ResourceLocation(MODID, "dragon_cap"));

		public static StreamCodec<ByteBuf, Data> STREAM_CODEC = new StreamCodec<ByteBuf, Data>() {
			@Override
			public void encode(ByteBuf pBuffer, Data pValue) {
				pBuffer.writeInt(pValue.playerId);
				Utf8String.write(pBuffer, pValue.dragonType != null ? pValue.dragonType.getSubtypeName() : "none", 32);
				Utf8String.write(pBuffer, pValue.dragonBody != null ? pValue.dragonBody.getBodyName() : "none", 32);
				pBuffer.writeBoolean(pValue.hiding);
				pBuffer.writeDouble(pValue.size);
				pBuffer.writeBoolean(pValue.hasWings);
				pBuffer.writeInt(pValue.passengerId);
			}

			@Override
			public Data decode(ByteBuf pBuffer) {
				int id = pBuffer.readInt();
				String typeS = Utf8String.read(pBuffer,32);
				String typeB = Utf8String.read(pBuffer, 32);
				AbstractDragonType type = typeS.equals("none") ? null : DragonTypes.getStaticSubtype(typeS);
				AbstractDragonBody body = typeB.equals("none") ? null : DragonBodies.getStatic(typeB);
				boolean hiding = pBuffer.readBoolean();
				double size = pBuffer.readDouble();
				boolean hasWings = pBuffer.readBoolean();
				int passengerId = pBuffer.readInt();
				return new Data(id, hiding, type, body, size, hasWings, passengerId);
			}
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}