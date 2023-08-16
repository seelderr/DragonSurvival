package by.dragonsurvivalteam.dragonsurvival.network.player;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDragonTypeData implements IMessage<SyncDragonTypeData> {
	public int playerId;
	public CompoundTag nbt;

	public SyncDragonTypeData() { /* Nothing to do */ }

	public SyncDragonTypeData(int playerId, final CompoundTag nbt) {
		this.playerId = playerId;
		this.nbt = nbt;
	}
	
	public SyncDragonTypeData(int playerId, final AbstractDragonType nbt) {
		this.playerId = playerId;
		this.nbt = nbt.writeNBT();
	}
	
	@Override
	public void encode(final SyncDragonTypeData message, final FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.nbt);
	}

	@Override
	public SyncDragonTypeData decode(final FriendlyByteBuf buffer) {
		return new SyncDragonTypeData(buffer.readInt(), buffer.readNbt());
	}

	@Override
	public void handle(final SyncDragonTypeData message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			context.enqueueWork(() -> ClientProxy.handleSyncDragonTypeData(message));
		}

		context.setPacketHandled(true);
	}
}