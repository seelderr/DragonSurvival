package by.dragonsurvivalteam.dragonsurvival.network.config;


import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncNumberConfig implements IMessage<SyncNumberConfig>{
	public String key;
	public Double value;

	public SyncNumberConfig(){}

	public SyncNumberConfig(String key, double value){
		this.key = key;
		this.value = value;
	}

	@Override
	public void encode(SyncNumberConfig message, FriendlyByteBuf buffer){
		buffer.writeDouble(message.value);
		buffer.writeUtf(message.key);
	}

	@Override

	public SyncNumberConfig decode(FriendlyByteBuf buffer){
		Double value = buffer.readDouble();
		String key = buffer.readUtf();
		return new SyncNumberConfig(key, value);
	}

	@Override

	public void handle(SyncNumberConfig message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayer entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)){
			supplier.get().setPacketHandled(true);
			return;
		}

		Object ob = ConfigHandler.serverSpec.getValues().get("server." + message.key);

		if(ob instanceof IntValue value){
			ConfigHandler.updateConfigValue(value, message.value.intValue());

		}else if(ob instanceof DoubleValue value){
			ConfigHandler.updateConfigValue(value, message.value.longValue());

		}else if(ob instanceof LongValue value){
			ConfigHandler.updateConfigValue(value, message.value.longValue());
		}
		supplier.get().setPacketHandled(true);
	}
}