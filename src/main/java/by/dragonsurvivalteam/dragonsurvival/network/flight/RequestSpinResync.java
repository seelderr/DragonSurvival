package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class RequestSpinResync implements IMessage<RequestSpinResync>{

	public RequestSpinResync(){
	}


	@Override
	public void encode(RequestSpinResync message, PacketBuffer buffer){}

	@Override
	public RequestSpinResync decode(PacketBuffer buffer){
		return new RequestSpinResync();
	}

	@Override
	public void handle(RequestSpinResync message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayerEntity entity = supplier.get().getSender();
		if(entity != null){
			DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncSpinStatus(entity.getId(), dragonStateHandler.getMovementData().spinAttack, dragonStateHandler.getMovementData().spinCooldown, dragonStateHandler.getMovementData().spinLearned));
			});
		}
	}
}