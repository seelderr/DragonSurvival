package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
 
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
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

			if(serverPlayer.containerMenu != null){
				serverPlayer.containerMenu.removed(serverPlayer);
			}

			serverPlayer.openMenu(new INamedContainerProvider(){
				@Override
				public Component getDisplayName(){
					return new TextComponent("");
				}

				@Nullable
				@Override
				public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, Player p_createMenu_3_){
					return new DragonContainer(p_createMenu_1_, p_createMenu_2_, false);
				}
			});
		}
	}
}