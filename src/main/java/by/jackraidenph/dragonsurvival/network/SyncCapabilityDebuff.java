package by.jackraidenph.dragonsurvival.network;

import by.jackraidenph.dragonsurvival.PacketProxy;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncCapabilityDebuff implements IMessage<SyncCapabilityDebuff>{

	public int playerId;
	public double timeWithoutWater;
	public int timeInDarkness;
	public int timeInRain;
	
	public SyncCapabilityDebuff() {
    }
	
	public SyncCapabilityDebuff(int playerId, double timeWithoutWater, int timeInDarkness, int timeInRain) {
        this.playerId = playerId;
        this.timeWithoutWater = timeWithoutWater;
        this.timeInDarkness = timeInDarkness;
		this.timeInRain = timeInRain;
    }
	
	@Override
	public void encode(SyncCapabilityDebuff message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeDouble(message.timeWithoutWater);
		buffer.writeInt(message.timeInDarkness);
		buffer.writeInt(message.timeInRain);
	}

	@Override
	public SyncCapabilityDebuff decode(PacketBuffer buffer) {
		return new SyncCapabilityDebuff(buffer.readInt(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
	}

	@Override
	public void handle(SyncCapabilityDebuff message, Supplier<Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new PacketProxy().handleCapabilityDebuff(message, supplier));
	}

}
