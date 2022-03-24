package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncFlyingStatus implements IMessage<SyncFlyingStatus>{
	public int playerId;
	public boolean state;

	public SyncFlyingStatus(){
	}

	public SyncFlyingStatus(int playerId, boolean state){
		this.state = state;
		this.playerId = playerId;
	}

	@Override
	public void encode(SyncFlyingStatus message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}

	@Override
	public SyncFlyingStatus decode(PacketBuffer buffer){
		return new SyncFlyingStatus(buffer.readInt(), buffer.readBoolean());
	}

	@Override
	public void handle(SyncFlyingStatus message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.setWingsSpread(message.state);
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncFlyingStatus(entity.getId(), message.state));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncFlyingStatus message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.setWingsSpread(message.state);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}