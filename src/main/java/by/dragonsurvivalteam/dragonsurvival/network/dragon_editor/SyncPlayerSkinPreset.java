package by.dragonsurvivalteam.dragonsurvival.network.dragon_editor;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
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


public class SyncPlayerSkinPreset implements IMessage<SyncPlayerSkinPreset>{
	public int playerId;
	public SkinPreset preset;

	public SyncPlayerSkinPreset(){}

	public SyncPlayerSkinPreset(int playerId, SkinPreset preset){
		this.playerId = playerId;
		this.preset = preset;
	}

	@Override
	public void encode(SyncPlayerSkinPreset message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.preset.writeNBT());
	}

	@Override
	public SyncPlayerSkinPreset decode(FriendlyByteBuf buffer){
		int playerId = buffer.readInt();
		SkinPreset preset = new SkinPreset();
		preset.readNBT(buffer.readNbt());
		return new SyncPlayerSkinPreset(playerId, preset);
	}

	@Override
	public void handle(SyncPlayerSkinPreset message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));

		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					dragonStateHandler.getSkin().skinPreset = message.preset;
					dragonStateHandler.getSkin().compileSkin();
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncPlayerSkinPreset(entity.getId(), message.preset));
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(SyncPlayerSkinPreset message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Player thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof Player){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						dragonStateHandler.getSkin().skinPreset = message.preset;
						dragonStateHandler.getSkin().compileSkin();
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}