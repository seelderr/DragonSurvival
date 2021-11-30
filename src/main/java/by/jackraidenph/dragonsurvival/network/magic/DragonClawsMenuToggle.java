package by.jackraidenph.dragonsurvival.network.magic;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.containers.DragonContainer;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DragonClawsMenuToggle implements IMessage<DragonClawsMenuToggle>
{
	public boolean state;
	public DragonClawsMenuToggle() {}
	
	public DragonClawsMenuToggle(boolean state) {
		this.state = state;
	}
	
	@Override
	public void encode(DragonClawsMenuToggle message, PacketBuffer buffer) {
		buffer.writeBoolean(message.state);
	}
	
	@Override
	public DragonClawsMenuToggle decode(PacketBuffer buffer) {
		boolean state = buffer.readBoolean();
		return new DragonClawsMenuToggle(state);
	}
	
	@Override
	public void handle(DragonClawsMenuToggle message, Supplier<NetworkEvent.Context> supplier) {
		ServerPlayerEntity player = supplier.get().getSender();
		
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			dragonStateHandler.clawsMenuOpen = message.state;
		});
		
		if(player.containerMenu instanceof DragonContainer){
			DragonContainer container = (DragonContainer)player.containerMenu;
			container.update();
		}
	}
}