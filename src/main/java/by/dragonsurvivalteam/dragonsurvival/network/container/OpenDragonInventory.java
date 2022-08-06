package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenDragonInventory implements IMessage<OpenDragonInventory>{
	@Override
	public void encode(OpenDragonInventory message, FriendlyByteBuf buffer){}

	@Override
	public OpenDragonInventory decode(FriendlyByteBuf buffer){
		return new OpenDragonInventory();
	}

	@Override
	public void handle(OpenDragonInventory message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayer serverPlayer = supplier.get().getSender();
		if(DragonUtils.isDragon(serverPlayer)){
			serverPlayer.containerMenu.removed(serverPlayer);
			serverPlayer.openMenu(new SimpleMenuProvider((val1, inv, player) -> new DragonContainer(val1, inv, false), new TextComponent("")));
		}
	}
}