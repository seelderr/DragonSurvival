package by.dragonsurvivalteam.dragonsurvival.network.entity.player;

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

public class SyncDragonSkinSettings implements IMessage<SyncDragonSkinSettings>{
	public int playerId;
	public boolean newborn;
	public boolean young;
	public boolean adult;

	public SyncDragonSkinSettings(){}

	public SyncDragonSkinSettings(int playerId, boolean newborn, boolean young, boolean adult){
		this.playerId = playerId;
		this.newborn = newborn;
		this.young = young;
		this.adult = adult;
	}

	@Override
	public void encode(SyncDragonSkinSettings message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.newborn);
		buffer.writeBoolean(message.young);
		buffer.writeBoolean(message.adult);
	}

	@Override
	public SyncDragonSkinSettings decode(PacketBuffer buffer){
		int playerId = buffer.readInt();
		boolean newborn = buffer.readBoolean();
		boolean young = buffer.readBoolean();
		boolean adult = buffer.readBoolean();
		return new SyncDragonSkinSettings(playerId, newborn, young, adult);
	}

	@Override
	public void handle(SyncDragonSkinSettings message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getSkin().renderNewborn = message.newborn;
					dragonStateHandler.getSkin().renderYoung = message.young;
					dragonStateHandler.getSkin().renderAdult = message.adult;
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncDragonSkinSettings(entity.getId(), message.newborn, message.young, message.adult));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncDragonSkinSettings message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getSkin().renderNewborn = message.newborn;
						dragonStateHandler.getSkin().renderYoung = message.young;
						dragonStateHandler.getSkin().renderAdult = message.adult;


						if(thisPlayer == entity){
							ConfigHandler.CLIENT.renderNewbornSkin.set(message.newborn);
							ConfigHandler.CLIENT.renderYoungSkin.set(message.young);
							ConfigHandler.CLIENT.renderAdultSkin.set(message.adult);
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}