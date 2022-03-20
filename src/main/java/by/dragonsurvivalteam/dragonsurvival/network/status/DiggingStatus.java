package by.dragonsurvivalteam.dragonsurvival.network.status;

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

public class DiggingStatus implements IMessage<DiggingStatus>{
	public int playerId;
	public boolean status;

	public DiggingStatus(int playerId, boolean status){
		this.playerId = playerId;
		this.status = status;
	}

	public DiggingStatus(){
	}

	@Override
	public void encode(DiggingStatus message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.status);
	}

	@Override
	public DiggingStatus decode(PacketBuffer buffer){
		int playerId = buffer.readInt();
		boolean status = buffer.readBoolean();
		return new DiggingStatus(playerId, status);
	}

	@Override
	public void handle(DiggingStatus message, Supplier<Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void run(DiggingStatus message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getMovementData().dig = message.status;
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}