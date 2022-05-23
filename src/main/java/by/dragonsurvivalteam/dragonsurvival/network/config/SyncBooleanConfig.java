package by.dragonsurvivalteam.dragonsurvival.network.config;


import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;


public class SyncBooleanConfig implements IMessage<SyncBooleanConfig>{
	public String key;
	public boolean value;

	public SyncBooleanConfig(){}

	public SyncBooleanConfig(String key, boolean value){
		this.key = key;
		this.value = value;
	}

	@Override

	public void encode(SyncBooleanConfig message, FriendlyByteBuf buffer){
		buffer.writeBoolean(message.value);
		buffer.writeUtf(message.key);
	}

	@Override

	public SyncBooleanConfig decode(FriendlyByteBuf buffer){
		boolean value = buffer.readBoolean();
		String key = buffer.readUtf();
		return new SyncBooleanConfig(key, value);
	}

	@Override

	public void handle(SyncBooleanConfig message, Supplier<NetworkEvent.Context> supplier){
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
			if(entity == null || !entity.hasPermissions(2)){
				return;
			}
			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncBooleanConfig(message.key, message.value));
		}

		if(ConfigHandler.serverSpec.getValues().get("server." + message.key) instanceof BooleanValue value){
			ConfigHandler.updateConfigValue(value, message.value);
		}
	}
}