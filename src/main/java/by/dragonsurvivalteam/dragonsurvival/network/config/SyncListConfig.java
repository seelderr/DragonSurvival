package by.dragonsurvivalteam.dragonsurvival.network.config;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncListConfig implements IMessage<SyncListConfig>{
	public String key;
	public List<?> value;

	public SyncListConfig(){}

	public SyncListConfig(String key, List<?> value){
		this.key = key;
		this.value = value;
	}

	@Override
	public void encode(final SyncListConfig message, final FriendlyByteBuf buffer){
		buffer.writeInt(message.value.size());

		for (Object object : message.value) {
			if (object instanceof Number number) {
				buffer.writeUtf(number.toString());
			} else {
				buffer.writeUtf(object.toString());
			}
		}

		buffer.writeUtf(message.key);
	}

	@Override

	public SyncListConfig decode(final FriendlyByteBuf buffer){
		int size = buffer.readInt();
		ArrayList<String> list = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			list.add(buffer.readUtf());
		}

		String key = buffer.readUtf();
		return new SyncListConfig(key, list);
	}

	@Override
	public void handle(final SyncListConfig message, final Supplier<NetworkEvent.Context> supplier) {
		if (supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ServerPlayer entity = supplier.get().getSender();

			if (entity == null || !entity.hasPermissions(2)) {
				supplier.get().setPacketHandled(true);
				return;
			}

			NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncListConfig(message.key, message.value));
		}

		UnmodifiableConfig spec = ConfigHandler.serverSpec.getValues();
		Object ob = spec.get("server." + message.key);

		if (ob instanceof ConfigValue<?> configValue) {
			ConfigHandler.updateConfigValue(configValue, message.value);
		}

		supplier.get().setPacketHandled(true);
	}
}