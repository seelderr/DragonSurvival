package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.PacketProxy;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPotionAddedEffect implements IMessage<SyncPotionAddedEffect>
{
	
	public int entityId;
	public int effectId;
	public int duration;
	public int amplifier;
	
	public SyncPotionAddedEffect() {}
	
	public SyncPotionAddedEffect(int playerId, int effectId, int duration, int amplifier) {
		this.entityId = playerId;
		this.effectId = effectId;
		this.duration = duration;
		this.amplifier = amplifier;
	}
	
	@Override
	public void encode(SyncPotionAddedEffect message, PacketBuffer buffer) {
		buffer.writeInt(message.entityId);
		buffer.writeInt(message.effectId);
		buffer.writeInt(message.duration);
		buffer.writeInt(message.amplifier);
	}
	
	@Override
	public SyncPotionAddedEffect decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		int effectId = buffer.readInt();
		int duration = buffer.readInt();
		int amplifier = buffer.readInt();
		
		return new SyncPotionAddedEffect(playerId, effectId, duration, amplifier);
	}
	
	@Override
	public void handle(SyncPotionAddedEffect message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new PacketProxy().handleAddedEffect(message, supplier));
	}
}