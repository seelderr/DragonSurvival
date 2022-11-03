package by.dragonsurvivalteam.dragonsurvival.network.syncing;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.GenericCapabilityProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.ISidedMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonAltar;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class DragonChoiceCheck extends ISidedMessage<DragonChoiceCheck>{
	public DragonChoiceCheck(){
		super(-1);
	}

	public DragonChoiceCheck(int playerId){
		super(playerId);
	}

	@Override
	public void encode(DragonChoiceCheck message, FriendlyByteBuf buffer){
		buffer.writeInt(message.playerId);
	}

	@Override
	public DragonChoiceCheck decode(FriendlyByteBuf buffer){
		return new DragonChoiceCheck(buffer.readInt());
	}

	@Override
	public DragonChoiceCheck create(DragonChoiceCheck message){
		return new DragonChoiceCheck(message.playerId);
	}

	@Override
	public void runCommon(DragonChoiceCheck message, Supplier<Context> supplier){

	}

	@Override
	public void runServer(DragonChoiceCheck message, Supplier<Context> supplier, ServerPlayer sender){
		if(ServerConfig.startWithDragonChoice){
			GenericCapabilityProvider.getGenericCapability(sender).ifPresent(cap -> {
				if(!cap.hasUsedAltar && !DragonUtils.isDragon(sender)){
					NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new OpenDragonAltar());
				}
			});
		}
	}

	@Override
	public void runClient(DragonChoiceCheck message, Supplier<Context> supplier, Player targetPlayer){}
}