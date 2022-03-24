package by.dragonsurvivalteam.dragonsurvival.network.config;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncNumberConfig.java
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
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncNumberConfig.java
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncNumberConfig implements IMessage<SyncNumberConfig>{
	public String key;
	public Double value;
	public String type;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncNumberConfig.java
	
	public SyncNumberConfig(String key, double value, String type)
	{
=======
	public SyncNumberConfig(){}

	public SyncNumberConfig(String key, double value, String type){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncNumberConfig.java
		this.key = key;
		this.value = value;
		this.type = type;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncNumberConfig.java
	public void encode(SyncNumberConfig message, FriendlyByteBuf buffer)
	{
=======
	public void encode(SyncNumberConfig message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncNumberConfig.java
		buffer.writeUtf(message.type);
		buffer.writeDouble(message.value);
		buffer.writeUtf(message.key);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncNumberConfig.java
	public SyncNumberConfig decode(FriendlyByteBuf buffer)
	{
=======
	public SyncNumberConfig decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncNumberConfig.java
		String type = buffer.readUtf();
		Double value = buffer.readDouble();
		String key = buffer.readUtf();
		return new SyncNumberConfig(key, value, type);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncNumberConfig.java
	public void handle(SyncNumberConfig message, Supplier<Context> supplier)
	{
		ServerPlayer entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)) return;
		
		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);
		
		if (ob instanceof IntValue) {
=======
	public void handle(SyncNumberConfig message, Supplier<Context> supplier){
		ServerPlayerEntity entity = supplier.get().getSender();
		if(entity == null || !entity.hasPermissions(2)){
			return;
		}

		UnmodifiableConfig spec = message.type.equalsIgnoreCase("server") ? ConfigHandler.serverSpec.getValues() : ConfigHandler.commonSpec.getValues();
		Object ob = spec.get(message.type + "." + message.key);

		if(ob instanceof IntValue){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncNumberConfig.java
			IntValue value1 = (IntValue)ob;
			try{
				value1.set(message.value.intValue());
				value1.save();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncNumberConfig.java
			}catch (Exception ignored){}
		} else if (ob instanceof DoubleValue) {
=======
			}catch(Exception ignored){
			}
		}else if(ob instanceof DoubleValue){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncNumberConfig.java
			DoubleValue value1 = (DoubleValue)ob;

			try{
				value1.set(message.value);
				value1.save();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncNumberConfig.java
			}catch (Exception ignored){}
			
		} else if (ob instanceof LongValue) {
=======
			}catch(Exception ignored){
			}
		}else if(ob instanceof LongValue){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncNumberConfig.java
			LongValue value1 = (LongValue)ob;

			try{
				value1.set(message.value.longValue());
				value1.save();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/config/SyncNumberConfig.java
			}catch (Exception ignored){}
=======
			}catch(Exception ignored){
			}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/config/SyncNumberConfig.java
		}
	}
}