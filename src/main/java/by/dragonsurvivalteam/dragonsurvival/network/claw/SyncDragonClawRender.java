package by.dragonsurvivalteam.dragonsurvival.network.claw;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/SyncDragonClawRender.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/SyncDragonClawRender.java
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


public class SyncDragonClawRender implements IMessage<SyncDragonClawRender>{
	public int playerId;
	public boolean state;

	public SyncDragonClawRender(){}

	public SyncDragonClawRender(int playerId, boolean state){
		this.playerId = playerId;
		this.state = state;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/SyncDragonClawRender.java
	public void encode(SyncDragonClawRender message, FriendlyByteBuf buffer) {
=======
	public void encode(SyncDragonClawRender message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/SyncDragonClawRender.java
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/SyncDragonClawRender.java
	public SyncDragonClawRender decode(FriendlyByteBuf buffer) {
=======
	public SyncDragonClawRender decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/SyncDragonClawRender.java
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		return new SyncDragonClawRender(playerId, state);
	}

	@Override
	public void handle(SyncDragonClawRender message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/SyncDragonClawRender.java
		
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
=======

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/SyncDragonClawRender.java
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getClawInventory().renderClaws = message.state;
				});
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/SyncDragonClawRender.java
				
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncDragonClawRender(entity.getId(), message.state));
=======

				if(ConfigHandler.SERVER.syncClawRender.get()){
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncDragonClawRender(entity.getId(), message.state));
				}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/SyncDragonClawRender.java
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncDragonClawRender message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/SyncDragonClawRender.java
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
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/SyncDragonClawRender.java
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