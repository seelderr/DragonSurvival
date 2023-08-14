package by.dragonsurvivalteam.dragonsurvival.network.syncing;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompleteDataSync extends ISidedMessage<CompleteDataSync> {
	private CompoundTag nbt;

	public CompleteDataSync() {
		super(-1);
	}

	public CompleteDataSync(final Player player) {
		super(player.getId());
		DragonStateProvider.getCap(player).ifPresent(cap -> nbt = cap.writeNBT());
	}

	public CompleteDataSync(int playerId, final CompoundTag nbt) {
		super(playerId);
		this.nbt = nbt;
	}

	@Override
	public void encode(final CompleteDataSync message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.nbt);
	}

	@Override
	public CompleteDataSync decode(final FriendlyByteBuf buffer) {
		return new CompleteDataSync(buffer.readInt(), buffer.readNbt());
	}

	@Override
	public CompleteDataSync create(final CompleteDataSync message) {
		return new CompleteDataSync(message.playerId, message.nbt);
	}

	@Override
	public void runCommon(CompleteDataSync message, Supplier<NetworkEvent.Context> supplier) { /* Nothing to do */ }

	@Override
	public void runServer(final CompleteDataSync message, final Supplier<NetworkEvent.Context> supplier, final ServerPlayer player) {
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			SimpleContainer container = cap.getClawToolData().getClawsInventory();
			cap.readNBT(message.nbt);
			cap.getClawToolData().setClawsInventory(container); // TODO :: Why is the old state restored?
		});

		player.refreshDimensions();
	}

	@Override
	public void runClient(final CompleteDataSync message, final Supplier<NetworkEvent.Context> supplier, final Player player) {
		DragonStateProvider.getCap(player).ifPresent(handler -> handler.readNBT(message.nbt));
	}
}