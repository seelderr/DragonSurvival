package by.dragonsurvivalteam.dragonsurvival.network.entity;

import by.dragonsurvivalteam.dragonsurvival.common.entity.monsters.MagicalPredatorEntity;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncPredatorStats implements IMessage<PacketSyncPredatorStats>{

	public float size;
	public int type;
	public int id;

	public PacketSyncPredatorStats(){
	}

	public PacketSyncPredatorStats(int type, float size, int id){
		this.type = type;
		this.size = size;
		this.id = id;
	}

	@Override
	public void encode(PacketSyncPredatorStats m, PacketBuffer b){
		b.writeInt(m.type);
		b.writeFloat(m.size);
		b.writeInt(m.id);
	}

	@Override
	public PacketSyncPredatorStats decode(PacketBuffer b){
		return new PacketSyncPredatorStats(b.readInt(), b.readFloat(), b.readInt());
	}

	@Override
	public void handle(PacketSyncPredatorStats m, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(m, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(PacketSyncPredatorStats message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			World world = Minecraft.getInstance().level;

			if(world != null){
				Entity entity = world.getEntity(message.id);
				if(entity != null){
					((MagicalPredatorEntity)entity).size = message.size;
					((MagicalPredatorEntity)entity).type = message.type;
				}
			}

			supplier.get().setPacketHandled(true);
		});
	}
}