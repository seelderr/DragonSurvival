package by.dragonsurvivalteam.dragonsurvival.network.status;


import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;


public class SyncTreasureRestStatus implements IMessage<SyncTreasureRestStatus>{
	public int playerId;
	public boolean state;

	public SyncTreasureRestStatus(){}

	public SyncTreasureRestStatus(int playerId, boolean state){
		this.playerId = playerId;
		this.state = state;
	}

	@Override

	public void encode(SyncTreasureRestStatus message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}

	@Override

	public SyncTreasureRestStatus decode(FriendlyByteBuf buffer){
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		return new SyncTreasureRestStatus(playerId, state);
	}

	@Override
	public void handle(SyncTreasureRestStatus message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));


		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();

			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					boolean update = false;

					if(message.state != dragonStateHandler.treasureResting){
						dragonStateHandler.treasureRestTimer = 0;
						dragonStateHandler.treasureSleepTimer = 0;
						update = true;
					}
					dragonStateHandler.treasureResting = message.state;

					if(update){
						((ServerLevel)entity.level).updateSleepingPlayerList();
					}
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncTreasureRestStatus(entity.getId(), message.state));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncTreasureRestStatus message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){

					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						if(message.state != dragonStateHandler.treasureResting){
							dragonStateHandler.treasureRestTimer = 0;
							dragonStateHandler.treasureSleepTimer = 0;
						}
						dragonStateHandler.treasureResting = message.state;
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}