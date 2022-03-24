package by.jackraidenph.dragonsurvival.network.SkinCustomization;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
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

import java.util.HashMap;
import java.util.function.Supplier;


public class SyncPlayerCustomization implements IMessage<SyncPlayerCustomization>
{
	public int playerId;
	public CustomizationLayer layers;
	public String key;
	
	public SyncPlayerCustomization() {}
	
	public SyncPlayerCustomization(int playerId, CustomizationLayer layers, String key)
	{
		this.playerId = playerId;
		this.layers = layers;
		this.key = key;
	}
	
	@Override
	public void encode(SyncPlayerCustomization message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeEnum(message.layers);
		buffer.writeUtf(message.key);
	}
	
	@Override
	public SyncPlayerCustomization decode(FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		CustomizationLayer layer = buffer.readEnum(CustomizationLayer.class);
		String key = buffer.readUtf();
		return new SyncPlayerCustomization(playerId, layer, key);
	}
	
	@Override
	public void handle(SyncPlayerCustomization message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getSkin().playerSkinLayers.computeIfAbsent(dragonStateHandler.getLevel(), (b) -> new HashMap<>());
					dragonStateHandler.getSkin().playerSkinLayers.getOrDefault(dragonStateHandler.getLevel(), new HashMap<>()).put(message.layers, message.key);
				});
				
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncPlayerCustomization(entity.getId(), message.layers, message.key));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void runClient(SyncPlayerCustomization message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Player thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof Player) {
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getSkin().playerSkinLayers.computeIfAbsent(dragonStateHandler.getLevel(), (b) -> new HashMap<>());
						dragonStateHandler.getSkin().playerSkinLayers.getOrDefault(dragonStateHandler.getLevel(), new HashMap<>()).put(message.layers, message.key);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}