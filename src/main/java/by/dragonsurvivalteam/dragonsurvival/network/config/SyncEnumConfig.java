package by.dragonsurvivalteam.dragonsurvival.network.config;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncEnumConfig.java
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
=======
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncEnumConfig.java
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncEnumConfig implements IMessage<SyncEnumConfig>{
	public String key;
	public Enum value;
	public String type;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncEnumConfig.java
	
	public SyncEnumConfig(String key, Enum value, String type)
	{
=======
	public SyncEnumConfig(){}

	public SyncEnumConfig(String key, Enum value, String type){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncEnumConfig.java
		this.key = key;
		this.value = value;
		this.type = type;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncEnumConfig.java
	public void encode(SyncEnumConfig message, FriendlyByteBuf buffer)
	{
=======
	public void encode(SyncEnumConfig message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncEnumConfig.java
		buffer.writeUtf(message.type);
		buffer.writeUtf(message.value.getDeclaringClass().getName());
		buffer.writeEnum(message.value);
		buffer.writeUtf(message.key);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncEnumConfig.java
	public SyncEnumConfig decode(FriendlyByteBuf buffer)
	{
=======
	public SyncEnumConfig decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncEnumConfig.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncEnumConfig.java
	public void handle(SyncEnumConfig message, Supplier<Context> supplier)
	{
		ServerPlayer entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)) return;
		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);
		
		if (ob instanceof EnumValue) {
=======
	public void handle(SyncEnumConfig message, Supplier<Context> supplier){
		ServerPlayerEntity entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)){
			return;
		}
		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);

		if(ob instanceof EnumValue){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncEnumConfig.java
			EnumValue value1 = (EnumValue)ob;

			try{
				value1.set(message.value);
				value1.save();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncEnumConfig.java
			}catch (Exception ignored){}
=======
			}catch(Exception ignored){
			}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncEnumConfig.java
		}
	}
}