package by.dragonsurvivalteam.dragonsurvival.network;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompleteHandlerDataPacket extends by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage<CompleteHandlerDataPacket>{
	private CompoundTag nbt;

	public CompleteHandlerDataPacket(){
		super(-1);
	}

	public CompleteHandlerDataPacket(Player player){
		super(player.getId());
		DragonStateProvider.getCap(player).ifPresent((cap) -> nbt = cap.writeNBT());
	}

	public CompleteHandlerDataPacket(int playerId, CompoundTag nbt){
		super(playerId);
		this.nbt = nbt;
	}

	@Override
	public void encode(CompleteHandlerDataPacket message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.nbt);
	}

	@Override
	public CompleteHandlerDataPacket decode(FriendlyByteBuf buffer){
		return new CompleteHandlerDataPacket(buffer.readInt(), buffer.readNbt());
	}

	@Override
	public CompleteHandlerDataPacket create(CompleteHandlerDataPacket message){
		return new CompleteHandlerDataPacket(message.playerId, message.nbt);
	}

	@Override
	public void runCommon(CompleteHandlerDataPacket message, Supplier<NetworkEvent.Context> supplier){}

	@Override
	public void runServer(CompleteHandlerDataPacket message, Supplier<NetworkEvent.Context> supplier, ServerPlayer sender){
		DragonStateProvider.getCap(sender).ifPresent((cap) -> {
			cap.readNBT(message.nbt);
		});
	}

	@Override
	public void runClient(CompleteHandlerDataPacket message, Supplier<NetworkEvent.Context> supplier, Player targetPlayer){
		DragonStateProvider.getCap(targetPlayer).ifPresent((cap) -> {
			cap.readNBT(message.nbt);
		});
	}
}