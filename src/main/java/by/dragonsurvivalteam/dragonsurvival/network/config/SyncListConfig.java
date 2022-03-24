package by.dragonsurvivalteam.dragonsurvival.network.config;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncListConfig.java
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
=======
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncListConfig.java
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class SyncListConfig implements IMessage<SyncListConfig>{
	public String key;
	public List<String> value;
	public String type;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncListConfig.java
	
	public SyncListConfig(String key, List<String> value, String type)
	{
=======
	public SyncListConfig(){}

	public SyncListConfig(String key, List<String> value, String type){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncListConfig.java
		this.key = key;
		this.value = value;
		this.type = type;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncListConfig.java
	public void encode(SyncListConfig message, FriendlyByteBuf buffer)
	{
=======
	public void encode(SyncListConfig message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncListConfig.java
		buffer.writeUtf(message.type);
		buffer.writeInt(message.value.size());
		message.value.forEach((val) -> buffer.writeUtf(val));
		buffer.writeUtf(message.key);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncListConfig.java
	public SyncListConfig decode(FriendlyByteBuf buffer)
	{
=======
	public SyncListConfig decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncListConfig.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncListConfig.java
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
=======
	public void handle(SyncListConfig message, Supplier<Context> supplier){
		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity == null || !entity.hasPermissions(2)){
				return;
			}
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncListConfig(message.key, message.value, message.type));
		}

		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);

		if(ob instanceof ConfigValue){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncListConfig.java
			ConfigValue value = (ConfigValue)ob;

			try{
				value.set(message.value);
				value.save();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncListConfig.java
			}catch (Exception ignored){}
=======
			}catch(Exception ignored){
			}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncListConfig.java
		}
	}
}