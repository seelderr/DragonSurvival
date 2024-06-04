package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

// TODO: Sort out what this is supposed to do
//public class SyncOpenInventory implements IMessage<SyncOpenInventory.Data> {
	/*@Override
	public void handle(final SyncOpenInventory.Data message, final IPayloadContext context) {
		Player sender = context.player();
		context.enqueueWork(() -> {
			if (sender.containerMenu != null) { // TODO :: container might never be null?
				sender.containerMenu.removed(sender);
			}

			InventoryMenu inventory = sender.inventoryMenu;
			sender.open
			sender.initMenu(inventory);
		});

		context.setPacketHandled(true);
	}

	public record Data() implements CustomPacketPayload {

		public static final Type<RequestOpenDragonEditor.Data> TYPE = new Type<>(new ResourceLocation(MODID, "open_dragon_editor"));

		public static final StreamCodec<ByteBuf, RequestOpenDragonEditor.Data> STREAM_CODEC = new StreamCodec<>(){
			@Override
			public void encode(ByteBuf pBuffer, RequestOpenDragonEditor.Data pValue) {}

			@Override
			public RequestOpenDragonEditor.Data decode(ByteBuf pBuffer) { return new RequestOpenDragonEditor.Data(); }
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}*/
//}