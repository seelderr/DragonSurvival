package by.jackraidenph.dragonsurvival.network.config;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class SyncListConfig implements IMessage<SyncListConfig>
{
	public SyncListConfig() {}
	
	public String key;
	public List<String> value;
	public String type;
	
	public SyncListConfig(String key, List<String> value, String type)
	{
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	@Override
	public void encode(SyncListConfig message, FriendlyByteBuf buffer)
	{
		buffer.writeUtf(message.type);
		buffer.writeInt(message.value.size());
		message.value.forEach((val) -> buffer.writeUtf(val));
		buffer.writeUtf(message.key);
	}
	
	@Override
	public SyncListConfig decode(FriendlyByteBuf buffer)
	{
		String type = buffer.readUtf();
		int size = buffer.readInt();
		ArrayList<String> list = new ArrayList<>();
		
		for(int i = 0; i < size; i++){
			list.add(buffer.readUtf());
		}
		
		String key = buffer.readUtf();
		return new SyncListConfig(key, list, type);
	}
	
	@Override
	public void handle(SyncListConfig message, Supplier<Context> supplier)
	{
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
			if(entity == null || !entity.hasPermissions(2)) return;
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg() , new SyncListConfig(message.key, message.value, message.type));
		}
		
		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);
		
		if (ob instanceof ConfigValue) {
			ConfigValue value = (ConfigValue)ob;
			
			try {
				value.set(message.value);
				value.save();
			}catch (Exception ignored){}
		}
	}
}
