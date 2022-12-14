package by.dragonsurvivalteam.dragonsurvival.network;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestClientData implements IMessage<RequestClientData>{
	public DragonStateHandler handler;
	public AbstractDragonType type;
	public DragonLevel level;

	public RequestClientData(DragonStateHandler handler){
		this.handler = handler;
		this.type = handler.getType();
		this.level = handler.getLevel();
	}

	public RequestClientData(AbstractDragonType type, DragonLevel level){
		this.type = type;
		this.level = level;
	}

	public RequestClientData(){}

	@Override
	public void encode(RequestClientData message, FriendlyByteBuf buffer){
		buffer.writeUtf(message.type != null ? message.type.getTypeName() : "none");
		buffer.writeEnum(message.level);
	}

	@Override
	public RequestClientData decode(FriendlyByteBuf buffer){
		String type = buffer.readUtf();
		return new RequestClientData(type.equals("none") ? null : DragonTypes.getStatic(type), buffer.readEnum(DragonLevel.class));
	}

	@Override
	public void handle(RequestClientData message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(RequestClientData message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				ClientEvents.sendClientData(message);
			}
			context.setPacketHandled(true);
		});
	}
}