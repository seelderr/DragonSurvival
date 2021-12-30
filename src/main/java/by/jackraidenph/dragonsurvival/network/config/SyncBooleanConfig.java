package by.jackraidenph.dragonsurvival.network.config;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncBooleanConfig implements IMessage<SyncBooleanConfig>
{
	public SyncBooleanConfig() {}
	
	public String key;
	public boolean value;
	public int type;
	
	public SyncBooleanConfig(String key, boolean value, int type)
	{
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	@Override
	public void encode(SyncBooleanConfig message, PacketBuffer buffer)
	{
		buffer.writeInt(message.type);
		buffer.writeBoolean(message.value);
		buffer.writeUtf(message.key);
	}
	
	@Override
	public SyncBooleanConfig decode(PacketBuffer buffer)
	{
		int type = buffer.readInt();
		boolean value = buffer.readBoolean();
		String key = buffer.readUtf();
		return new SyncBooleanConfig(key, value, type);
	}
	
	@Override
	public void handle(SyncBooleanConfig message, Supplier<Context> supplier)
	{
		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity == null || !entity.hasPermissions(2)) return;
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg() , new SyncBooleanConfig(message.key, message.value, message.type));
		}
		
		ForgeConfigSpec spec = message.type == 0 ? ConfigHandler.serverSpec : ConfigHandler.commonSpec;
		
		Object ob = spec.getValues().get((message.type == 0 ? "server" : "common") + "." + message.key);
		
		if (ob instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue)ob;
			
			try {
				booleanValue.set(message.value);
			}catch (Exception ignored){}
		}
	}
}
