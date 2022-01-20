package by.jackraidenph.dragonsurvival.network.container;

import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenInventory implements IMessage<OpenInventory>
{
    @Override
    public void encode(OpenInventory message, FriendlyByteBuf buffer) {

    }

    @Override
    public OpenInventory decode(FriendlyByteBuf buffer) {
        return new OpenInventory();
    }

    @Override
    public void handle(OpenInventory message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context=supplier.get();
        ServerPlayer player = context.getSender();
        
        if(player.containerMenu != null){
            player.containerMenu.removed(player);
        }
        
        AbstractContainerMenu container = player.inventoryMenu;
        
        player.initMenu(container);
        player.containerMenu = container;
        
        context.setPacketHandled(true);
    }
}
