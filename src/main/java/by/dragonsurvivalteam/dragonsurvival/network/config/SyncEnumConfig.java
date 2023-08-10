package by.dragonsurvivalteam.dragonsurvival.network.config;


import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncEnumConfig implements IMessage<SyncEnumConfig>{
	public String key;
	public Enum value;

	public SyncEnumConfig(){}

	public SyncEnumConfig(String key, Enum value){
		this.key = key;
		this.value = value;
	}

	@Override

	public void encode(SyncEnumConfig message, FriendlyByteBuf buffer){
		buffer.writeUtf(message.value.getDeclaringClass().getName());
		buffer.writeEnum(message.value);
		buffer.writeUtf(message.key);
	}

	@Override

	public SyncEnumConfig decode(FriendlyByteBuf buffer){
		String classType = buffer.readUtf();
		Enum enm = null;

		try{
			Class<? extends Enum> cls = (Class<? extends Enum>)Class.forName(classType);
			Enum value = buffer.readEnum(cls);

			enm = value;
		}catch(ClassNotFoundException ignored){}

		String key = buffer.readUtf();
		return new SyncEnumConfig(key, enm);
	}

	@Override

	public void handle(SyncEnumConfig message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayer entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)){
			supplier.get().setPacketHandled(true);
			return;
		}

		if(ConfigHandler.serverSpec.getValues().get("server." + message.key) instanceof ForgeConfigSpec.EnumValue value){
			ConfigHandler.updateConfigValue(value, message.value);
		}
		supplier.get().setPacketHandled(true);
	}
}