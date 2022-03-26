package by.dragonsurvivalteam.dragonsurvival.network.emotes;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.EmoteCap;
import by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncEmote extends ISidedMessage<SyncEmote>{

	private CompoundTag nbt;

	public SyncEmote(int playerId, EmoteCap cap){
		super(playerId);
		nbt = cap.writeNBT();
	}

	public SyncEmote(int playerId, CompoundTag nbt){
		super(playerId);
		this.nbt = nbt;
	}

	public SyncEmote(){
		super(-1);
	}

	@Override
	public void encode(SyncEmote message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeNbt(message.nbt);
	}

	@Override
	public SyncEmote decode(FriendlyByteBuf buffer){
		int playerId = buffer.readInt();
		CompoundTag nbt = buffer.readNbt();
		return new SyncEmote(playerId, nbt);
	}

	@Override
	public SyncEmote create(SyncEmote message){
		return new SyncEmote(message.playerId, message.nbt);
	}

	@Override
	public void runCommon(SyncEmote message, Supplier<NetworkEvent.Context> supplier){

	}

	@Override
	public void runServer(SyncEmote message, Supplier<NetworkEvent.Context> supplier, ServerPlayer sender){
		DragonStateProvider.getCap(sender).ifPresent(dragonStateHandler -> {
			dragonStateHandler.getEmotes().readNBT(message.nbt);
		});
	}

	@OnlyIn( Dist.CLIENT )
	@Override
	public void runClient(SyncEmote message, Supplier<NetworkEvent.Context> supplier, Player targetPlayer){
		DragonStateProvider.getCap(targetPlayer).ifPresent(dragonStateHandler -> {
			dragonStateHandler.getEmotes().readNBT(message.nbt);
		});
	}
}