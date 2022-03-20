package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncBooleanConfig implements IMessage<SyncBooleanConfig>{
	public String key;
	public boolean value;
	public String type;
	public SyncBooleanConfig(){}

	public SyncBooleanConfig(String key, boolean value, String type){
		this.key = key;
		this.value = value;
		this.type = type;
	}

	@Override
	public void encode(SyncBooleanConfig message, PacketBuffer buffer){
		buffer.writeUtf(message.type);
		buffer.writeBoolean(message.value);
		buffer.writeUtf(message.key);
	}

	@Override
	public SyncBooleanConfig decode(PacketBuffer buffer){
		String type = buffer.readUtf();
		boolean value = buffer.readBoolean();
		String key = buffer.readUtf();
		return new SyncBooleanConfig(key, value, type);
	}

	@Override
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
			BooleanValue booleanValue = (BooleanValue)ob;

			try{
				booleanValue.set(message.value);
				booleanValue.save();
			}catch(Exception ignored){
			}
		}
	}
}