package by.jackraidenph.dragonsurvival.network.config;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncNumberConfig implements IMessage<SyncNumberConfig>
{
	public SyncNumberConfig() {}
	
	public String key;
	public Double value;
	public int type;
	
	public SyncNumberConfig(String key, double value, int type)
	{
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	@Override
	public void encode(SyncNumberConfig message, PacketBuffer buffer)
	{
		buffer.writeInt(message.type);
		buffer.writeDouble(message.value);
		buffer.writeUtf(message.key);
	}
	
	@Override
	public SyncNumberConfig decode(PacketBuffer buffer)
	{
		int type = buffer.readInt();
		Double value = buffer.readDouble();
		String key = buffer.readUtf();
		return new SyncNumberConfig(key, value, type);
	}
	
	@Override
	public void handle(SyncNumberConfig message, Supplier<Context> supplier)
	{
		ServerPlayerEntity entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)) return;
		ForgeConfigSpec spec = message.type == 0 ? ConfigHandler.serverSpec : ConfigHandler.commonSpec;
		
		Object ob = spec.getValues().get((message.type == 0 ? "server" : "common") + "." +  message.key);
		
		if (ob instanceof IntValue) {
			IntValue value1 = (IntValue)ob;
			try {
				value1.set(message.value.intValue());
			}catch (Exception ignored){}
		} else if (ob instanceof DoubleValue) {
			DoubleValue value1 = (DoubleValue)ob;
			
			try {
				value1.set(message.value);
			}catch (Exception ignored){}
			
		} else if (ob instanceof LongValue) {
			LongValue value1 = (LongValue)ob;
			
			try {
				value1.set(message.value.longValue());
			}catch (Exception ignored){}
		}
	}
}
