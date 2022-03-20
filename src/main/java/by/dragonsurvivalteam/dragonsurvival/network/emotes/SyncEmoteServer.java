package by.dragonsurvivalteam.dragonsurvival.network.emotes;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncEmoteServer implements IMessage<SyncEmoteServer>{

	private String emote;

	public SyncEmoteServer(String emote){
		this.emote = emote;
	}

	public SyncEmoteServer(){
	}

	@Override
	public void encode(SyncEmoteServer message, PacketBuffer buffer){
		buffer.writeUtf(message.emote);
	}

	@Override
	public SyncEmoteServer decode(PacketBuffer buffer){
		String emote = buffer.readUtf();
		return new SyncEmoteServer(emote);
	}

	@Override
	public void handle(SyncEmoteServer message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayerEntity playerEntity = supplier.get().getSender();

		if(playerEntity == null){
			return;
		}

		DragonStateProvider.getCap(playerEntity).ifPresent(cap -> {
			cap.getEmotes().serverTick = playerEntity.tickCount;
			cap.getEmotes().serverEmote = message.emote;
		});

		NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> playerEntity), new SyncEmote(playerEntity.getId(), message.emote, 0));
	}
}