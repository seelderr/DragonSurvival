package by.dragonsurvivalteam.dragonsurvival.network.claw;


import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDragonClawsMenu implements IMessage<SyncDragonClawsMenu>{
	public int playerId;
	public boolean state;

	public SimpleContainer inv;

	public SyncDragonClawsMenu(){}

	public SyncDragonClawsMenu(int playerId, boolean state, SimpleContainer inv){

		this.playerId = playerId;
		this.state = state;
		this.inv = inv;
	}

	@Override
	public void encode(SyncDragonClawsMenu message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
		CompoundTag nbt = new CompoundTag();
		nbt.put("inv", ClawInventory.saveClawInventory(message.inv));
		buffer.writeNbt(nbt);
	}

	@Override
	public SyncDragonClawsMenu decode(FriendlyByteBuf buffer){
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		CompoundTag tag = buffer.readNbt();
		SimpleContainer inventory = ClawInventory.readClawInventory(tag.getList("inv", 10));
		return new SyncDragonClawsMenu(playerId, state, inventory);
	}

	@Override
	public void handle(SyncDragonClawsMenu message, Supplier<NetworkEvent.Context> supplier){
		if (supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			runClient(message, supplier);
		} else {
			supplier.get().setPacketHandled(true);
		}
	}

	public void runClient(final SyncDragonClawsMenu message, final Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();

		context.enqueueWork(() -> {
			ClientProxy.handleSyncDragonClawsMenu(message);
			context.setPacketHandled(true);
		});
	}
}