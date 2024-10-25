package by.dragonsurvivalteam.dragonsurvival.network.container;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.DragonInventoryScreen;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawsMenuToggle;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
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

public class RequestOpenDragonInventory implements IMessage<RequestOpenDragonInventory.Data> {

	// TODO: This randomly fails rarely (about 1 in 20 times) and I have no idea why.
	// This is needed since an OpenInventory message normally resets the cursor back to the center of the screen.
	// This is disruptive when navigating between various dragon menus, so we set the cursor back to prevent this behavior in that case.
	public static void SendOpenDragonInventoryAndMaintainCursorPosition() {
		DragonInventoryScreen.mouseX = Minecraft.getInstance().mouseHandler.xpos();
		DragonInventoryScreen.mouseY = Minecraft.getInstance().mouseHandler.ypos();
		PacketDistributor.sendToServer(new RequestOpenDragonInventory.Data());
		DragonStateProvider.getOptional(ClientProxy.getLocalPlayer()).ifPresent(cap -> {
			boolean clawsMenuOpen = cap.getClawToolData().isMenuOpen();
			PacketDistributor.sendToServer(new SyncDragonClawsMenuToggle.Data(clawsMenuOpen));
			cap.getClawToolData().setMenuOpen(clawsMenuOpen);
		});
	}

	public static void handleServer(final RequestOpenDragonInventory.Data message, final IPayloadContext context) {
		Player sender = context.player();
		context.enqueueWork(
				() -> DragonStateProvider.getOptional(sender).ifPresent(handler -> {
				if (handler.isDragon()) {
					context.enqueueWork(() -> {
						sender.containerMenu.removed(sender);
						sender.openMenu(new SimpleMenuProvider((containerId, inventory, player) -> new DragonContainer(containerId, inventory), Component.empty()));
					});
				}
			})
		);
	}

	public record Data() implements CustomPacketPayload {

		public static final Type<RequestOpenDragonInventory.Data> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "open_dragon_inventory"));

		public static final StreamCodec<ByteBuf, RequestOpenDragonInventory.Data> STREAM_CODEC = new StreamCodec<>(){
			@Override
			public void encode(ByteBuf pBuffer, RequestOpenDragonInventory.Data pValue) {}

			@Override
			public RequestOpenDragonInventory.Data decode(ByteBuf pBuffer) { return new RequestOpenDragonInventory.Data(); }
		};

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}