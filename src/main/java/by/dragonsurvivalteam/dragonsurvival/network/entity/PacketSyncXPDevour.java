package by.dragonsurvivalteam.dragonsurvival.network.entity;

import by.dragonsurvivalteam.dragonsurvival.common.entity.monsters.MagicalPredator;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncXPDevour implements IMessage<PacketSyncXPDevour>{

	public int entity;
	public int xp;

	public PacketSyncXPDevour(){
	}

	public PacketSyncXPDevour(int entity, int xp){
		this.entity = entity;
		this.xp = xp;
	}

	@Override
	public void encode(PacketSyncXPDevour m, FriendlyByteBuf b){
		b.writeInt(m.entity);
		b.writeInt(m.xp);
	}

	@Override
	public PacketSyncXPDevour decode(FriendlyByteBuf b){
		return new PacketSyncXPDevour(b.readInt(), b.readInt());
	}

	@Override
	public void handle(PacketSyncXPDevour m, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(m, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(PacketSyncXPDevour message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Level world = Minecraft.getInstance().level;

			if(world != null){
				ExperienceOrb xpOrb = (ExperienceOrb)(world.getEntity(message.xp));
				MagicalPredator entity = (MagicalPredator)world.getEntity(message.entity);
				if(xpOrb != null && entity != null){
					entity.size += xpOrb.getValue() / 100.0F;
					entity.size = Mth.clamp(entity.size, 0.95F, 1.95F);
					world.addParticle(ParticleTypes.SMOKE, xpOrb.getX(), xpOrb.getY(), xpOrb.getZ(), 0, world.getRandom().nextFloat() / 12.5f, 0);
					xpOrb.remove(RemovalReason.DISCARDED);
				}
			}

			supplier.get().setPacketHandled(true);
		});
	}
}