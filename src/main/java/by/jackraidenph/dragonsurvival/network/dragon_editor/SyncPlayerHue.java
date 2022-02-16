package by.jackraidenph.dragonsurvival.network.dragon_editor;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncPlayerHue implements IMessage<SyncPlayerHue>
{
	public int playerId;
	public CustomizationLayer layers;
	public Integer key;
	
	public SyncPlayerHue() {}
	
	public SyncPlayerHue(int playerId, CustomizationLayer layers, Integer key)
	{
		this.playerId = playerId;
		this.layers = layers;
		this.key = key;
	}
	
	@Override
	public void encode(SyncPlayerHue message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeEnum(message.layers);
		buffer.writeInt(message.key);
	}
	
	@Override
	public SyncPlayerHue decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		CustomizationLayer layer = buffer.readEnum(CustomizationLayer.class);
		Integer key = buffer.readInt();
		return new SyncPlayerHue(playerId, layer, key);
	}
	
	@Override
	public void handle(SyncPlayerHue message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		
		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getSkin().skinLayerHue.computeIfAbsent(dragonStateHandler.getLevel(), (b) -> new HashMap<>());
					dragonStateHandler.getSkin().skinLayerHue.getOrDefault(dragonStateHandler.getLevel(), new HashMap<>()).put(message.layers, message.key);
					dragonStateHandler.getSkin().hueChanged.addAll(Arrays.stream(CustomizationLayer.values()).distinct().collect(Collectors.toList()));
				});
				
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncPlayerHue(entity.getId(), message.layers, message.key));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void runClient(SyncPlayerHue message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof PlayerEntity) {
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getSkin().skinLayerHue.computeIfAbsent(dragonStateHandler.getLevel(), (b) -> new HashMap<>());
						dragonStateHandler.getSkin().skinLayerHue.getOrDefault(dragonStateHandler.getLevel(), new HashMap<>()).put(message.layers, message.key);
						dragonStateHandler.getSkin().hueChanged.addAll(Arrays.stream(CustomizationLayer.values()).distinct().collect(Collectors.toList()));
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}