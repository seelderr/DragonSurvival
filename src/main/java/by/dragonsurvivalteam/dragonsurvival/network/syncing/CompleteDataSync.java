package by.dragonsurvivalteam.dragonsurvival.network.syncing;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class CompleteDataSync extends ISidedMessage<CompleteDataSync>{
	private CompoundNBT nbt;

	public CompleteDataSync(){
		super(-1);
	}

	public CompleteDataSync(int playerId){
		super(playerId);
	}

	public CompleteDataSync(PlayerEntity player){
		super(player.getId());
		DragonStateProvider.getCap(player).ifPresent((cap) -> nbt = cap.writeNBT());
	}

	public CompleteDataSync(int playerId, CompoundNBT nbt){
		super(playerId);
		this.nbt = nbt;
	}

	@Override
	public void encode(CompleteDataSync message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.nbt);
	}

	@Override
	public CompleteDataSync decode(PacketBuffer buffer){
		return new CompleteDataSync(buffer.readInt(), buffer.readNbt());
	}

	@Override
	public CompleteDataSync create(CompleteDataSync message){
		return new CompleteDataSync(message.playerId, message.nbt);
	}

	@Override
	public void runClient(CompleteDataSync message, Supplier<Context> supplier, PlayerEntity targetPlayer){
		DragonStateProvider.getCap(targetPlayer).ifPresent((cap) -> {
			cap.readNBT(message.nbt);
		});
	}

	@Override
	public void runCommon(CompleteDataSync message, Supplier<Context> supplier){

	}

	@Override
	public void runServer(CompleteDataSync message, Supplier<Context> supplier, ServerPlayerEntity sender){
		DragonStateProvider.getCap(sender).ifPresent((cap) -> {
			cap.readNBT(message.nbt);
		});

		sender.refreshDimensions();
	}
}