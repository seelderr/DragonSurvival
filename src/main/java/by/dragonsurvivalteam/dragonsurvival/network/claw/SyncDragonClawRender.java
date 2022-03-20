package by.dragonsurvivalteam.dragonsurvival.network.claw;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
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

public class SyncDragonClawRender implements IMessage<SyncDragonClawRender>{
	public int playerId;
	public boolean state;

	public SyncDragonClawRender(){}

	public SyncDragonClawRender(int playerId, boolean state){
		this.playerId = playerId;
		this.state = state;
	}

	@Override
	public void encode(SyncDragonClawRender message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}

	@Override
	public SyncDragonClawRender decode(PacketBuffer buffer){
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		return new SyncDragonClawRender(playerId, state);
	}

	@Override
	public void handle(SyncDragonClawRender message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getClawInventory().renderClaws = message.state;
				});

				if(ConfigHandler.SERVER.syncClawRender.get()){
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncDragonClawRender(entity.getId(), message.state));
				}
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncDragonClawRender message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getClawInventory().renderClaws = message.state;

						if(thisPlayer == entity){
							ConfigHandler.CLIENT.renderDragonClaws.set(message.state);
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}