package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.PacketProxy;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPotionRemovedEffect implements IMessage<SyncPotionRemovedEffect>
{
	
	public int entityId;
	public int effectId;
	
	public SyncPotionRemovedEffect() {}
	
	public SyncPotionRemovedEffect(int playerId, int effectId) {
		this.entityId = playerId;
		this.effectId = effectId;
	}
	
	@Override
	public void encode(SyncPotionRemovedEffect message, PacketBuffer buffer) {
		buffer.writeInt(message.entityId);
		buffer.writeInt(message.effectId);
	}
	
	@Override
	public SyncPotionRemovedEffect decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		int effectId = buffer.readInt();
		
		return new SyncPotionRemovedEffect(playerId, effectId);
	}
	
	@Override
	public void handle(SyncPotionRemovedEffect message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new PacketProxy().handleEndedEffect(message, supplier));
	}
}