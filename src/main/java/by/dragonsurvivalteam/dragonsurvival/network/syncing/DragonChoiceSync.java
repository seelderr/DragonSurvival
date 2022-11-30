package by.dragonsurvivalteam.dragonsurvival.network.syncing;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class DragonChoiceSync extends ISidedMessage<DragonChoiceSync>{
	private boolean choiceGiven;

	public DragonChoiceSync(){
		super(-1);
	}

	public DragonChoiceSync(int playerId){
		super(playerId);
	}

	public DragonChoiceSync(int playerId, boolean choiceGiven){
		super(playerId);
		this.choiceGiven = choiceGiven;
	}

	@Override
	public void encode(DragonChoiceSync message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.choiceGiven);
	}

	@Override
	public DragonChoiceSync decode(FriendlyByteBuf buffer){
		return new DragonChoiceSync(buffer.readInt(), buffer.readBoolean());
	}

	@Override
	public DragonChoiceSync create(DragonChoiceSync message){
		return new DragonChoiceSync(message.playerId, message.choiceGiven);
	}

	@Override
	public void runCommon(DragonChoiceSync message, Supplier<Context> supplier){

	}

	@Override
	public void runServer(DragonChoiceSync message, Supplier<Context> supplier, ServerPlayer sender){
		GenericCapabilityProvider.getGenericCapability(sender).hasUsedAltar = message.choiceGiven;
	}

	@Override
	public void runClient(DragonChoiceSync message, Supplier<Context> supplier, Player targetPlayer){
		GenericCapabilityProvider.getGenericCapability(targetPlayer).hasUsedAltar = message.choiceGiven;
	}
}