package by.dragonsurvivalteam.dragonsurvival.network.emotes;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.EmoteCap;
import by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncEmote extends ISidedMessage<SyncEmote>{

	private CompoundNBT nbt;

	public SyncEmote(int playerId, EmoteCap cap){
		super(playerId);
		nbt = cap.writeNBT();
	}

	public SyncEmote(int playerId, CompoundNBT nbt){
		super(playerId);
		this.nbt = nbt;
	}

	public SyncEmote(){
		super(-1);
	}

	@Override
	public void encode(SyncEmote message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.nbt);
	}

	@Override
	public SyncEmote decode(PacketBuffer buffer){
		int playerId = buffer.readInt();
		CompoundNBT nbt = buffer.readNbt();
		return new SyncEmote(playerId, nbt);
	}

	@Override
	public SyncEmote create(SyncEmote message){
		return new SyncEmote(message.playerId, message.nbt);
	}

	@Override
	public void runCommon(SyncEmote message, Supplier<Context> supplier){

	}

	@Override
	public void runServer(SyncEmote message, Supplier<Context> supplier, ServerPlayerEntity sender){
		DragonStateProvider.getCap(sender).ifPresent(dragonStateHandler -> {
			dragonStateHandler.getEmotes().readNBT(message.nbt);
		});
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void runClient(SyncEmote message, Supplier<Context> supplier, PlayerEntity targetPlayer){
		DragonStateProvider.getCap(targetPlayer).ifPresent(dragonStateHandler -> {
			dragonStateHandler.getEmotes().readNBT(message.nbt);
		});
	}
}