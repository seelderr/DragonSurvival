package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPotionAddedEffect implements IMessage<SyncPotionAddedEffect>{

	public int entityId;
	public int effectId;
	public int duration;
	public int amplifier;

	public SyncPotionAddedEffect(){}

	public SyncPotionAddedEffect(int playerId, int effectId, int duration, int amplifier){
		this.entityId = playerId;
		this.effectId = effectId;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	@Override
	public void encode(SyncPotionAddedEffect message, PacketBuffer buffer){
		buffer.writeInt(message.entityId);
		buffer.writeInt(message.effectId);
		buffer.writeInt(message.duration);
		buffer.writeInt(message.amplifier);
	}

	@Override
	public SyncPotionAddedEffect decode(PacketBuffer buffer){
		int playerId = buffer.readInt();
		int effectId = buffer.readInt();
		int duration = buffer.readInt();
		int amplifier = buffer.readInt();

		return new SyncPotionAddedEffect(playerId, effectId, duration, amplifier);
	}

	@Override
	public void handle(SyncPotionAddedEffect message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncPotionAddedEffect message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.entityId);
				Effect ef = Effect.byId(message.effectId);

				if(ef != null){
					if(entity instanceof LivingEntity){
						((LivingEntity)entity).addEffect(new EffectInstance(ef, message.duration, message.amplifier));
					}
				}
			}
			context.setPacketHandled(true);
		});
	}
}