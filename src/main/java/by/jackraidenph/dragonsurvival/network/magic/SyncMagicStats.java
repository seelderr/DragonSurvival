package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.PacketProxy;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncMagicStats implements IMessage<SyncMagicStats>
{
	
	public int playerid;
	public int selectedSlot;
	public int currentMana;
	public boolean renderHotbar;
	
	public SyncMagicStats() {}
	
	public SyncMagicStats(int playerid, int selectedSlot, int currentMana, boolean renderHotbar) {
		this.playerid = playerid;
		this.currentMana = currentMana;
		this.selectedSlot = selectedSlot;
		this.renderHotbar = renderHotbar;
	}
	
	@Override
	public void encode(SyncMagicStats message, PacketBuffer buffer) {
		buffer.writeInt(message.playerid);
		buffer.writeInt(message.selectedSlot);
		buffer.writeInt(message.currentMana);
		buffer.writeBoolean(message.renderHotbar);
	}
	
	@Override
	public SyncMagicStats decode(PacketBuffer buffer) {
		int playerid = buffer.readInt();
		int selectedSlot = buffer.readInt();
		int currentMana = buffer.readInt();
		boolean renderHotbar = buffer.readBoolean();
		
		return new SyncMagicStats(playerid, selectedSlot, currentMana, renderHotbar);
	}
	
	@Override
	public void handle(SyncMagicStats message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new PacketProxy().handleMagicSync(message, supplier));
	}
}