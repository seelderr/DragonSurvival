package by.dragonsurvivalteam.dragonsurvival.network.status;


import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
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

	public void encode(SyncMagicSourceStatus message, FriendlyByteBuf buffer){

		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
		buffer.writeInt(message.timer);
	}

	@Override

	public SyncMagicSourceStatus decode(FriendlyByteBuf buffer){

		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		int timer = buffer.readInt();
		return new SyncMagicSourceStatus(playerId, state, timer);
	}

	@Override
	public void handle(SyncMagicSourceStatus message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));


		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();

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

			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){

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