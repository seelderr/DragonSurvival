package by.dragonsurvivalteam.dragonsurvival.network.config;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncBooleanConfig.java
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
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncBooleanConfig.java
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;


public class SyncBooleanConfig implements IMessage<SyncBooleanConfig>{
	public String key;
	public boolean value;
	public String type;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncBooleanConfig.java
	
	public SyncBooleanConfig(String key, boolean value, String type)
	{
=======
	public SyncBooleanConfig(){}

	public SyncBooleanConfig(String key, boolean value, String type){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncBooleanConfig.java
		this.key = key;
		this.value = value;
		this.type = type;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncBooleanConfig.java
	public void encode(SyncBooleanConfig message, FriendlyByteBuf buffer)
	{
=======
	public void encode(SyncBooleanConfig message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncBooleanConfig.java
		buffer.writeUtf(message.type);
		buffer.writeBoolean(message.value);
		buffer.writeUtf(message.key);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncBooleanConfig.java
	public SyncBooleanConfig decode(FriendlyByteBuf buffer)
	{
=======
	public SyncBooleanConfig decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncBooleanConfig.java
		String type = buffer.readUtf();
		boolean value = buffer.readBoolean();
		String key = buffer.readUtf();
		return new SyncBooleanConfig(key, value, type);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncBooleanConfig.java
	public void handle(SyncBooleanConfig message, Supplier<Context> supplier)
	{
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
			if(entity == null || !entity.hasPermissions(2)) return;
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg() , new SyncBooleanConfig(message.key, message.value, message.type));
		}
		
		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);
		
		if (ob instanceof BooleanValue) {
=======
	public void handle(SyncBooleanConfig message, Supplier<Context> supplier){
		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity == null || !entity.hasPermissions(2)){
				return;
			}
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncBooleanConfig(message.key, message.value, message.type));
		}

		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);

		if(ob instanceof BooleanValue){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncBooleanConfig.java
			BooleanValue booleanValue = (BooleanValue)ob;

			try{
				booleanValue.set(message.value);
				booleanValue.save();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncBooleanConfig.java
			}catch (Exception ignored){}
=======
			}catch(Exception ignored){
			}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncBooleanConfig.java
		}
	}
}