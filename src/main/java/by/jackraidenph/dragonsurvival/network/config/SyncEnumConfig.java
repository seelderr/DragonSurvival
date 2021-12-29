package by.jackraidenph.dragonsurvival.network.config;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncEnumConfig implements IMessage<SyncEnumConfig>
{
	public SyncEnumConfig() {}
	
	public String key;
	public Enum value;
	public int type;
	
	public SyncEnumConfig(String key, Enum value, int type)
	{
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	@Override
	public void encode(SyncEnumConfig message, PacketBuffer buffer)
	{
		buffer.writeInt(message.type);
		buffer.writeUtf(message.value.getDeclaringClass().getName());
		buffer.writeEnum(message.value);
		buffer.writeUtf(message.key);
	}
	
	@Override
	public SyncEnumConfig decode(PacketBuffer buffer)
	{
		int type = buffer.readInt();
		String classType = buffer.readUtf();
		Enum enm = null;
		
		try {
			Class<? extends Enum> cls = (Class<? extends Enum>)Class.forName(classType);
			Enum value = buffer.readEnum(cls);
			
			if(value != null){
				enm = value;
			}
			
		} catch (ClassNotFoundException e) {}
		
		String key = buffer.readUtf();
		return new SyncEnumConfig(key, enm, type);
	}
	
	@Override
	public void handle(SyncEnumConfig message, Supplier<Context> supplier)
	{
		ServerPlayerEntity entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)) return;
		ForgeConfigSpec spec = message.type == 0 ? ConfigHandler.serverSpec : ConfigHandler.commonSpec;
		
		Object ob = spec.getValues().get((message.type == 0 ? "server" : "common") + "." + message.key);
		
		if (ob instanceof EnumValue) {
			EnumValue value1 = (EnumValue)ob;
			value1.set(message.value);
		}
	}
}
