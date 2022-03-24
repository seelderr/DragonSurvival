package by.dragonsurvivalteam.dragonsurvival.network.entity.player;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGrowthState implements IMessage<SyncGrowthState>{

	public boolean growing;

	public SyncGrowthState(boolean growing){
		this.growing = growing;
	}

	public SyncGrowthState(){

	}

	@Override
	public void encode(SyncGrowthState message, PacketBuffer buffer){
		buffer.writeBoolean(message.growing);
	}

	@Override
	public SyncGrowthState decode(PacketBuffer buffer){
		return new SyncGrowthState(buffer.readBoolean());
	}

	@Override
	public void handle(SyncGrowthState message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncGrowthState message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				DragonStateProvider.getCap(thisPlayer).ifPresent(dragonStateHandler -> {
					dragonStateHandler.growing = message.growing;
				});
			}
			context.setPacketHandled(true);
		});
	}
}