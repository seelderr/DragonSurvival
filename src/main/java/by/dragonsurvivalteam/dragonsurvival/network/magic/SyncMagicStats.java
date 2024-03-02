package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncMagicStats implements IMessage<SyncMagicStats> {
	public int playerid;
	public int selectedSlot;
	public int currentMana;
	public boolean renderHotbar;

	public SyncMagicStats() { /* Nothing to do */ }

	public SyncMagicStats(int playerid, int selectedSlot, int currentMana, boolean renderHotbar) {
		this.playerid = playerid;
		this.currentMana = currentMana;
		this.selectedSlot = selectedSlot;
		this.renderHotbar = renderHotbar;
	}

	@Override
	public void encode(final SyncMagicStats message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerid);
		buffer.writeInt(message.selectedSlot);
		buffer.writeInt(message.currentMana);
		buffer.writeBoolean(message.renderHotbar);
	}

	@Override
	public SyncMagicStats decode(final FriendlyByteBuf buffer) {
		int playerid = buffer.readInt();
		int selectedSlot = buffer.readInt();
		int currentMana = buffer.readInt();
		boolean renderHotbar = buffer.readBoolean();

		return new SyncMagicStats(playerid, selectedSlot, currentMana, renderHotbar);
	}

	@Override
	public void handle(final SyncMagicStats message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncMagicstats(message));
		}

		context.setPacketHandled(true);
	}
}