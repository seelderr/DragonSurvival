package by.dragonsurvivalteam.dragonsurvival.network.emotes;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.EmoteCap;
import by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class SyncEmote extends ISidedMessage<SyncEmote> {
	private CompoundTag nbt;

	public SyncEmote(int playerId, final EmoteCap cap) {
		super(playerId);
		nbt = cap.writeNBT();
	}

	public SyncEmote(int playerId, final CompoundTag nbt) {
		super(playerId);
		this.nbt = nbt;
	}

	public SyncEmote() {
		super(-1);
	}

	@Override
	public void encode(final SyncEmote message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.nbt);
	}

	@Override
	public SyncEmote decode(final FriendlyByteBuf buffer) {
		int playerId = buffer.readInt();
		CompoundTag nbt = buffer.readNbt();
		return new SyncEmote(playerId, nbt);
	}

	@Override
	public SyncEmote create(final SyncEmote message) {
		return new SyncEmote(message.playerId, message.nbt);
	}

	@Override
	public void runCommon(SyncEmote message, NetworkEvent.Context context) { /* Nothing to do */ }

	@Override
	public void runServer(final SyncEmote message, final NetworkEvent.Context context, final ServerPlayer sender) {
		DragonStateProvider.getCap(sender).ifPresent(handler -> handler.getEmoteData().readNBT(message.nbt));
	}

	@Override
	public void runClient(final SyncEmote message, final NetworkEvent.Context context, final Player player) {
		DragonStateProvider.getCap(player).ifPresent(handler -> handler.getEmoteData().readNBT(message.nbt));
	}
}