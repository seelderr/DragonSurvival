package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler.DRAGON_HANDLER;

public class SyncOpenDragonInventory implements IMessage<SyncOpenDragonInventory.Data> {

	// FIXME: This randomly fails rarely (about 1 in 20 times) and I have no idea why.
	// This is needed since an OpenInventory message normally resets the cursor back to the center of the screen.
	// This is disruptive when navigating between various dragon menus, so we set the cursor back to prevent this behavior in that case.
	public static void SendOpenDragonInventoryAndMaintainCursorPosition() {
		ClientEvents.mouseX = Minecraft.getInstance().mouseHandler.xpos();
		ClientEvents.mouseY = Minecraft.getInstance().mouseHandler.ypos();
		PacketDistributor.sendToServer(new SyncOpenDragonInventory.Data());
	}

	public static void handleServer(final SyncOpenDragonInventory.Data message, final IPayloadContext context) {
		Player sender = context.player();
		DragonStateHandler handler = sender.getData(DRAGON_HANDLER);
		if (handler.isDragon()) {
			context.enqueueWork(() -> {
				sender.containerMenu.removed(sender);
				sender.openMenu(new SimpleMenuProvider((containerId, inventory, player) -> new DragonContainer(containerId, inventory), Component.empty()));
			});
		}
	}

	public record Data() implements CustomPacketPayload {

		public static final Type<SyncOpenDragonInventory.Data> TYPE = new Type<>(new ResourceLocation(MODID, "open_dragon_inventory"));

		public static final StreamCodec<ByteBuf, SyncOpenDragonInventory.Data> STREAM_CODEC = new StreamCodec<>(){
			@Override
			public void encode(ByteBuf pBuffer, SyncOpenDragonInventory.Data pValue) {}

			@Override
			public SyncOpenDragonInventory.Data decode(ByteBuf pBuffer) { return new SyncOpenDragonInventory.Data(); }
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}