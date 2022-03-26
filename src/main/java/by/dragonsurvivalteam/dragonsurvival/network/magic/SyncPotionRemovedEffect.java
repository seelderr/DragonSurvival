package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

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

	public void encode(SyncPotionRemovedEffect message, FriendlyByteBuf buffer){

		buffer.writeInt(message.entityId);
		buffer.writeInt(message.effectId);
	}

	@Override

	public SyncPotionRemovedEffect decode(FriendlyByteBuf buffer){

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

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.entityId);
				MobEffect ef = MobEffect.byId(message.effectId);


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