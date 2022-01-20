package by.jackraidenph.dragonsurvival.network.claw;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
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


public class SyncDragonClawRender implements IMessage<SyncDragonClawRender>
{
	public int playerId;
	public boolean state;
	
	public SyncDragonClawRender() {}
	
	public SyncDragonClawRender(int playerId, boolean state) {
		this.playerId = playerId;
		this.state = state;
	}
	
	@Override
	public void encode(SyncDragonClawRender message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}
	
	@Override
	public SyncDragonClawRender decode(FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		return new SyncDragonClawRender(playerId, state);
	}
	
	@Override
	public void handle(SyncDragonClawRender message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getClawInventory().renderClaws = message.state;
				});
				
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncDragonClawRender(entity.getId(), message.state));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void runClient(SyncDragonClawRender message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Player thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof Player) {
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