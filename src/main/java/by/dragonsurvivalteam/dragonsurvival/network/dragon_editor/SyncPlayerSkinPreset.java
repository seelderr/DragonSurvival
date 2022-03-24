package by.dragonsurvivalteam.dragonsurvival.network.dragon_editor;

import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncPlayerSkinPreset implements IMessage<SyncPlayerSkinPreset>{
	public int playerId;
	public SkinPreset preset;

	public SyncPlayerSkinPreset(){}

	public SyncPlayerSkinPreset(int playerId, SkinPreset preset){
		this.playerId = playerId;
		this.preset = preset;
	}

	@Override
	public void encode(SyncPlayerSkinPreset message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.preset.writeNBT());
	}

	@Override
	public SyncPlayerSkinPreset decode(PacketBuffer buffer){
		int playerId = buffer.readInt();
		SkinPreset preset = new SkinPreset();
		preset.readNBT(buffer.readNbt());
		return new SyncPlayerSkinPreset(playerId, preset);
	}

	@Override
	public void handle(SyncPlayerSkinPreset message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));

		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getSkin().skinPreset = message.preset;
					dragonStateHandler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncPlayerSkinPreset(entity.getId(), message.preset));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncPlayerSkinPreset message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getSkin().skinPreset = message.preset;
						dragonStateHandler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}