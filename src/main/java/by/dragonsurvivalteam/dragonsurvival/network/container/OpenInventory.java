package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenInventory implements IMessage<OpenInventory>{
	@Override
	public void encode(OpenInventory message, PacketBuffer buffer){

	}

	@Override
	public OpenInventory decode(PacketBuffer buffer){
		return new OpenInventory();
	}

	@Override
	public void handle(OpenInventory message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		ServerPlayerEntity player = context.getSender();

		if(player.containerMenu != null){
			player.containerMenu.removed(player);
		}

		Container container = player.inventoryMenu;

		container.addSlotListener(player);
		player.containerMenu = container;

		context.setPacketHandled(true);
	}
}