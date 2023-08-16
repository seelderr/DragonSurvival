package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class RequestSpinResync implements IMessage<RequestSpinResync>{

	public RequestSpinResync(){
	}


	@Override
	public void encode(RequestSpinResync message, FriendlyByteBuf buffer){}

	@Override
	public RequestSpinResync decode(FriendlyByteBuf buffer){
		return new RequestSpinResync();
	}

	@Override
	public void handle(RequestSpinResync message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayer entity = supplier.get().getSender();
		if(entity != null){
			DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncSpinStatus(entity.getId(), dragonStateHandler.getMovementData().spinAttack, dragonStateHandler.getMovementData().spinCooldown, dragonStateHandler.getMovementData().spinLearned));
			});
		}
	}
}