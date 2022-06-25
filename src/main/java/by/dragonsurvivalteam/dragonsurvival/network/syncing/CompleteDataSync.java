package by.dragonsurvivalteam.dragonsurvival.network.syncing;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompleteDataSync extends ISidedMessage<CompleteDataSync>{
	private CompoundTag nbt;

	public CompleteDataSync(){
		super(-1);
	}

	public CompleteDataSync(int playerId){
		super(playerId);
	}

	public CompleteDataSync(Player player){
		super(player.getId());
		DragonStateProvider.getCap(player).ifPresent((cap) -> nbt = cap.writeNBT());
	}

	public CompleteDataSync(int playerId, CompoundTag nbt){
		super(playerId);
		this.nbt = nbt;
	}

	@Override
	public void encode(CompleteDataSync message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.nbt);
	}

	@Override
	public CompleteDataSync decode(FriendlyByteBuf buffer){
		return new CompleteDataSync(buffer.readInt(), buffer.readNbt());
	}

	@Override
	public CompleteDataSync create(CompleteDataSync message){
		return new CompleteDataSync(message.playerId, message.nbt);
	}

	@Override
	public void runCommon(CompleteDataSync message, Supplier<NetworkEvent.Context> supplier){

	}

	@Override
	public void runServer(CompleteDataSync message, Supplier<NetworkEvent.Context> supplier, ServerPlayer sender){
		DragonStateProvider.getCap(sender).ifPresent((cap) -> {
			SimpleContainer container = cap.getClawInventory().getClawsInventory();
			cap.readNBT(message.nbt);
			cap.getClawInventory().setClawsInventory(container);
		});

		sender.refreshDimensions();
	}

	@Override
	public void runClient(CompleteDataSync message, Supplier<NetworkEvent.Context> supplier, Player targetPlayer){
		DragonStateProvider.getCap(targetPlayer).ifPresent((cap) -> {
			cap.readNBT(message.nbt);
		});
	}
}