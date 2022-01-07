package by.jackraidenph.dragonsurvival.network.config;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncListConfig implements IMessage<SyncListConfig>
{
	public SyncListConfig() {}
	
	public String key;
	public List<String> value;
	public int type;
	
	public SyncListConfig(String key, List<String> value, int type)
	{
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	@Override
	public void encode(SyncListConfig message, PacketBuffer buffer)
	{
		buffer.writeInt(message.type);
		buffer.writeInt(message.value.size());
		message.value.forEach((val) -> buffer.writeUtf(val));
		buffer.writeUtf(message.key);
	}
	
	@Override
	public SyncListConfig decode(PacketBuffer buffer)
	{
		int type = buffer.readInt();
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
		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity == null || !entity.hasPermissions(2)) return;
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg() , new SyncListConfig(message.key, message.value, message.type));
		}
		
		ForgeConfigSpec spec = message.type == 0 ? ConfigHandler.serverSpec : ConfigHandler.commonSpec;
		
		Object ob = spec.getValues().get((message.type == 0 ? "server" : "common") + "." + message.key);
		
		if (ob instanceof ConfigValue) {
			ConfigValue value = (ConfigValue)ob;
			
			try {
				value.set(message.value);
			}catch (Exception ignored){}
		}
	}
}
