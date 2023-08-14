package by.dragonsurvivalteam.dragonsurvival.network.dragon_editor;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncPlayerSkinPreset implements IMessage<SyncPlayerSkinPreset> {
	public int playerId;
	public SkinPreset preset;

	public SyncPlayerSkinPreset() { /* Nothing to do */ }

	public SyncPlayerSkinPreset(int playerId, final SkinPreset preset) {
		this.playerId = playerId;
		this.preset = preset;
	}

	@Override
	public void encode(final SyncPlayerSkinPreset message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.preset.writeNBT());
	}

	@Override
	public SyncPlayerSkinPreset decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();

		SkinPreset preset = new SkinPreset();
		preset.readNBT(buffer.readNbt());

		return new SyncPlayerSkinPreset(playerId, preset);
	}

	@Override
	public void handle(final SyncPlayerSkinPreset message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncPlayerSkinPreset(message));
		} else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER){
			ServerPlayer sender = context.getSender();

			if (sender != null) {
				DragonStateProvider.getCap(sender).ifPresent(handler -> {
					handler.getSkinData().skinPreset = message.preset;
					handler.getSkinData().compileSkin();
				});

				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), new SyncPlayerSkinPreset(sender.getId(), message.preset));
			}
		}

		context.setPacketHandled(true);
	}
}