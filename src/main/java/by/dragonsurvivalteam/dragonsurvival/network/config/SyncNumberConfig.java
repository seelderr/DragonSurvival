package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncNumberConfig implements IMessage<SyncNumberConfig>{
	public String key;
	public Double value;
	public String type;
	public SyncNumberConfig(){}

	public SyncNumberConfig(String key, double value, String type){
		this.key = key;
		this.value = value;
		this.type = type;
	}

	@Override
	public void encode(SyncNumberConfig message, PacketBuffer buffer){
		buffer.writeUtf(message.type);
		buffer.writeDouble(message.value);
		buffer.writeUtf(message.key);
	}

	@Override
	public SyncNumberConfig decode(PacketBuffer buffer){
		String type = buffer.readUtf();
		Double value = buffer.readDouble();
		String key = buffer.readUtf();
		return new SyncNumberConfig(key, value, type);
	}

	@Override
	public void handle(SyncNumberConfig message, Supplier<Context> supplier){
		ServerPlayerEntity entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)){
			return;
		}

		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);

		if(ob instanceof IntValue){
			IntValue value1 = (IntValue)ob;
			try{
				value1.set(message.value.intValue());
				value1.save();
			}catch(Exception ignored){
			}
		}else if(ob instanceof DoubleValue){
			DoubleValue value1 = (DoubleValue)ob;

			try{
				value1.set(message.value);
				value1.save();
			}catch(Exception ignored){
			}
		}else if(ob instanceof LongValue){
			LongValue value1 = (LongValue)ob;

			try{
				value1.set(message.value.longValue());
				value1.save();
			}catch(Exception ignored){
			}
		}
	}
}