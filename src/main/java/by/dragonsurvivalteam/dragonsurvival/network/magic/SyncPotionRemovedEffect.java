package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPotionRemovedEffect implements IMessage<SyncPotionRemovedEffect>{

	public int entityId;
	public int effectId;

	public SyncPotionRemovedEffect(){}

	public SyncPotionRemovedEffect(int playerId, int effectId){
		this.entityId = playerId;
		this.effectId = effectId;
	}

	@Override
	public void encode(SyncPotionRemovedEffect message, PacketBuffer buffer){
		buffer.writeInt(message.entityId);
		buffer.writeInt(message.effectId);
	}

	@Override
	public SyncPotionRemovedEffect decode(PacketBuffer buffer){
		int playerId = buffer.readInt();
		int effectId = buffer.readInt();

		return new SyncPotionRemovedEffect(playerId, effectId);
	}

	@Override
	public void handle(SyncPotionRemovedEffect message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncPotionRemovedEffect message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.entityId);
				Effect ef = Effect.byId(message.effectId);

				if(ef != null){
					if(entity instanceof LivingEntity){
						((LivingEntity)entity).removeEffect(ef);
					}
				}
			}
			context.setPacketHandled(true);
		});
	}
}