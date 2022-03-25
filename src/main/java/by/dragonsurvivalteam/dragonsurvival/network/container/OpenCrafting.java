package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.server.containers.CraftingContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.FriendlyByteBuf;
 
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenCrafting implements IMessage<OpenCrafting>{
	@Override
	public void encode(OpenCrafting message, FriendlyByteBuf buffer){

	}

	@Override
	public OpenCrafting decode(FriendlyByteBuf buffer){
		return new OpenCrafting();
	}

	@Override
	public void handle(OpenCrafting message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.getSender().openMenu(new INamedContainerProvider(){
			@Override
			public Component getDisplayName(){
				return new TextComponent("Crafting");
			}

			@Override
			public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, Player p_createMenu_3_){
				return new CraftingContainer(p_createMenu_1_, p_createMenu_2_);
			}
		});
		context.setPacketHandled(true);
	}
}