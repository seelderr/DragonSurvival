package by.dragonsurvivalteam.dragonsurvival.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public abstract class ISidedMessage<T extends ISidedMessage> implements IMessage<T>{
	public int playerId;

	public ISidedMessage(int playerId){
		this.playerId = playerId;
	}

	public void handle(T message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClientThread(message, supplier));

		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				runServer(message, supplier, entity);
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), create(message));
			}
		}

		runCommon(message, supplier);
	}

	public abstract T create(T message);

	public abstract void runCommon(T message, Supplier<NetworkEvent.Context> supplier);

	public abstract void runServer(T message, Supplier<NetworkEvent.Context> supplier, ServerPlayerEntity sender);

	@OnlyIn( Dist.CLIENT )
	private void runClientThread(T message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					runClient(message, supplier, (PlayerEntity)entity);
				}
			}
			context.setPacketHandled(true);
		});
	}

	public abstract void runClient(T message, Supplier<Context> supplier, PlayerEntity targetPlayer);
}