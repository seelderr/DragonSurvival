package by.dragonsurvivalteam.dragonsurvival.network.entity.player;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncCapabilityDebuff implements IMessage<SyncCapabilityDebuff>{

	public int playerId;
	public double timeWithoutWater;
	public int timeInDarkness;
	public int timeInRain;

	public SyncCapabilityDebuff(){
	}

	public SyncCapabilityDebuff(int playerId, double timeWithoutWater, int timeInDarkness, int timeInRain){
		this.playerId = playerId;
		this.timeWithoutWater = timeWithoutWater;
		this.timeInDarkness = timeInDarkness;
		this.timeInRain = timeInRain;
	}

	@Override
	public void encode(SyncCapabilityDebuff message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeDouble(message.timeWithoutWater);
		buffer.writeInt(message.timeInDarkness);
		buffer.writeInt(message.timeInRain);
	}

	@Override
	public SyncCapabilityDebuff decode(PacketBuffer buffer){
		return new SyncCapabilityDebuff(buffer.readInt(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
	}

	@Override
	public void handle(SyncCapabilityDebuff message, Supplier<Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncCapabilityDebuff message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.setDebuffData(message.timeWithoutWater, message.timeInDarkness, message.timeInRain);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}