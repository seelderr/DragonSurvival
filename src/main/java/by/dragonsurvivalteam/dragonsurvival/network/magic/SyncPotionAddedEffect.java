package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncPotionAddedEffect.java
	public void encode(SyncPotionAddedEffect message, FriendlyByteBuf buffer) {
=======
	public void encode(SyncPotionAddedEffect message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncPotionAddedEffect.java
		buffer.writeInt(message.entityId);
		buffer.writeInt(message.effectId);
		buffer.writeInt(message.duration);
		buffer.writeInt(message.amplifier);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncPotionAddedEffect.java
	public SyncPotionAddedEffect decode(FriendlyByteBuf buffer) {
=======
	public SyncPotionAddedEffect decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncPotionAddedEffect.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncPotionAddedEffect.java
			Player thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.entityId);
				MobEffect ef = MobEffect.byId(message.effectId);
				
=======
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.entityId);
				Effect ef = Effect.byId(message.effectId);

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncPotionAddedEffect.java
				if(ef != null){
					if(entity instanceof LivingEntity){
						((LivingEntity)entity).addEffect(new MobEffectInstance(ef, message.duration, message.amplifier));
					}
				}
			}
			context.setPacketHandled(true);
		});
	}
}