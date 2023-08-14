package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncAltarCooldown implements IMessage<SyncAltarCooldown> {
	public int playerId;
	public int cooldown;

	public SyncAltarCooldown() { /* Nothing to do */ }

	public SyncAltarCooldown(int playerId, int cooldown) {
		this.playerId = playerId;
		this.cooldown = cooldown;
	}

	@Override
	public void encode(final SyncAltarCooldown message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeInt(message.cooldown);
	}

	@Override
	public SyncAltarCooldown decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		int cooldown = buffer.readInt();
		return new SyncAltarCooldown(playerId, cooldown);
	}

	@Override
	public void handle(final SyncAltarCooldown message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncAltarCooldown(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				DragonStateHandler handler = DragonUtils.getHandler(sender);
				handler.altarCooldown = message.cooldown;
				handler.hasUsedAltar = true;

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncAltarCooldown(sender.getId(), message.cooldown));
			}
		}

		context.setPacketHandled(true);
	}
}