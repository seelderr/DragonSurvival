package by.dragonsurvivalteam.dragonsurvival.network.status;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncMagicSourceStatus.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncMagicSourceStatus.java
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


public class SyncMagicSourceStatus implements IMessage<SyncMagicSourceStatus>{
	public int playerId;
	public boolean state;
	public int timer;

	public SyncMagicSourceStatus(){}

	public SyncMagicSourceStatus(int playerId, boolean state, int timer){
		this.playerId = playerId;
		this.state = state;
		this.timer = timer;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncMagicSourceStatus.java
	public void encode(SyncMagicSourceStatus message, FriendlyByteBuf buffer) {
=======
	public void encode(SyncMagicSourceStatus message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncMagicSourceStatus.java
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
		buffer.writeInt(message.timer);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncMagicSourceStatus.java
	public SyncMagicSourceStatus decode(FriendlyByteBuf buffer) {
=======
	public SyncMagicSourceStatus decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncMagicSourceStatus.java
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		int timer = buffer.readInt();
		return new SyncMagicSourceStatus(playerId, state, timer);
	}

	@Override
	public void handle(SyncMagicSourceStatus message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncMagicSourceStatus.java
		
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
=======

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncMagicSourceStatus.java
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getMagic().onMagicSource = message.state;
					dragonStateHandler.getMagic().magicSourceTimer = message.timer;
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncMagicSourceStatus(entity.getId(), message.state, message.timer));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncMagicSourceStatus message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/status/SyncMagicSourceStatus.java
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
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/status/SyncMagicSourceStatus.java
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getMagic().onMagicSource = message.state;
						dragonStateHandler.getMagic().magicSourceTimer = message.timer;
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}