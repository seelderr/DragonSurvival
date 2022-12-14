package by.dragonsurvivalteam.dragonsurvival.network.claw;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DragonClawsMenuToggle implements IMessage<DragonClawsMenuToggle>{
	public boolean state;

	public DragonClawsMenuToggle(){}

	public DragonClawsMenuToggle(boolean state){
		this.state = state;
	}

	@Override

	public void encode(DragonClawsMenuToggle message, FriendlyByteBuf buffer){

		buffer.writeBoolean(message.state);
	}

	@Override

	public DragonClawsMenuToggle decode(FriendlyByteBuf buffer){

		boolean state = buffer.readBoolean();
		return new DragonClawsMenuToggle(state);
	}

	@Override

	public void handle(DragonClawsMenuToggle message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayer player = supplier.get().getSender();


		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			dragonStateHandler.getClawToolData().setClawsMenuOpen(message.state);
		});

		if(player.containerMenu instanceof DragonContainer){
			DragonContainer container = (DragonContainer)player.containerMenu;
			container.update();
		}
	}
}