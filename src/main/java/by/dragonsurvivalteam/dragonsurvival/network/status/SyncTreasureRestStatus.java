package by.dragonsurvivalteam.dragonsurvival.network.status;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncTreasureRestStatus.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncTreasureRestStatus.java
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


public class SyncTreasureRestStatus implements IMessage<SyncTreasureRestStatus>{
	public int playerId;
	public boolean state;

	public SyncTreasureRestStatus(){}

	public SyncTreasureRestStatus(int playerId, boolean state){
		this.playerId = playerId;
		this.state = state;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncTreasureRestStatus.java
	public void encode(SyncTreasureRestStatus message, FriendlyByteBuf buffer) {
=======
	public void encode(SyncTreasureRestStatus message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncTreasureRestStatus.java
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncTreasureRestStatus.java
	public SyncTreasureRestStatus decode(FriendlyByteBuf buffer) {
=======
	public SyncTreasureRestStatus decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncTreasureRestStatus.java
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		return new SyncTreasureRestStatus(playerId, state);
	}

	@Override
	public void handle(SyncTreasureRestStatus message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncTreasureRestStatus.java
		
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
=======

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncTreasureRestStatus.java
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					if(message.state != dragonStateHandler.treasureResting){
						dragonStateHandler.treasureRestTimer = 0;
						dragonStateHandler.treasureSleepTimer = 0;
					}
					dragonStateHandler.treasureResting = message.state;
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncTreasureRestStatus(entity.getId(), message.state));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncTreasureRestStatus message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncTreasureRestStatus.java
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
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncTreasureRestStatus.java
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						if(message.state != dragonStateHandler.treasureResting){
							dragonStateHandler.treasureRestTimer = 0;
							dragonStateHandler.treasureSleepTimer = 0;
						}
						dragonStateHandler.treasureResting = message.state;
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}