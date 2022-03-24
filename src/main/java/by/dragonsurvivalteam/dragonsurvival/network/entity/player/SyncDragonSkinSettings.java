package by.dragonsurvivalteam.dragonsurvival.network.entity.player;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;


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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
	public void encode(SyncDragonSkinSettings message, FriendlyByteBuf buffer) {
=======
	public void encode(SyncDragonSkinSettings message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.newborn);
		buffer.writeBoolean(message.young);
		buffer.writeBoolean(message.adult);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
	public SyncDragonSkinSettings decode(FriendlyByteBuf buffer) {
=======
	public SyncDragonSkinSettings decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
		int playerId = buffer.readInt();
		boolean newborn = buffer.readBoolean();
		boolean young = buffer.readBoolean();
		boolean adult = buffer.readBoolean();
		return new SyncDragonSkinSettings(playerId, newborn, young, adult);
	}

	@Override
	public void handle(SyncDragonSkinSettings message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
		
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
=======

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
			Player thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof Player) {
=======
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/entity/player/SyncDragonSkinSettings.java
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