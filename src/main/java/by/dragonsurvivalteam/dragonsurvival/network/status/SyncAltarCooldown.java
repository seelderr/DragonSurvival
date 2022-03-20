package by.dragonsurvivalteam.dragonsurvival.network.status;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncAltarCooldown implements IMessage<SyncAltarCooldown>{
	public int playerId;
	public int cooldown;

	public SyncAltarCooldown(){}

	public SyncAltarCooldown(int playerId, int cooldown){
		this.playerId = playerId;
		this.cooldown = cooldown;
	}

	@Override
	public void encode(SyncAltarCooldown message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeInt(message.cooldown);
	}

	@Override
	public SyncAltarCooldown decode(PacketBuffer buffer){
		int playerId = buffer.readInt();
		int cooldown = buffer.readInt();
		return new SyncAltarCooldown(playerId, cooldown);
	}

	@Override
	public void handle(SyncAltarCooldown message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.altarCooldown = message.cooldown;
					dragonStateHandler.hasUsedAltar = true;
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncAltarCooldown(entity.getId(), message.cooldown));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncAltarCooldown message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.altarCooldown = message.cooldown;
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}