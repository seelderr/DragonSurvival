package by.dragonsurvivalteam.dragonsurvival.network.entity;

import by.dragonsurvivalteam.dragonsurvival.common.entity.monsters.MagicalPredatorEntity;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

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
	public void encode(PacketSyncXPDevour m, PacketBuffer b){
		b.writeInt(m.entity);
		b.writeInt(m.xp);
	}

	@Override
	public PacketSyncXPDevour decode(PacketBuffer b){
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
			World world = Minecraft.getInstance().level;

			if(world != null){
				ExperienceOrbEntity xpOrb = (ExperienceOrbEntity)(world.getEntity(message.xp));
				MagicalPredatorEntity entity = (MagicalPredatorEntity)world.getEntity(message.entity);
				if(xpOrb != null && entity != null){
					entity.size += xpOrb.getValue() / 100.0F;
					entity.size = MathHelper.clamp(entity.size, 0.95F, 1.95F);
					world.addParticle(ParticleTypes.SMOKE, xpOrb.getX(), xpOrb.getY(), xpOrb.getZ(), 0, world.getRandom().nextFloat() / 12.5f, 0);
					xpOrb.remove();
				}
			}

			supplier.get().setPacketHandled(true);
		});
	}
}