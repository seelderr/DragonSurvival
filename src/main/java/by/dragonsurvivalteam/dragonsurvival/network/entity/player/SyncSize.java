package by.dragonsurvivalteam.dragonsurvival.network.entity.player;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Synchronizes dragon level and size
 */
public class SyncSize implements IMessage<SyncSize>{

	public int playerId;
	public double size;

	public SyncSize(int playerId, double size){
		this.playerId = playerId;
		this.size = size;
	}

	public SyncSize(){

	}

	@Override
	public void encode(SyncSize message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeDouble(message.size);
	}

	@Override
	public SyncSize decode(PacketBuffer buffer){
		return new SyncSize(buffer.readInt(), buffer.readDouble());
	}

	@Override
	public void handle(SyncSize message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncSize message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(message.playerId);
			if(entity instanceof PlayerEntity){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.setSize(message.size, (PlayerEntity)entity);
				});
			}
			supplier.get().setPacketHandled(true);
		});
	}
}