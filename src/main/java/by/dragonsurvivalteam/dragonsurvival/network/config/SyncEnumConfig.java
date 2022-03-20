package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncEnumConfig implements IMessage<SyncEnumConfig>{
	public String key;
	public Enum value;
	public String type;
	public SyncEnumConfig(){}

	public SyncEnumConfig(String key, Enum value, String type){
		this.key = key;
		this.value = value;
		this.type = type;
	}

	@Override
	public void encode(SyncEnumConfig message, PacketBuffer buffer){
		buffer.writeUtf(message.type);
		buffer.writeUtf(message.value.getDeclaringClass().getName());
		buffer.writeEnum(message.value);
		buffer.writeUtf(message.key);
	}

	@Override
	public SyncEnumConfig decode(PacketBuffer buffer){
		String type = buffer.readUtf();
		String classType = buffer.readUtf();
		Enum enm = null;

		try{
			Class<? extends Enum> cls = (Class<? extends Enum>)Class.forName(classType);
			Enum value = buffer.readEnum(cls);

			if(value != null){
				enm = value;
			}
		}catch(ClassNotFoundException e){
		}

		String key = buffer.readUtf();
		return new SyncEnumConfig(key, enm, type);
	}

	@Override
	public void handle(SyncEnumConfig message, Supplier<Context> supplier){
		ServerPlayerEntity entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)){
			return;
		}
		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);

		if(ob instanceof EnumValue){
			EnumValue value1 = (EnumValue)ob;

			try{
				value1.set(message.value);
				value1.save();
			}catch(Exception ignored){
			}
		}
	}
}