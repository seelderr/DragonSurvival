package by.jackraidenph.dragonsurvival.network.SkinCustomization;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
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

public class SyncPlayerAllCustomization implements IMessage<SyncPlayerAllCustomization>
{
	public int playerId;
	public HashMap<DragonLevel, HashMap<CustomizationLayer, String>> values;
	
	public SyncPlayerAllCustomization() {}
	
	public SyncPlayerAllCustomization(int playerId, HashMap<DragonLevel, HashMap<CustomizationLayer, String>> state) {
		this.playerId = playerId;
		this.values = state;
	}
	
	@Override
	public void encode(SyncPlayerAllCustomization message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		
		for(DragonLevel level : DragonLevel.values()) {
			for (CustomizationLayer layers : CustomizationLayer.values()) {
				buffer.writeUtf(message.values.getOrDefault(level, new HashMap<>()).getOrDefault(layers, SkinCap.defaultSkinValue));
			}
		}
	}
	
	@Override
	public SyncPlayerAllCustomization decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		HashMap<DragonLevel, HashMap<CustomizationLayer, String>> map = new HashMap<>();
		
		for(DragonLevel level : DragonLevel.values()) {
			for (CustomizationLayer layers : CustomizationLayer.values()) {
				map.computeIfAbsent(level, (b) -> new HashMap<>());
				map.get(level).put(layers, buffer.readUtf());
			}
		}
		return new SyncPlayerAllCustomization(playerId, map);
	}
	
	@Override
	public void handle(SyncPlayerAllCustomization message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		
		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getSkin().playerSkinLayers.clear();
					dragonStateHandler.getSkin().playerSkinLayers.putAll(message.values);
				});
				
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncPlayerAllCustomization(entity.getId(), message.values));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void runClient(SyncPlayerAllCustomization message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof PlayerEntity) {
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getSkin().playerSkinLayers.clear();
						dragonStateHandler.getSkin().playerSkinLayers.putAll(message.values);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}