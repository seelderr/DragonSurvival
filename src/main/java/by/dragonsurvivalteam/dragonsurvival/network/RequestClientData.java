package by.dragonsurvivalteam.dragonsurvival.network;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class RequestClientData implements IMessage<RequestClientData>{
	public DragonStateHandler handler;
	public DragonType type;
	public DragonLevel level;

	public RequestClientData(DragonStateHandler handler){
		this.handler = handler;
		this.type = handler.getType();
		this.level = handler.getLevel();
	}

	public RequestClientData(DragonType type, DragonLevel level){
		this.type = type;
		this.level = level;
	}

	public RequestClientData(){}

	@Override
	public void encode(RequestClientData message, PacketBuffer buffer){
		buffer.writeEnum(message.type);
		buffer.writeEnum(message.level);
	}

	@Override
	public RequestClientData decode(PacketBuffer buffer){
		return new RequestClientData(buffer.readEnum(DragonType.class), buffer.readEnum(DragonLevel.class));
	}

	@Override
	public void handle(RequestClientData message, Supplier<Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(RequestClientData message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				ClientEvents.sendClientData(message);
			}
			context.setPacketHandled(true);
		});
	}
}