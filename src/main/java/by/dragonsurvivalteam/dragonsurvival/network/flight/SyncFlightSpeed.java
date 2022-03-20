package by.dragonsurvivalteam.dragonsurvival.network.flight;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;

import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncFlightSpeed implements IMessage<SyncFlightSpeed>{
	public int playerId;
	public Vector3d flightSpeed;

	public SyncFlightSpeed(){
	}

	public SyncFlightSpeed(int playerId, Vector3d flightSpeed){
		this.playerId = playerId;
		this.flightSpeed = flightSpeed;
	}

	@Override
	public void encode(SyncFlightSpeed message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeDouble(message.flightSpeed.x);
		buffer.writeDouble(message.flightSpeed.y);
		buffer.writeDouble(message.flightSpeed.z);
	}

	@Override
	public SyncFlightSpeed decode(PacketBuffer buffer){
		return new SyncFlightSpeed(buffer.readInt(), new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
	}

	@Override
	public void handle(SyncFlightSpeed message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				entity.setDeltaMovement(message.flightSpeed);

				TargetPoint point = new TargetPoint(entity, entity.position().x, entity.position().y, entity.position().z, 32, entity.level.dimension());
				NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> point), new SyncFlightSpeed(entity.getId(), message.flightSpeed));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncFlightSpeed message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					entity.setDeltaMovement(message.flightSpeed);
				}
			}
			context.setPacketHandled(true);
		});
	}
}