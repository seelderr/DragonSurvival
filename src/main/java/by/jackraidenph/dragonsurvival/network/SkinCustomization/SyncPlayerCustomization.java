package by.jackraidenph.dragonsurvival.network.SkinCustomization;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
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

import java.util.HashMap;
import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

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
	public void encode(SyncPlayerCustomization message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeEnum(message.layers);
		buffer.writeUtf(message.key);
	}
	
	@Override
	public SyncPlayerCustomization decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		CustomizationLayer layer = buffer.readEnum(CustomizationLayer.class);
		String key = buffer.readUtf();
		return new SyncPlayerCustomization(playerId, layer, key);
	}
	
	@Override
	public void handle(SyncPlayerCustomization message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		
		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
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
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof PlayerEntity) {
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